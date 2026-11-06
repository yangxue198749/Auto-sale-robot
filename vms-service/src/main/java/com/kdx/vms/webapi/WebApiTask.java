package com.kdx.vms.webapi;


import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yangshuxi on 2016/12/16.
 */
public class WebApiTask {
    public static ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
    private WebApiDao webApiDao = new WebApiDao(context);
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Integer ELEMENT_EACH_THREAD = 300;

    private Integer ELEMENT_EACH_LOOP = 3000;

//    public void syncSaleOrder() {
//        logger.debug("开始推送销售流水信息到ERP");
//        try {
//            List<TSlOrderMaster> list = webApiDao.findSaleOrderList();
//            if (null == list || list.isEmpty()) return;
//            Counter counter = new Counter(list.size(), ELEMENT_EACH_LOOP);
//            for (int i = 0; i < counter.getNum(); i++) {
//                List<TSlOrderMaster> subList = formatList(i, ELEMENT_EACH_LOOP, list);
//                Counter loopCounter = pushSaleOrderSingle(subList, i + 1);
//                counter.plus(loopCounter.getSuccess());
//            }
//            logger.debug("完成推送销售流水信息到ERP,共计" + counter.getTotal() + "条数据，成功" + counter.getSuccess() + "条");
//        } catch (Exception e) {
//            logger.error(e.getLocalizedMessage());
//            e.printStackTrace();
//        }
//    }
//
//    public void syncData() {
//        logger.debug("开始推送数据到ERP");
//        try {
//            List<TErpDataSync> list = webApiDao.findErpDataSync();
//            if (null == list || list.isEmpty()) return;
//            Counter counter = new Counter(list.size(), ELEMENT_EACH_LOOP);
//            for (int i = 0; i < counter.getNum(); i++) {
//                List<TErpDataSync> subList = formatList(i, ELEMENT_EACH_LOOP, list);
//                Counter loopCounter = pushDataSingle(subList, i + 1);
//                counter.plus(loopCounter.getSuccess());
//            }
//            logger.debug("完成推送数据到ERP,共计" + counter.getTotal() + "条数据，成功" + counter.getSuccess() + "条");
//        } catch (Exception e) {
//            logger.error(e.getLocalizedMessage());
//            e.printStackTrace();
//        }
//    }
//
//
//    private Counter pushSaleOrderSingle(List<TSlOrderMaster> list, Integer loopNum) {
//        logger.debug("开始推送销售流水信息到ERP,第" + loopNum + "次循环");
//        String url = ErpConfig.URL_PUSH_SALE_ORDER;
//        Counter counter = new Counter(list.size(), ELEMENT_EACH_THREAD);
//        Thread[] threads = new Thread[counter.getNum()];
//        final CountDownLatch latch = new CountDownLatch(counter.getNum());
//        for (int i = 0; i < counter.getNum(); i++) {
//            List<TSlOrderMaster> subList = formatList(i, ELEMENT_EACH_THREAD, list);
//            PushThread pushSaleThread = new PushSaleThread(url, subList, latch, i, counter);
//            threads[i] = new Thread(pushSaleThread);
//            threads[i].start();
//        }
//        try {
//            latch.await();
//            context.commitChanges();
//            logger.debug(",第" + loopNum + "次循环，共开启" + counter.getNum() + "个线程推送销售流水信息到ERP，共计" + counter.getTotal() + "条数据，成功" + counter.getSuccess() + "条");
//        } catch (InterruptedException e) {
//            context.rollbackChanges();
//            logger.error(e.getLocalizedMessage());
//            e.printStackTrace();
//        }
//        return counter;
//    }
//
//    private Counter pushDataSingle(List<TErpDataSync> list, Integer loopNum) {
//        logger.debug("开始推送数据到ERP,第" + loopNum + "次循环");
//        Counter counter = new Counter(list.size(), ELEMENT_EACH_THREAD);
//        Thread[] threads = new Thread[counter.getNum()];
//        final CountDownLatch latch = new CountDownLatch(counter.getNum());
//        for (int i = 0; i < counter.getNum(); i++) {
//            List<TErpDataSync> subList = formatList(i, ELEMENT_EACH_THREAD, list);
//            PushThread pushDataThread = new PushDataThread(null, subList, latch, i, counter);
//            threads[i] = new Thread(pushDataThread);
//            threads[i].start();
//        }
//        try {
//            latch.await();
//            context.commitChanges();
//            logger.debug(",第" + loopNum + "次循环，共开启" + counter.getNum() + "个线程推送销售流水信息到ERP，共计" + counter.getTotal() + "条数据，成功" + counter.getSuccess() + "条");
//        } catch (InterruptedException e) {
//            context.rollbackChanges();
//            logger.error(e.getLocalizedMessage());
//            e.printStackTrace();
//        }
//        return counter;
//    }
//
//    public Map<String, Object> searchInventory(String skuCode, String warehouseCode) {
//        logger.debug("开始查询库存，商品编码=" + skuCode + "仓库编码=" + warehouseCode);
//        String url = ErpConfig.URL_SEARCH_INVENTORY;
////        InventoryObject inventoryObject = InventoryObject.getInstance(skuCode, warehouseCode);
//        String result = null;
//        Map<String, Object> resMap = new HashMap<>();
//        try {
////            result = WebApiUtil.post(inventoryObject.getJson(), url, "库存查询");
//            if (result.contains("TRUE")) {
//                result = result.replaceAll("\\[", "").replaceAll("\\]", "");
//                JSONObject resultObject = JSON.parseObject(result);
//                String unit = resultObject.getString("fstockunitname");
//                String number = resultObject.getString("FBASEQTY");
//                resMap.put("unit", unit);
//                resMap.put("number", StringUtil.getIntValue(number, 0));
//                return resMap;
//            }
//            logger.error("调用查询接口库存失败" + result);
//        } catch (Exception e) {
//            logger.error("调用查询库存接口，解析结果失败" + result);
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public static void main(String[] args) throws IOException {
////        List<AllocationEntry> list = new ArrayList<>();
////        AllocationEntry allocationEntry1 = new AllocationEntry("14753309004",1,"CK01","CK0101");
////        list.add(allocationEntry1);
////        AllocationEntry allocationEntry2 = new AllocationEntry("14753304009",1,"CK0101","CK01");
////        list.add(allocationEntry2);
////        AllocationObject allocationObject = AllocationObject.getInstance("李冉",list);
////        String url= ErpConfig.URL_PUSH_ALLOCATION
////        WebApiUtil.post(allocationObject.getJson(),url,"库存调拨");
////        System.out.println(new WebApiTask().searchInventory("14753301006","CK02"));
//    }
//
//
//    public static <T> List<T> formatList(Integer pageNo, Integer pageSize, List<T> parentList) {
//        List<T> childObject = new ArrayList<>();
//        try {
//            if (null != parentList && parentList.size() > 0) {
//                int startIndex = (pageNo) * pageSize;
//                int totalSize = parentList.size();
//                if (startIndex < totalSize) {
//                    int size = 0;
//                    for (int i = startIndex; i < totalSize; i++) {
//                        childObject.add(parentList.get(i));
//                        size++;
//                        if (size == pageSize) {
//                            return childObject;
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return childObject;
//
//    }

}
