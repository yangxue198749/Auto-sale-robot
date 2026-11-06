package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.*;
import com.kdx.vms.commons.util.oss.AliyunOssUtils;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.IMcsService;
import com.kdx.vms.service.IMtThemeService;
import com.kdx.vms.service.ISysLogService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by yangshuxi on 2017/5/3.
 */
@Controller
@RequestMapping(value = "mtVmTheme")
public class MtVmThemeController {
    static String PATH="mtVmTheme";
    @Autowired
    private IMtThemeService mtThemeService;
    @Autowired
    private IMcsService mcsService;
    @Autowired
    private ISysLogService sysLogService;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @RequestMapping("list")
    public String list(HttpServletRequest request,Integer[] ids){
        logger.info("开始设置"+StringUtil.join(",",ids)+"的皮肤");
        try {
            SSOToken token= TokenUtil.getToken();
            ObjectContext context = token.getSysUser().getObjectContext();
            TMtVm tMtVm = Cayenne.objectForPK(context,TMtVm.class,ids[0]);
            List<TMtVmThemeEntrance> entrances = tMtVm.getEntrances();
            request.setAttribute("ids", ids);
            request.setAttribute("entrances", entrances);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "mtVm/dialog/theme/mt_vm_theme";
    }

    @RequestMapping("page")
    public String page(HttpServletRequest request,String strategyName,Integer pageNo){
        if(pageNo == null) pageNo = 1;
        SSOToken token= TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        String url = PATH+"/page";
        List totalList = mtThemeService.getThemesByName(currentUser,strategyName,5);
        List subList= PageUtil.formatList(pageNo,5,totalList);
        try {
            request.setAttribute("strategyName",strategyName);
            request.setAttribute("subList",subList);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("pageSize", 5);
            request.setAttribute("url", url);
            request.setAttribute("totalSize", totalList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "mtVm/dialog/theme/mt_vm_theme_table";
    }

    @RequestMapping("save")
    @ResponseBody
    public Map<String,Object> save(Integer strategyId, Integer[] ids, HttpServletRequest request){
        logger.info("保存皮肤设置,售货机id："+StringUtil.join(",",ids)+"，皮肤id："+strategyId);
        SSOToken token= TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        return mtThemeService.bindTheme(currentUser,ids,strategyId).toMap();
    }

    @RequestMapping("saveFormat")
    @ResponseBody
    public Map<String,Object> saveFormat(Integer formatCode, Integer[] ids, HttpServletRequest request){
        logger.info("保存皮肤版式,售货机id：" + StringUtil.join(",",ids)+"，版式：" + formatCode);
        SSOToken token= TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        return mtThemeService.setFormat(currentUser, ids, formatCode).toMap();
    }

    @RequestMapping("selectMtVmResourcesDialog")
    public String selectMtVmResourcesDialog(HttpServletRequest request,String shortcutsCode){
        request.setAttribute("shortcutsCode",shortcutsCode);
        return "mtVm/dialog/theme/theme_entrance_dialog";
    }

    @RequestMapping(method = RequestMethod.POST, value = "saveConfiguration")
    @ResponseBody
    public Map<String,Object> saveConfiguration(Integer firstResourceId, Integer secondResourceId, Integer thirdResourceId, Integer[] ids, HttpServletRequest request){
        try {
            logger.info("保存皮肤入口配置,售货机id：" + StringUtil.join(",",ids)+"，入口：" + firstResourceId + "," + secondResourceId + "," + thirdResourceId);
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile firstFile = multipartRequest.getFile("firstFile");
            MultipartFile secondFile = multipartRequest.getFile("secondFile");
            MultipartFile thirdFile = multipartRequest.getFile("thirdFile");
            String newFileName =null;
            TSysUser currentUser = TokenUtil.getToken().getSysUser();
            ObjectContext context = currentUser.getObjectContext();

            for(Integer vmId : ids){
                if(null != firstFile && firstFile.getSize() >0 ){
                    newFileName = new Date().getTime() + "_" + RandomUtils.nextLong(1000, 9999L) +firstFile.getOriginalFilename().substring(firstFile.getOriginalFilename().lastIndexOf("."));
                    Map<String, Object> map = validateImgFile(firstFile,vmId,newFileName,TMtVmThemeEntrance.FIRST_PORTAL);
                    if(null !=map){
                        return map;
                    }
                    //mtThemeService.delectConfiguration(context, vmId, TMtVmThemeEntrance.FIRST_PORTAL);
                    mtThemeService.setConfiguration(context,firstResourceId,vmId,newFileName,TMtVmThemeEntrance.FIRST_PORTAL);
                }else{
                    if(null !=firstResourceId){
                        mtThemeService.setConfiguration(context,firstResourceId,vmId,newFileName,TMtVmThemeEntrance.FIRST_PORTAL);
                    }else {
                        mtThemeService.delectConfiguration(context, vmId, TMtVmThemeEntrance.FIRST_PORTAL);
                    }
                }
                if(null != secondFile && secondFile.getSize() >0 ){
                    newFileName = new Date().getTime() + "_" + RandomUtils.nextLong(1000, 9999L) +secondFile.getOriginalFilename().substring(secondFile.getOriginalFilename().lastIndexOf("."));
                    Map<String, Object> map = validateImgFile(secondFile,vmId,newFileName,TMtVmThemeEntrance.SECOND_PORTAL);
                    if(null !=map){
                        return map;
                    }
                    //mtThemeService.delectConfiguration(context, vmId, TMtVmThemeEntrance.SECOND_PORTAL);
                    mtThemeService.setConfiguration(context,secondResourceId,vmId,newFileName,TMtVmThemeEntrance.SECOND_PORTAL);
                }else{
                    if(null !=secondResourceId){
                        mtThemeService.setConfiguration(context,secondResourceId,vmId,newFileName,TMtVmThemeEntrance.SECOND_PORTAL);
                    }else {
                        mtThemeService.delectConfiguration(context, vmId, TMtVmThemeEntrance.SECOND_PORTAL);
                    }
                }
                if(null != thirdFile && thirdFile.getSize() >0 ){
                    newFileName = new Date().getTime() + "_" + RandomUtils.nextLong(1000, 9999L) +thirdFile.getOriginalFilename().substring(thirdFile.getOriginalFilename().lastIndexOf("."));
                    Map<String, Object> map = validateImgFile(thirdFile,vmId,newFileName,TMtVmThemeEntrance.THIRD_PORTAL);
                    if(null !=map){
                        return map;
                    }
                    //mtThemeService.delectConfiguration(context, vmId, TMtVmThemeEntrance.THIRD_PORTAL);
                    mtThemeService.setConfiguration(context,thirdResourceId,vmId,newFileName,TMtVmThemeEntrance.THIRD_PORTAL);
                }else{
                    if(null !=thirdResourceId){
                        mtThemeService.setConfiguration(context,thirdResourceId,vmId,newFileName,TMtVmThemeEntrance.THIRD_PORTAL);
                    }else {
                        mtThemeService.delectConfiguration(context, vmId, TMtVmThemeEntrance.THIRD_PORTAL);
                    }
                }

            }

            context.commitChanges();
            String mcsMsg = mcsService.sendEntrance(context, Arrays.asList(ids),null);
            if(StringUtil.isEmpty(mcsMsg)){
                sysLogService.saveSysLog("配置入口信息到售货机"+ StringUtil.join(",",ids), TSysLog.SAVE, SyslogEnum.MT_VM_CODE,currentUser.getId(),currentUser.getObjectContext());
                return ReturnCode.SUCCESS.toMap();
            }else{
                return ReturnCode.MCS_FAIL.toMap();
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return ReturnCode.CONTEXT_IS_NULL.toMap();
    }

    private Map<String, Object> validateImgFile(MultipartFile imgFile ,Integer vmId ,String newFileName ,Integer index){
        try {
            Integer maxImageSize = 100 * 1024;
            if (null != imgFile) {
                if (!imgFile.getOriginalFilename().toLowerCase().endsWith(".png") &&
                        !imgFile.getOriginalFilename().toLowerCase().endsWith(".jpg") &&
                        !imgFile.getOriginalFilename().toLowerCase().endsWith(".jpeg")) { // 文件后缀png
                    return ReturnCode.IMAGE_SUF_NEW.toMap();
                }
                if (!ImageUtil.isImage(imgFile.getInputStream())) { // 是否图片文件
                    return ReturnCode.IMAGE_DENY.toMap();
                } else if (imgFile.getSize() > maxImageSize) { // 文件大小
                    Map<String, Object> map = ReturnCode.IMAGE_SIZE.toMap();
                    map.put("message", "上传失败，上传文件不能大于" + 100 + "KB");
                    return map;
                }else { // 满足要求
                    // 查询旧文件名
                    TMtVmThemeEntrance themeEntrance = mtThemeService.findByVmId(TokenUtil.getToken().getSysUser().getObjectContext(), index, vmId);
                    String oldFileName = null;
                    if(null != themeEntrance && index == themeEntrance.getEntranceIndex()){
                        oldFileName = themeEntrance.getImgUrl();
                    }
                    // 上传到oss
                    if (AliyunOssUtils.upload(newFileName, imgFile.getInputStream())) {
                        if (!StringUtil.isEmpty(oldFileName)) AliyunOssUtils.delete(oldFileName);
                        return null;
                    } else {
                        logger.info("上传图片到oss失败。");
                    }
                }
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(), e);
        }
        return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
    }
}
