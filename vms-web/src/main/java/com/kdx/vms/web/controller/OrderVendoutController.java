package com.kdx.vms.web.controller;


import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.cache.CardCache;
import com.kdx.vms.commons.util.*;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.custom.OrderVendoutVo;
import com.kdx.vms.model.enumeration.OrderRefundType;
import com.kdx.vms.mq.PublisherPool;
import com.kdx.vms.orm.dao.VendoutDao;
import com.kdx.vms.service.IMtExportService;
import com.kdx.vms.service.IOrderService;
import com.kdx.vms.service.IRefundService;
import com.kdx.vms.service.VendoutService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.kdx.vms.model.bean.TMtExport.TYPE_VENDOUT_EXPORT;

/**
 * 出货流水
 *
 * Created by Zhouxing
 */
@Controller
@RequestMapping(value = "vendout")
public class OrderVendoutController {

    static String PATH = "vendout";
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private IOrderService orderService;
    @Resource
    private VendoutService vendoutService;
    @Resource
    private IRefundService iRefundService;
    @Resource
    private SearchTaskController searchTaskController;
    @Resource
    private VendoutDao vendoutDao;
    @Resource
    IMtExportService mtExportService;

    @RequestMapping(value ="home")
    public String home(HttpServletRequest request) {
        //取出payType放入CardCache中
        Map<Integer, String> map = orderService.findPayType(TokenUtil.getToken().getSysUser());
        CardCache.refresh(map);
        request.setAttribute("date", DateUtils.getTime1());
        List<TMtCardType> list = orderService.findPayTypeList(TokenUtil.getToken().getSysUser());
        list = list.stream().filter(cardType -> cardType.getTypeCode() > 0).collect(Collectors.toList());
        request.setAttribute("payTypeList", list);
        return "vendout/list";
    }

    @RequestMapping("table")
    public String table(HttpServletRequest request, Integer pageSize, Integer pageNo, Integer searchType, String searchValue,
                        String skuName, Integer payType, Integer outStatus, String beginTime, String endTime,
                        @CookieValue(value = "pageSize", required = false) Integer cookiePageSize) {
        SSOToken token = TokenUtil.getToken();
        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 10;
        if (StringUtil.isEmpty(beginTime) && StringUtil.isEmpty(endTime)) {
            beginTime = endTime = DateUtils.getTime1();
        }
        List totalList = orderService.findVendoutList(token.getSysUser(), searchType, searchValue, skuName, payType, outStatus, beginTime, endTime, pageSize);
        /*List<OrderVendoutVo> subList = PageUtil.formatList(pageNo, pageSize, totalList)
                .stream().map(TOrderDetail.class::cast).map(OrderVendoutVo::of).collect(Collectors.toList());*/

        String url = PATH + "/table";
        Integer totalSize = totalList.size();
        List subList = PageUtil.formatList(pageNo, pageSize, totalList);
        try {
            request.setAttribute("url", url);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", totalSize);
            request.setAttribute("subList", subList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "vendout/table/table";
    }

    @RequestMapping("findMemo")
    public String findMemoById(HttpServletRequest request,String detailId){
        try {
            SSOToken token = TokenUtil.getToken();
            TOrderDetail tOrderDetail = vendoutDao.findById(token.getObjectContext(),detailId);
            request.setAttribute("detail",tOrderDetail);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return "vendout/dialog/add_memo_dialog";
    }

    /**
     * 修改备注
     * @param detailId
     * @param memo
     * @return
     */
    @RequestMapping("memo")
    @ResponseBody
    public Map<String, Object> updateMemoById(String detailId, String memo){
        try {
            SSOToken token = TokenUtil.getToken();
            return vendoutService.updateMemoById(token.getObjectContext() ,detailId ,memo);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return Collections.EMPTY_MAP;
    }

    /**
     * 退款
     * @param detailId
     * @return
     */
    @RequestMapping("refund")
    @ResponseBody
    public Map<String, Object> refundByDetailId(Integer detailId){
        try {
            SSOToken token = TokenUtil.getToken();
            TSysUser currentUser = token.getSysUser();
            logger.info(currentUser.getUserAccount() + "发起退款，orderDetailId=" + detailId);
            Assert.notNull(token, "系统错误：TokenUtil.getToken() -> null。");
            TOrderMaster tOrderMaster = vendoutService.findByDetailId(token.getObjectContext(),detailId);
            String topic = getTopic(tOrderMaster);
            Assert.notNull(topic, "获取topic错误。");
            TOrderRefund refund = iRefundService.launchRefund(currentUser, OrderRefundType.VMS_GOOD, topic,detailId);
            if(Objects.nonNull(refund)){
                return ReturnCode.SUCCESS.toMap();
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return ReturnCode.FAIL.toMap();
    }

    /**
     * 获取主题
     * @param tOrderMaster
     * @return
     */
    private String getTopic(TOrderMaster tOrderMaster) {
        if (Objects.isNull(tOrderMaster)) return null;
        switch (tOrderMaster.getOrderType()) {
            case TOrderMaster.RESERVE:
                return (String) PropertiesLoader.map.get("reserveRefundTopic");
            case TOrderMaster.IMMEDIATE:
                return (String) PropertiesLoader.map.get("immediateRefundTopic");
        }
        return null;
    }

    /**
     * 导出售货机数据
     * @param
     */
    @RequestMapping("exportVendout")
    @ResponseBody
    public Map<String, Object> exportDetailVendout(Integer searchType, String searchValue,
                                                   String skuName, Integer payType, Integer outStatus, String beginTime, String endTime
                                                   ) {
        logger.info("[exportVendout]");
        Map<String,Object> map = new HashMap<>();

        try {
            map.put("type","ok");
            map.put("message","导出成功，请稍后前往报表下载处下载");
            // 从缓存中得到当前用户TSysUser
            SSOToken token = TokenUtil.getToken();
            TSysUser sysUser = token.getSysUser();
            Integer companyId =sysUser.getCompany().getId();
            if (StringUtil.isEmpty(beginTime) && StringUtil.isEmpty(endTime)) {
                beginTime = endTime = DateUtils.getTime1();
            }
            if(DateUtils.daysBetween(beginTime,endTime)>30){
                map.put("type","error");
                map.put("message", "导出数据范围最大31天");
                return  map;
            }
            String condition= companyId+"_"+beginTime+"_"+endTime + "_"+TMtExport.TYPE_VENDOUT_EXPORT;
            TMtExport exportCondition = mtExportService.findExportByMatchCondition(sysUser.getObjectContext(),condition);
            if(exportCondition != null){
                map.put("type","error");
                map.put("message", "导出文件存在，请稍后前往报表下载处下载");
                return  map;
            }
            TMtExport export = sysUser.getObjectContext().newObject(TMtExport.class);
            export.setType(TMtExport.TYPE_ORDER);
            export.setCompanyId(companyId);
            export.setCreateTime(new Date());
            export.setCtor(sysUser.getId());
            export.setCondition(condition);
            sysUser.getObjectContext().commitChanges();

            Map<String,Object> message = bulidOrderVendoutMessage(sysUser, TYPE_VENDOUT_EXPORT, export.getId(), beginTime, endTime);
            PublisherPool.getInstance().publish((String) PropertiesLoader.map.get("payExchangeName"), JSON.toJSONString(message), (String) PropertiesLoader.map.get("exportRequestTopic"));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            map.put("type","error");
            map.put("message","导出失败，请重试");
        }
        return map;
    }

    private Map<String, Object> bulidOrderVendoutMessage(TSysUser sysUser,Integer type, Integer exportId, String beginTime, String endTime) {
        Map<String, Object> head = new HashMap<>();
        head.put("id", UUID.randomUUID().toString());
        head.put("ts", System.currentTimeMillis());
        Map<String, Object> body = new HashMap<>();
        body.put("userId", sysUser.getId());
        body.put("beginTime", beginTime);
        body.put("endTime", endTime);
        body.put("exportId",exportId);
        body.put("type",type);
        Map<String, Object> message = new HashMap<>();
        message.put("head", head);
        message.put("body", body);
        return message;
    }

}