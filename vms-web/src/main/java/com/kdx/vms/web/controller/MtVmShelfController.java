package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.custom.ShelfConfigCache;
import com.kdx.vms.redis.MyJedisCluster;
import com.kdx.vms.service.IMcsService;
import com.kdx.vms.service.IMtVmShelfService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangshuxi on 2018/2/7.
 */
@Controller
@RequestMapping(value = "mtVmShelf")
public class MtVmShelfController {
    @Resource
    private MyJedisCluster myJedisCluster;
    @Resource
    private IMtVmShelfService mtVmShelfService;
    @Resource
    private IMcsService mcsService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("list")
    public String list(HttpServletRequest request, Integer ids) {
        try {
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            TSysUser user = token.getSysUser();
            TMtVm firstMtVm = Cayenne.objectForPK(context, TMtVm.class, ids);
            ShelfConfigCache cache = mtVmShelfService.getShelfCache(user, firstMtVm);
            String html = mtVmShelfService.makeShelfTable(firstMtVm, cache);
            request.setAttribute("html", html);
            request.setAttribute("mtVm", firstMtVm);
            request.setAttribute("ids", ids);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mtVm/dialog/shelf/mt_vm_shelf";
    }

    @RequestMapping("changeSort")
    @ResponseBody
    public Map<String, Object> changeSort(HttpServletRequest request, Integer firstNumber, Integer secondNumber, Integer vmId) {
        Map<String, Object> resultMap;
        try {
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, vmId);
            ShelfConfigCache cache = mtVmShelfService.getShelfCache(token.getSysUser(), mtVm);
            resultMap = cache.exchange(firstNumber, secondNumber);
            if (resultMap.get("type").equals("success")) {
                mtVmShelfService.updateShelfCache(cache);
                resultMap.put("html",mtVmShelfService.makeShelfTable(mtVm, cache));
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            resultMap = ReturnCode.FAIL.toMap();
        }
        return resultMap;
    }

    @RequestMapping("copySort")
    @ResponseBody
    public Map<String, Object> copySort(Integer sortNumber, Integer vmId) {
        Map<String, Object> resultMap;
        try {
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, vmId);
            ShelfConfigCache cache = mtVmShelfService.getShelfCache(token.getSysUser(), mtVm);
            resultMap = cache.copy(sortNumber);
            if (resultMap.get("type").equals("success")) {
                mtVmShelfService.updateShelfCache(cache);
                resultMap.put("html",mtVmShelfService.makeShelfTable(mtVm, cache));
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            resultMap = ReturnCode.FAIL.toMap();
        }
        return resultMap;
    }

    @RequestMapping("deleteSort")
    @ResponseBody
    public Map<String, Object> deleteSort(Integer sortNumber, Integer vmId) {
        Map<String, Object> resultMap;
        try {
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, vmId);
            ShelfConfigCache cache = mtVmShelfService.getShelfCache(token.getSysUser(), mtVm);
            resultMap = cache.delete(sortNumber);
            if (resultMap.get("type").equals("success")) {
                mtVmShelfService.updateShelfCache(cache);
                resultMap.put("html",mtVmShelfService.makeShelfTable(mtVm, cache));
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            resultMap = ReturnCode.FAIL.toMap();
        }
        return resultMap;
    }

    @RequestMapping("saveSort")
    @ResponseBody
    public Map<String, Object> saveSort(Integer vmId) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, vmId);
            ShelfConfigCache cache = mtVmShelfService.getShelfCache(token.getSysUser(), mtVm);
            mtVmShelfService.saveShelf(mtVm, cache);
            mtVmShelfService.deleteShelfCache(vmId);
            String errMsg = mcsService.sendSkuOrder(context, mtVm.getInnerCode(), null);
            if(StringUtil.isEmpty(errMsg)){
                resultMap.put("type", "success");
                resultMap.put("message", "保存成功");
            }else{
                resultMap.put("type", "error");
                resultMap.put("message", "下发消息失败");
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            resultMap = ReturnCode.FAIL.toMap();
        }
        return resultMap;
    }
}
