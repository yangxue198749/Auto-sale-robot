package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IMtVmModelService;
import com.kdx.vms.service.IShortcutsService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.Cayenne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 闪购键Controller
 *
 * Created by liyang on 16-11-29.
 */
@Controller
@RequestMapping("stShortcuts")
public class ShortcutsController {

    @Resource private IShortcutsService shortcutsService;
    @Resource private IMtVmModelService mtVmModelService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("list")
    public String list(HttpServletRequest request) {
        try {
            SSOToken token = TokenUtil.getToken();
            List list = mtVmModelService.findMtVmModel(token.getObjectContext(), false);
            request.setAttribute("modelList", list);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "shortcuts/shortcuts_list";
    }

    @RequestMapping("listPage")
    public String listPage(HttpServletRequest request,
                            Integer pageSize,
                            Integer pageNo,
                            @CookieValue(value = "pageSize", required  = false) Integer cookiePageSize) {
        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null)   pageNo = 1;
        if (pageSize == null) pageSize = 10;
        String url = "listPage";
        Integer totalSize = 1000;
        try {
            // 从缓存中得到当前用户TSysUser
            SSOToken token = TokenUtil.getToken();
            String strategyName = request.getParameter("strategyName");
            logger.info("strategyName:" + strategyName);
            String modelId = request.getParameter("modelId");
            logger.info("modelId:" + modelId);
            TSysUser tSysUser = token.getSysUser();
            List list = shortcutsService.getShortcutsStrategy(tSysUser.getCompany(),
                    strategyName,
                    StringUtil.isEmpty(modelId) ? null : Integer.parseInt(modelId),
                    pageSize);
            logger.info("list:" + list);
            if (null != list) {
                totalSize = list.size();
                Integer start = (pageNo - 1) * pageSize;
                Integer end = pageNo * pageSize;
                start = start > list.size() ? list.size() : start;
                end = end > list.size() ? list.size() : end;
                logger.info("start:" + start + ", end:" + end);
                request.setAttribute("strategyList", list.subList(start, end));
            }
            request.setAttribute("url"      , url);
            request.setAttribute("pageSize" , pageSize);
            request.setAttribute("pageNo"   , pageNo);
            request.setAttribute("totalSize", totalSize);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "shortcuts/table/shortcuts_table";
    }

    @RequestMapping("view")
    public String view(HttpServletRequest request, Integer strategyId) {
        try {
            TStVmStrategy stVmStrategy = Cayenne.objectForPK(TokenUtil.getToken().getObjectContext(), TStVmStrategy.class, strategyId);
            Map map = stVmStrategy.getStShortcuts();
            List<Object> list = new ArrayList<>();
            if (null != map) {
                for (int i = 1; i <= map.size(); i++) {
                    list.add(map.get(i + ""));
                }
            }
            request.setAttribute("shortcutsList", list);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "shortcuts/dialog/shortcuts_view_dialog";
    }

    @RequestMapping("addPage1")
    public String addPage1(HttpServletRequest request) {
        try {
            SSOToken token = TokenUtil.getToken();
            List list = mtVmModelService.findMtVmModel(token.getObjectContext(), true);
            logger.info("#addPage1 " + list);
            request.setAttribute("modelList", list);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "shortcuts/dialog/shortcuts_add_1_dialog";
    }

    @RequestMapping("addPage2")
    public String addPage2(HttpServletRequest request) {
        try {
            String strategyName = request.getParameter("strategyName");
            logger.info("strategyName:" + strategyName);
            request.setAttribute("strategyName", strategyName);
            String strategyDesc = request.getParameter("strategyDesc");
            logger.info("strategyDesc:" + strategyDesc);
            request.setAttribute("strategyDesc", strategyDesc);
            String modelId = request.getParameter("modelId");
            logger.info("modelId:" + modelId);
            request.setAttribute("modelId", modelId);
            TMtVmModel mtVmModel = Cayenne.objectForPK(TokenUtil.getToken().getObjectContext(), TMtVmModel.class, Integer.parseInt(modelId));
            logger.info("mtVmModel:" + mtVmModel);
            request.setAttribute("model", mtVmModel);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "shortcuts/dialog/shortcuts_add_2_dialog";
    }

    @RequestMapping("editPage1")
    public String editPage1(HttpServletRequest request, Integer ids) {
        try {
            SSOToken token = TokenUtil.getToken();
            TStVmStrategy stVmStrategy = Cayenne.objectForPK(token.getObjectContext(), TStVmStrategy.class, ids);
            request.setAttribute("strategy", stVmStrategy);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "shortcuts/dialog/shortcuts_edit_1_dialog";
    }

    @RequestMapping("editPage2")
    public String editPage2(HttpServletRequest request, Integer strategyId) {
        try {
            String strategyName = request.getParameter("strategyName");
            logger.info("strategyName:" + strategyName);
            request.setAttribute("strategyName", strategyName);
            String strategyDesc = request.getParameter("strategyDesc");
            logger.info("strategyDesc:" + strategyDesc);
            request.setAttribute("strategyDesc", strategyDesc);
            String modelId = request.getParameter("modelId");
            logger.info("modelId:" + modelId);
            request.setAttribute("modelId", modelId);
            TMtVmModel mtVmModel = Cayenne.objectForPK(TokenUtil.getToken().getObjectContext(), TMtVmModel.class, Integer.parseInt(modelId));
            logger.info("mtVmModel:" + mtVmModel);
            request.setAttribute("model", mtVmModel);
            request.setAttribute("strategyId", strategyId);

            TStVmStrategy stVmStrategy = Cayenne.objectForPK(TokenUtil.getToken().getObjectContext(), TStVmStrategy.class, strategyId);
            Map map = stVmStrategy.getStShortcuts();
            List<Object> list = new ArrayList<>();
            if (null != map) {
                for (int i = 1; i <= map.size(); i++) {
                    list.add(map.get(i + ""));
                }
            }
            request.setAttribute("shortcutsList", list);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "shortcuts/dialog/shortcuts_edit_2_dialog";
    }

    @RequestMapping("addShortcuts")
    @ResponseBody
    public Map<String, Object> addShortcuts(HttpServletRequest request, Integer modelId, Integer[] skuIds,Integer[] bindTypes,Integer[] resourceIds) {
        try {
            String strategyName = request.getParameter("strategyName");
            logger.info("strategyName:" + strategyName);
            String strategyDesc = request.getParameter("strategyDesc");
            logger.info("strategyDesc:" + strategyDesc);
            SystemCode systemCode = shortcutsService.addShortcutsStrategy(strategyName, strategyDesc, modelId, skuIds, TokenUtil.getToken().getSysUser(),bindTypes,resourceIds);
            return systemCode.toMap();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return SystemCode.FAIL.toMap();
    }

    @RequestMapping("editShortcuts")
    @ResponseBody
    public Map<String, Object> editShortcuts(HttpServletRequest request, Integer strategyId, Integer[] skuIds,Integer[] bindTypes,Integer[] resourceIds) {
        try {
            String strategyName = request.getParameter("strategyName");
            logger.info("strategyName:" + strategyName);
            String strategyDesc = request.getParameter("strategyDesc");
            logger.info("strategyDesc:" + strategyDesc);
            SystemCode systemCode = shortcutsService.editShortcutsStrategy(strategyId, strategyName, strategyDesc, skuIds, TokenUtil.getToken().getSysUser(),bindTypes,resourceIds);
            return systemCode.toMap();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return SystemCode.FAIL.toMap();
    }

    @RequestMapping("deleteShortcuts")
    @ResponseBody
    public Map<String, Object> deleteShortcuts(Integer[] ids) {
        try {
            SystemCode systemCode = shortcutsService.deleteShortcutsStrategy(ids, TokenUtil.getToken().getSysUser());
            return systemCode.toMap();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return SystemCode.FAIL.toMap();
    }

    @RequestMapping("sku")
    public String sku(HttpServletRequest request, String shortcutsCode){
        try {
            request.setAttribute("shortcutsCode", shortcutsCode);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return "shortcuts/dialog/select_sku_dialog";
    }

    @RequestMapping("sku/page")
    public String skuPage(HttpServletRequest request, Integer skuGroupTypeId, Integer skuTypeId, String skuName,String skuCode){
//        List totalList= SkuUtils.getSkuList(skuTypeId, skuGroupTypeId, null, skuName,false);
//        try {
//            request.setAttribute("subList", totalList);
//        } catch (Exception e) {
//            logger.error(e.getLocalizedMessage());
//        }
        request.setAttribute("skuIds","");
        request.setAttribute("skuName",skuName);
        request.setAttribute("skuCode",skuCode);
        request.setAttribute("skuGroupTypeId",skuGroupTypeId);
        request.setAttribute("skuTypeId",skuTypeId);
        return "shortcuts/dialog/sku_page";
    }

    @RequestMapping("selectMtVmResourceDialog")
    public String selectMtVmResourceDialog(HttpServletRequest request,String shortcutsCode){

        request.setAttribute("shortcutsCode",shortcutsCode);
        return "shortcuts/dialog/select_mt_vm_resource_dialog";
    }
}
