package com.kdx.vms;

import com.kdx.vms.model.custom.DeliveryObject;
import com.kdx.vms.model.custom.DeliveryTaskJson;
import com.kdx.vms.model.bean.TMtVmChannel;
import com.kdx.vms.model.bean.TTaskVm;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.IMcsService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 一期闭环：库存实际扣减端到端测试（依赖 MCS mock：tools/mcs_mock.py，端口 8011）。
 *
 * 通过加载应用自身的 Spring 根上下文（spring-config.xml）获取完整装配的 IMcsService，
 * 避免手动注入大量 DAO。验证真实库存变更（之前未覆盖的最后一环）：
 *  1. 构造进行中的配送工单
 *  2. 调用 deliveryTaskFinish（设备/小程序完成回调的核心逻辑）
 *  3. 断言货道 CURR_NUM 被真实更新为补后库存
 *  4. 断言工单状态变为完成(4)、完成时间写入
 *  5. MCS mock 收到设备指令通知（人工查 mcs_mock.log）
 *
 * 前置：mcs_mock.py 已启动，T_SYS_SETTINGS 的 mcs.url 指向 127.0.0.1:8011
 */
public class StockDeductionClosureTest {

    private static ClassPathXmlApplicationContext springCtx;
    private static IMcsService mcsService;

    private static final int VM_ID = 9001;
    private static final int CHANNEL_A01 = 9001; // 当前库存 3，容量 10
    private static final int CHANNEL_A02 = 9002; // 当前库存 2，容量 8
    private static final int SKU_A01 = 9001;
    private static final int SKU_A02 = 9002;

    @BeforeClass
    public static void initSpring() {
        springCtx = new ClassPathXmlApplicationContext("classpath:spring-config.xml");
        mcsService = springCtx.getBean(IMcsService.class);
        assertNotNull("IMcsService 应由 Spring 装配", mcsService);
    }

    @AfterClass
    public static void closeSpring() {
        if (springCtx != null) {
            springCtx.close();
        }
    }

    @Test
    public void testStockUpdatedOnDeliveryFinish() {
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();

        // 补货前库存
        TMtVmChannel chA01Before = Cayenne.objectForPK(context, TMtVmChannel.class, CHANNEL_A01);
        TMtVmChannel chA02Before = Cayenne.objectForPK(context, TMtVmChannel.class, CHANNEL_A02);
        int beforeA01 = chA01Before.getCurrNum();
        int beforeA02 = chA02Before.getCurrNum();
        System.out.println("[STOCK] before: A01=" + beforeA01 + ", A02=" + beforeA02);

        // 1. 构造进行中的配送工单
        TTaskVm task = context.newObject(TTaskVm.class);
        task.setCtime(new Date());
        task.setCtor(1);
        task.setTaskTitle("库存闭环测试-" + System.currentTimeMillis());
        task.setTaskType(TTaskVm.TYPE_DELIVERY);
        task.setTaskVmStatus(TTaskVm.STATUS_ONGOING);
        task.setTaskTime(new Date());
        task.setTaskLevel(1);
        task.setMtVm(Cayenne.objectForPK(context, com.kdx.vms.model.bean.TMtVm.class, VM_ID));
        context.commitChanges();
        Integer taskId = task.getTaskVmId();
        assertNotNull(taskId);

        // 2. 构造补货完成消息（模拟设备回调）：A01 补到 10，A02 补到 8
        DeliveryTaskJson taskJson = new DeliveryTaskJson();
        taskJson.setTaskId(taskId);
        List<DeliveryObject> fillList = new ArrayList<DeliveryObject>();
        fillList.add(buildFill(CHANNEL_A01, "A01", SKU_A01, beforeA01, 10));
        fillList.add(buildFill(CHANNEL_A02, "A02", SKU_A02, beforeA02, 8));
        taskJson.setFillList(fillList);
        taskJson.setCleanList(new ArrayList<DeliveryObject>());
        taskJson.setAdjustList(new ArrayList<DeliveryObject>());

        // 3. 调用核心完成逻辑（内部更新货道库存 + 完成工单 + 向 MCS mock 发通知）
        String error = mcsService.deliveryTaskFinish(taskJson);
        System.out.println("[STOCK] deliveryTaskFinish error='" + error + "'");
        assertTrue("deliveryTaskFinish 应无错误，实际: " + error, error == null || error.isEmpty());

        // 4. 新上下文断言库存真实落库
        ObjectContext verify = ObjectContextFactory.getInstance().newObjectContext();
        TMtVmChannel a01 = Cayenne.objectForPK(verify, TMtVmChannel.class, CHANNEL_A01);
        TMtVmChannel a02 = Cayenne.objectForPK(verify, TMtVmChannel.class, CHANNEL_A02);
        System.out.println("[STOCK] after: A01=" + a01.getCurrNum() + ", A02=" + a02.getCurrNum());
        assertEquals("A01 库存应更新为补后值 10", Integer.valueOf(10), a01.getCurrNum());
        assertEquals("A02 库存应更新为补后值 8", Integer.valueOf(8), a02.getCurrNum());

        // 5. 断言工单完成
        TTaskVm finished = Cayenne.objectForPK(verify, TTaskVm.class, taskId);
        assertEquals(TTaskVm.STATUS_FINISHED, (int) finished.getTaskVmStatus());
        assertNotNull("完成时间应写入", finished.getFinishTime());

        System.out.println("[STOCK] OK: stock deduction persisted, task finished, MCS notified");

        // 清理：恢复库存、删除测试工单
        ObjectContext cleanup = ObjectContextFactory.getInstance().newObjectContext();
        Cayenne.objectForPK(cleanup, TMtVmChannel.class, CHANNEL_A01).setCurrNum(beforeA01);
        Cayenne.objectForPK(cleanup, TMtVmChannel.class, CHANNEL_A02).setCurrNum(beforeA02);
        TTaskVm del = Cayenne.objectForPK(cleanup, TTaskVm.class, taskId);
        cleanup.deleteObjects(del.getTaskVmChannel().toArray());
        cleanup.deleteObject(del);
        cleanup.commitChanges();
    }

    private DeliveryObject buildFill(int channelId, String channelCode, int skuId, int currCount, int marginCount) {
        DeliveryObject o = new DeliveryObject();
        o.setChannelId(channelId);
        o.setChannelCode(channelCode);
        o.setSkuId(skuId);
        o.setType(1); // 1=补货
        o.setCurrCount(currCount);
        o.setMarginCount(marginCount); // 补后库存
        o.setClearCount(0);
        o.setTermInfo(0);
        return o;
    }
}
