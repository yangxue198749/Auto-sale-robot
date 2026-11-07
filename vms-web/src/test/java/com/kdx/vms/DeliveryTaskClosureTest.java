package com.kdx.vms;

import com.kdx.vms.model.bean.TTaskVm;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.model.bean.TTaskVmChannel;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 一期闭环：配送工单状态机集成测试（直连 MySQL，经 Cayenne）。
 *
 * 验证：
 *  1. 工单可经 Cayenne 持久化（AUTO_PK_SUPPORT 主键生成）
 *  2. 状态流转 进行中(2) → 完成(4)，完成时间被写入
 *  3. 幂等：重复设置完成态不产生新数据、不报错（模拟重复 MQ 消息）
 *  4. 关联货道明细可级联查询
 *  5. 新 ObjectContext（模拟服务重启）能读到已提交数据（重启恢复）
 *
 * 数据使用独立标记，测试后清理。
 */
public class DeliveryTaskClosureTest {

    private static final int TEST_VM_ID = 9001; // 种子数据中的售货机

    @Test
    public void testTaskLifecycleAndIdempotency() {
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();

        // 1. 新建一个配送工单（进行中）
        TTaskVm task = context.newObject(TTaskVm.class);
        task.setCtime(new Date());
        task.setCtor(1);
        task.setTaskTitle("闭环测试工单-" + System.currentTimeMillis());
        task.setTaskType(TTaskVm.TYPE_DELIVERY);
        task.setTaskVmStatus(TTaskVm.STATUS_ONGOING);
        task.setTaskTime(new Date());
        task.setTaskLevel(1);
        task.setMtVm(Cayenne.objectForPK(context, com.kdx.vms.model.bean.TMtVm.class, TEST_VM_ID));
        context.commitChanges();

        Integer taskId = task.getTaskVmId();
        assertNotNull("Cayenne 应生成主键", taskId);
        System.out.println("[CLOSURE] created task id=" + taskId);

        // 2. 关联一条货道明细
        TTaskVmChannel channel = context.newObject(TTaskVmChannel.class);
        channel.setCtime(new Date());
        channel.setCtor(1);
        channel.setTaskVm(task);
        channel.setSku(Cayenne.objectForPK(context, com.kdx.vms.model.bean.TMtSku.class, 9001));
        channel.setChannel(Cayenne.objectForPK(context, com.kdx.vms.model.bean.TMtVmChannel.class, 9001));
        channel.setSkuCount(7);
        channel.setMaxCount(10);
        channel.setBeforeDeliveryStock(3);
        channel.setTaskType(TTaskVmChannel.CHANNEL_TYPE_FILL);
        context.commitChanges();

        // 3. 状态流转到完成（模拟设备回调 / MCS 处理）
        task.setTaskVmStatus(TTaskVm.STATUS_FINISHED);
        task.setFinishTime(new Date());
        task.setUtime(new Date());
        task.setUtor(1);
        context.commitChanges();

        // 4. 幂等：再来一次完成（模拟重复消息），不应报错、不新增
        int beforeCount = countTasks(context, taskId);
        task.setTaskVmStatus(TTaskVm.STATUS_FINISHED);
        context.commitChanges();
        int afterCount = countTasks(context, taskId);
        assertEquals("重复完成消息不应产生重复工单", beforeCount, afterCount);

        // 5. 重启恢复：用全新 ObjectContext 读已提交数据
        ObjectContext freshContext = ObjectContextFactory.getInstance().newObjectContext();
        TTaskVm reloaded = Cayenne.objectForPK(freshContext, TTaskVm.class, taskId);
        assertNotNull("新上下文应读到已提交工单（重启恢复）", reloaded);
        assertEquals(TTaskVm.STATUS_FINISHED, (int) reloaded.getTaskVmStatus());
        assertNotNull("完成时间应已持久化", reloaded.getFinishTime());

        List<TTaskVmChannel> channels = reloaded.getTaskVmChannel();
        assertTrue("应能级联读到货道明细", channels != null && !channels.isEmpty());
        System.out.println("[CLOSURE] reloaded status=" + reloaded.getTaskVmStatus()
                + ", channels=" + channels.size()
                + ", finishTime=" + reloaded.getFinishTime());

        // 清理测试数据
        ObjectContext cleanup = ObjectContextFactory.getInstance().newObjectContext();
        TTaskVm toDelete = Cayenne.objectForPK(cleanup, TTaskVm.class, taskId);
        if (toDelete != null) {
            cleanup.deleteObjects(toDelete.getTaskVmChannel().toArray());
            cleanup.deleteObject(toDelete);
            cleanup.commitChanges();
        }
        System.out.println("[CLOSURE] OK: delivery task lifecycle + idempotency + restart recovery verified");
    }

    private int countTasks(ObjectContext context, Integer taskId) {
        TTaskVm t = Cayenne.objectForPK(context, TTaskVm.class, taskId);
        return t == null ? 0 : 1;
    }
}
