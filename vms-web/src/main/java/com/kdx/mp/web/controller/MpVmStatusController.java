package com.kdx.mp.web.controller;

import com.kdx.mp.service.IMpVmStatusService;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.MyJedisUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.custom.MpJsonObject;
import com.kdx.vms.model.custom.MpVmStatus;
import com.kdx.vms.model.custom.VmStockInfo;
import com.kdx.vms.service.IMcsService;
import com.kdx.vms.web.oauth.UserMap;
import com.kdx.vms.web.tags.VmStatusDetailUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.query.ObjectIdQuery;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yangshuxi on 2016/12/28.
 */
@Controller
@RequestMapping("mp/vmStatus")
public class MpVmStatusController {
    @Autowired
    private IMpVmStatusService mpVmStatusService;
    @Autowired
    private IMcsService mcsService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 没有权限地址
     **/
    private String unauthorizedUrl = "/mp/auth/unauthorized";

    @RequestMapping("list")
    public String list(HttpServletRequest request) {
//        TSysUser currentUser= (TSysUser) request.getSession().getAttribute("mpUser");
        try {
//            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
//            ObjectContext context = currentUser.getObjectContext();
//            List<TMtVm> vmList = mpVmStatusService.findVmList(context, currentUser);
//            Collections.sort(vmList);
//            int total = vmList.size();
//            long bad = vmList.parallelStream().filter(mtVm -> StringUtil.getIntValue(mtVm.getVmcStatus(), 0) + mtVm.getStockStatus() + mtVm.getNetworkStatus() > 0).count();
//            request.setAttribute("vmList", vmList);
//            request.setAttribute("total", total);
//            request.setAttribute("bad", bad);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mp/device/device_status_list";
    }
    @ResponseBody
    @RequestMapping(value = "load", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String load(HttpServletRequest request,String innerCode) {
        try {
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            ObjectContext context = currentUser.getObjectContext();
            List<TMtVm> vmList = mpVmStatusService.findVmList(currentUser,innerCode);
//            Collections.sort(vmList);
            int total = vmList.size();
            long bad = vmList.parallelStream().filter(mtVm -> StringUtil.getIntValue(mtVm.getVmcStatus(), 0) + mtVm.getStockStatus() + mtVm.getNetworkStatus() > 0).count();
            List<MpVmStatus> list = vmList.parallelStream().map(MpVmStatus::of).sorted().collect(Collectors.toList());
            MpJsonObject<MpVmStatus> mpJsonObject = new MpJsonObject<>(MpJsonObject.TYPE_OK, "", list);
            mpJsonObject.addSummary("total", total);
            mpJsonObject.addSummary("bad", bad);
            return mpJsonObject.toJson();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return new MpJsonObject<>(MpJsonObject.TYPE_ERROR, "加载失败", Collections.emptyList()).toJson();
    }


    @RequestMapping("detail")
    public String detail(HttpServletRequest request, HttpServletResponse response, Integer vmId) {
//        TSysUser currentUser= (TSysUser) request.getSession().getAttribute("mpUser");
        try {
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            ObjectContext context = currentUser.getObjectContext();

            ObjectId id = new ObjectId(TMtVm.class.getSimpleName(), TMtVm.VM_ID_PK_COLUMN, vmId);
            // 表示刷新缓存，从表中从新获取数据
            ObjectIdQuery query = new ObjectIdQuery(id, false, ObjectIdQuery.CACHE_REFRESH);
            TMtVm mtVm = (TMtVm) Cayenne.objectForQuery(context, query);
            List<TMtCompany> list = currentUser.getAllCompany();
            boolean flag = false;
            for (TMtCompany mtCompany : list) {
                if (mtVm.getCompany().getId().equals(mtCompany.getId())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                try {
                    WebUtils.issueRedirect(request, response, unauthorizedUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            request.setAttribute("mtVm", mtVm);
            if (mtVm.getVmcStatus().equals("2") || mtVm.getVmcStatus().equals("1")) {
                Map<String, String> timeMap = mpVmStatusService.getFaultTime(context, mtVm);
                request.setAttribute("faultInfo", VmStatusDetailUtil.vmStatusDetailStr(mtVm.getVmStatusLog().getVmStatusDatail()));
                request.setAttribute("dateTime", timeMap.get("dateTime"));
                request.setAttribute("passTime", timeMap.get("passTime"));
            }
            if (mtVm.getNetworkStatus() == 1) {
//            String time=mtVm.getLastTime();
//            long timestamp = DateUtils.getDate2(time).getTime();
                String value = MyJedisUtils.hGet("vm_list", mtVm.getInnerCode());
                if (!StringUtil.isEmpty(value)) {
                    Long timestamp = Long.parseLong(value);
                    long now = System.currentTimeMillis();
                    request.setAttribute("netDateTime", DateUtils.getTime2(new Date(timestamp)));
                    request.setAttribute("netPassTime", DateUtils.formatDateTime(now - timestamp));
                }
            }
            /* Add 设备详情页增加温度状态 根据售货机当前温度和设置温度，判断温度状态 */
//            try {
//                String nowTempStr = mtVm.getNowTemp();
//                String targetTempStr = mtVm.getTargetTemp();
//                request.setAttribute("tempStatus",getTempStatus(nowTempStr,targetTempStr));
//            } catch (Exception e) {
//                logger.error(e.getLocalizedMessage(),e);
//                logger.error("设备详情页: 设备innerCode："+ mtVm.getInnerCode() +
//                        "设备当前温度"+ mtVm.getNowTemp() +",设置温度" + mtVm.getTargetTemp());
//                request.setAttribute("tempStatus","2");
//            }
            List<VmStockInfo> stockList = mpVmStatusService.findStockList(context, mtVm);
            request.setAttribute("stockList", stockList);
            request.setAttribute("isReserve", mtVm.isReserve());
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mp/device/device_status_detail";
    }

    @RequestMapping("open")
    @ResponseBody
    public Map<String, String> openBox(Integer vmId) {
        Map<String, String> resMap = new HashMap<>();
        try {
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            ObjectContext context = currentUser.getObjectContext();
            TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, vmId);
            String errMsg = mcsService.openAllBox(mtVm.getInnerCode());
            if (StringUtil.isEmpty(errMsg)) {
                resMap.put("type", "success");
                resMap.put("message", "已打开全部格子门");
            } else {
                resMap.put("type", "error");
                resMap.put("message", "打开全部格子门失败");
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return resMap;
    }

}
