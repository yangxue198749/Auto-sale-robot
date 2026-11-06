package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.ValidateCode;
import com.kdx.vms.model.bean.TMtArea;
import com.kdx.vms.model.bean.TMtUpgradePackage;
import com.kdx.vms.model.bean.TMtUpgradePlan;
import com.kdx.vms.orm.cache.AreaCache;
import com.kdx.vms.orm.dao.MtVmModelDao;
import com.kdx.vms.service.IMtUpgradePackageService;
import com.kdx.vms.service.IMtUpgradePlanService;
import com.kdx.vms.service.IMtVmService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * vmbox 升级计划
 * Created by zhangguodong on 2017/6/22.
 */
@Controller
@RequestMapping("vmBoxUpgrade")
public class VmBoxUpgradeController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private IMtUpgradePlanService mtUpgradePlanService;
    @Resource
    private IMtUpgradePackageService mtUpgradePackageService;
    @Resource
    private MtVmModelDao mtVmModelDao;
    @Resource
    private IMtVmService mtVmService;
    @RequestMapping("list")
    public String list(){
        return "vmBoxUpgrade/vmBox_upgrade";
    }

    @RequestMapping("listPage")
    public String listPage(HttpServletRequest request, Integer pageSize, Integer pageNo,
                           @CookieValue(value = "pageSize",required  = false) Integer cookiePageSize,String versionNo){
        if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
        if(pageNo == null) pageNo = 1;
        if(pageSize == null) pageSize = 10;
        String url = "/vmBoxUpgrade/listPage";
        try {
            SSOToken token = TokenUtil.getToken();
            List list = mtUpgradePlanService.findList(token.getObjectContext(),versionNo,pageSize);
            request.setAttribute("url", url);
            request.setAttribute("pageSize",pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", null != list?list.size():0);
            request.setAttribute("vmBoxUpgradeList", PageUtil.formatList(pageNo, pageSize, list));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(),e);
        }
        return "vmBoxUpgrade/page/vmBox_upgrade_page";
    }

    /**
     * 创建升级计划弹窗
     * @return
     */
    @RequestMapping("createDialog")
    public String createDialog(){


        return "vmBoxUpgrade/dialog/create_dialog";
    }

    @RequestMapping("submitCreate")
    @ResponseBody
    public Map<String,Object> submitCreate(HttpServletRequest request,Integer vmModelId,Integer areaId,Integer vmBoxPackageId,Integer comminocationBoardPackageId,Integer outBoardPackageId,String validCode){
        Map<String,Object> map = new HashMap<>();
        try {
            String code = (String) request.getSession().getAttribute("CREATE_PLAN_CODE");

            if (StringUtils.isEmpty(validCode) || !StringUtils.equalsIgnoreCase(code, validCode)) {
                map.put("type", "error");
                map.put("message", "您输入的验证码有误，请重新输入.");
                return map;
            }
            SSOToken token = TokenUtil.getToken();
            map = mtUpgradePlanService.createVmBoxPlan(token.getSysUser(),vmModelId,areaId,vmBoxPackageId,comminocationBoardPackageId,outBoardPackageId);
            logger.info("创建升级计划结果 = "  + map.get("type")+" ,操作人Ip" + getIp(request));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return map;
    }
    public String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if(ip!=null &&!"".equals(ip) && !"".equalsIgnoreCase(ip)){
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if(index != -1){
                return ip.substring(0,index);
            }else{
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if(ip!=null &&!"".equals(ip) && !"unKnown".equalsIgnoreCase(ip)){
            return ip;
        }
        return request.getRemoteAddr();
    }

    /**
     * 选择升级版本弹窗
     * @return
     */
    @RequestMapping("selectVersionDialog")
    public String selectVersionDialog(HttpServletRequest request,Integer type){
        request.setAttribute("type",type);
        return "vmBoxUpgrade/dialog/select_version_dialog";
    }

    /**
     * 选择升级版本分页
     * @return
     */
    @RequestMapping("selectVersionPage")
    public String selectVersionPage(HttpServletRequest request, Integer pageSize, Integer pageNo,
                                    @CookieValue(value = "pageSize",required  = false) Integer cookiePageSize,Integer type){
        if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
        if(pageNo == null) pageNo = 1;
        if(pageSize == null) pageSize = 5;
        String url = "/vmBoxUpgrade/selectVersionPage";
        try {
            SSOToken token = TokenUtil.getToken();
            List list = mtUpgradePackageService.getUpgradePackage(token.getSysUser(),type,null,pageSize, TMtUpgradePackage.ENABLE);
            request.setAttribute("url", url);
            request.setAttribute("pageSize",pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", null != list?list.size():0);
            request.setAttribute("versionList", PageUtil.formatList(pageNo, pageSize, list));
            request.setAttribute("type",type);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(),e);
        }

        return "vmBoxUpgrade/page/select_version_page";
    }

    /**
     * 选择售货机型号弹窗
     * @return
     */
    @RequestMapping("selectVmModelDialog")
    public String selectVmModelDialog(){
        return "vmBoxUpgrade/dialog/select_vm_model_dialog";
    }

    /**
     * 选择售货机型号分页
     * @return
     */
    @RequestMapping("selectVmModelPage")
    public String selectVmModelPage(HttpServletRequest request, Integer pageSize, Integer pageNo,
                                    @CookieValue(value = "pageSize",required  = false) Integer cookiePageSize){
        if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
        if(pageNo == null) pageNo = 1;
        if(pageSize == null) pageSize = 5;
        String url = "/vmBoxUpgrade/selectVmModelPage";
        try {
            SSOToken token = TokenUtil.getToken();
            List list = mtVmModelDao.findVmModel(null,pageSize,token.getObjectContext());
            request.setAttribute("url", url);
            request.setAttribute("pageSize",pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", null != list?list.size():0);
            request.setAttribute("vmModelList", PageUtil.formatList(pageNo, pageSize, list));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(),e);
        }
        return "vmBoxUpgrade/page/select_vm_model_page";
    }

    /**
     * 展示详情
     * @return
     */
    @RequestMapping("showDetailDialog")
    public String showDetailDialog(HttpServletRequest request,Integer planId){

        request.setAttribute("planId",planId);
        return "vmBoxUpgrade/dialog/show_detail_dialog";
    }

    /**
     * 展示详情分页
     * @return
     */
    @RequestMapping("showDetailPage")
    public String showDetailPage(HttpServletRequest request, Integer pageSize, Integer pageNo,
                                 @CookieValue(value = "pageSize",required  = false) Integer cookiePageSize,Integer planId){
        if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
        if(pageNo == null) pageNo = 1;
        if(pageSize == null) pageSize = 5;
        String url = "/vmBoxUpgrade/showDetailPage";
        try {
            SSOToken token = TokenUtil.getToken();
            TMtUpgradePlan plan = mtUpgradePlanService.findById(token.getObjectContext(),planId);
            Integer areaId = plan.getAreaId();
            Integer province = null;
            Integer city = null;
            Integer country = null;
            TMtArea area = AreaCache.getArea(areaId);
            if(area.getAreaLevel() == 2)province = areaId;
            if(area.getAreaLevel() == 3)city = areaId;
            if(area.getAreaLevel() == 4)country = areaId;

            List list = mtVmService.findForUpGrade(token.getObjectContext(),plan.getVmModelId(),province,city,country,pageSize);
            request.setAttribute("url", url);
            request.setAttribute("pageSize",pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", null != list?list.size():0);
            request.setAttribute("mtVmList", PageUtil.formatList(pageNo, pageSize, list));
            request.setAttribute("planId",planId);
            request.setAttribute("plan",plan);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(),e);
        }
        return "vmBoxUpgrade/page/show_detail_page";
    }

    /**
     * 获取验证码
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/createCode")
    public void generateCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            response.setHeader("Cache-Control", "no-cache");
            response.setContentType("image/jpeg");

            String code = ValidateCode.generateTextCode(ValidateCode.TYPE_NUM_UPPER, 4, null);
            BufferedImage bim = ValidateCode.generateImageCode(code, 80, 25, 5, true, Color.WHITE, Color.BLACK, null);
            ImageIO.write(bim, "JPEG", response.getOutputStream());

            request.getSession().setAttribute("CREATE_PLAN_CODE", code);
            logger.info(request.getSession().getId()+  "获取到的验证码为 =  " + code);
        }catch (Exception e){
            logger.error("获取验证码异常  " + e.getLocalizedMessage(),e);
        }
    }

}
