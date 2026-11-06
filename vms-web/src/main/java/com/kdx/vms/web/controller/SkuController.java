package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.*;
import com.kdx.vms.commons.util.oss.AliyunOssUtils;
import com.kdx.vms.model.bean.TMtSku;
import com.kdx.vms.model.bean.TMtSkuImg;
import com.kdx.vms.model.bean.TSysSettings;
import com.kdx.vms.service.IMtSkuCompanyService;
import com.kdx.vms.service.ISkuService;
import com.kdx.vms.utils.SysSettingsCache;
import com.kdx.vms.web.tags.SkuUtils;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URLDecoder;
import java.util.*;


@Controller
@RequestMapping(value = "sku")
public class SkuController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ISkuService iSkuService;

    @Autowired
    private IMtSkuCompanyService mtSkuCompanyService;

    @RequestMapping(value = "home")
    public String home() {
        return "sku/sku_list";
    }

    @RequestMapping(value = "list")
    @ResponseBody
    public ModelAndView list(String skuName,String skuCode, Integer skuTypePid, Integer skuTypeId, Integer pageSize, Integer pageNo) {
        ModelAndView modelAndView = new ModelAndView("sku/page/sku_page", iSkuService.list(TokenUtil.getToken().getObjectContext(), skuName, skuCode, skuTypePid, skuTypeId, pageSize, pageNo));
        modelAndView.addObject("preImgUrl", PropertiesLoader.map.get("imgUrlPrefix"));
        return modelAndView;
    }

    @RequestMapping(value = "lookOtherPage")
    public ModelAndView lookOtherPage(Integer ids) {
        ModelAndView modelAndView = new ModelAndView("sku/dialog/lookOther_sku_dialog", "sku", iSkuService.getTMtSku(TokenUtil.getToken().getObjectContext(), ids));
        return modelAndView;
    }

    @RequestMapping(value = "create")
    @ResponseBody
    public Integer createSku(TMtSku tMtSku, Integer skuTypeId) {
        return iSkuService.createSku(TokenUtil.getToken().getObjectContext(), tMtSku, skuTypeId, TokenUtil.getToken().getSysUser().getId());
    }

    @RequestMapping(value = "createPage")
    public String createSkuPage() {
        return "sku/dialog/add_sku_dialog";
    }

    @RequestMapping(method = RequestMethod.POST, value = "change")
    @ResponseBody
    public Map<String, Object> changeSku(Integer skuId, String describe, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile fileMaster = multipartRequest.getFile("fileMaster");
            MultipartFile fileSecond = multipartRequest.getFile("fileSecond");
            MultipartFile fileThird = multipartRequest.getFile("fileThird");
            MultipartFile fileDesc = multipartRequest.getFile("fileDesc");
            String newFileName = null;
            Boolean flag = true;

            Integer id = iSkuService.updateSkuDescribe(TokenUtil.getToken().getObjectContext(), skuId, describe, TokenUtil.getToken().getSysUser().getId());
            if(null == id){
                return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
            }
            if(null != fileMaster){
                newFileName = new Date().getTime() + "_" + RandomUtils.nextLong(1000, 9999L) +fileMaster.getOriginalFilename().substring(fileMaster.getOriginalFilename().lastIndexOf("."));
                Map<String, Object> map = validateImgFile(fileMaster, skuId, newFileName, TMtSkuImg.IMG_FIRST_URL);
                if(null != map){
                    return map;
                }
                flag = iSkuService.changeSkuNew(TokenUtil.getToken().getObjectContext(), skuId, newFileName, TMtSkuImg.IMG_FIRST_URL, TMtSkuImg.IMG_SOURCE_VMS, TMtSkuImg.NO_IMG_MASTER_URL);
                if(false == flag){
                    return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
                }
            }
            if(null != fileSecond){
                newFileName = new Date().getTime() + "_" + RandomUtils.nextLong(1000, 9999L) +fileSecond.getOriginalFilename().substring(fileSecond.getOriginalFilename().lastIndexOf("."));
                Map<String, Object> map = validateImgFile(fileSecond, skuId, newFileName, TMtSkuImg.IMG_SECOND_URL);
                if(null != map){
                    return map;
                }
                flag = iSkuService.changeSkuNew(TokenUtil.getToken().getObjectContext(), skuId, newFileName, TMtSkuImg.IMG_SECOND_URL,TMtSkuImg.IMG_SOURCE_VMS, TMtSkuImg.NO_IMG_MASTER_URL);
                if(false == flag){
                    return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
                }
            }
            if(null != fileThird){
                newFileName = new Date().getTime() + "_" + RandomUtils.nextLong(1000, 9999L) +fileThird.getOriginalFilename().substring(fileThird.getOriginalFilename().lastIndexOf("."));
                Map<String, Object> map = validateImgFile(fileThird, skuId, newFileName, TMtSkuImg.IMG_THREE_URL);
                if(null != map){
                    return map;
                }
                flag = iSkuService.changeSkuNew(TokenUtil.getToken().getObjectContext(), skuId, newFileName, TMtSkuImg.IMG_THREE_URL,TMtSkuImg.IMG_SOURCE_VMS, TMtSkuImg.NO_IMG_MASTER_URL);
                if(false == flag){
                    return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
                }
            }
            if(null != fileDesc){
                newFileName = new Date().getTime() + "_" + RandomUtils.nextLong(1000, 9999L) +fileDesc.getOriginalFilename().substring(fileDesc.getOriginalFilename().lastIndexOf("."));
                Map<String, Object> map = validateImgFile(fileDesc, skuId, newFileName, TMtSkuImg.IMG_DESC_URL);
                if(null != map){
                    return map;
                }
                flag = iSkuService.changeSkuNew(TokenUtil.getToken().getObjectContext(), skuId, newFileName, TMtSkuImg.IMG_DESC_URL,TMtSkuImg.IMG_SOURCE_VMS, TMtSkuImg.IMG_MASTER_URL);
                if(false == flag){
                    return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
                }
            }
            return ReturnCode.SUCCESS.toMap();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
    }

    @RequestMapping(value = "changePage")
    public String changeSkuPage(HttpServletRequest request, Integer ids) {
        try {
            ObjectContext context = TokenUtil.getToken().getObjectContext();
            List<TMtSkuImg> skuImgs = iSkuService.findSkuImgsBySkuId(context, ids);
            List<TMtSkuImg> imgs = new ArrayList<>();
            for(TMtSkuImg tMtSkuImg: skuImgs){
                if(tMtSkuImg.getImgIndex() != TMtSkuImg.IMG_DESC_URL){
                    imgs.add(tMtSkuImg);
                }
            }
            TMtSku tMtSku = Cayenne.objectForPK(context, TMtSku.class, ids);
            request.setAttribute("imgSize", imgs.size());
            request.setAttribute("skuDesc", tMtSku.getSkuDesc());
            request.setAttribute("skuId", ids);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(), e);
        }
        return "sku/dialog/change_sku_dialog";
    }


    @RequestMapping(value = "update")
    @ResponseBody
    public Integer updateSku(TMtSku tMtSku, Integer skuTypeId) {
        return iSkuService.updateSku(TokenUtil.getToken().getObjectContext(), tMtSku, skuTypeId, TokenUtil.getToken().getSysUser().getId());
    }

    @RequestMapping(value = "updatePage")
    public ModelAndView updatePage(Integer ids) {
        ModelAndView modelAndView = new ModelAndView("sku/dialog/edit_sku_dialog", "sku", iSkuService.getTMtSku(TokenUtil.getToken().getObjectContext(), ids));
        return modelAndView;
    }

    private String getParentFile(String url) {
        File file = new File(this.getClass().getClassLoader().getResource("/").getPath());
        return file.getParentFile().getParentFile().toString() + url;
    }


    @RequestMapping(value = "loadSku")
    @ResponseBody
    public Map<String, Object> loadSku(HttpServletRequest request, String skuIds, String skuGroupTypeId, String skuTypeId, String skuName, String skuCode, Integer pageNo) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (!StringUtil.isEmpty(skuName)) {
                skuName = URLDecoder.decode(skuName, "UTF-8");
            }
            if (!StringUtil.isEmpty(skuCode)) {
                skuName = URLDecoder.decode(skuName, "UTF-8");
            }
            Integer groupTypeId = null;
            if (!StringUtil.isEmpty(skuGroupTypeId)) {
                groupTypeId = StringUtil.getIntValue(skuGroupTypeId, 0);
            }
            Integer typeId = null;
            if (!StringUtil.isEmpty(skuTypeId)) {
                typeId = StringUtil.getIntValue(skuTypeId, 0);
            }
//            List totalList = SkuUtils.getSkuList(typeId, groupTypeId, StringUtil.splitToIntegerArray(",",skuIds,0), skuName);
            List totalList = SkuUtils.getSkuList(typeId, groupTypeId, StringUtil.splitToIntegerArray(",", skuIds, 0), skuName,skuCode, false);
            List subList = PageUtil.formatList(pageNo, 45, totalList);
            String html = createHtml(subList, pageNo == 1);
            map.put("html", html);
            map.put("type", "success");
            if (StringUtil.isEmpty(html)) {
                map.put("next", pageNo);
            } else {
                map.put("next", pageNo + 1);
            }
            map.put("emptyLibrary",totalList==null||totalList.size()==0);
            if (subList == null || subList.isEmpty() || subList.size() < 45) {
                map.put("message", "商品已全部加载");
                map.put("finish", true);
            } else {
                map.put("message", "");
                map.put("finish", false);
            }
            return map;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            map.put("type", "error");
            map.put("message", "加载商品失败");
            return map;
        }

    }

    @RequestMapping(value = "loadAllSku")
    @ResponseBody
    public Map<String, Object> loadAllSku(HttpServletRequest request, String skuIds, String skuGroupTypeId, String skuTypeId, String skuName, String skuCode, Integer pageNo) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (!StringUtil.isEmpty(skuName)) {
                skuName = URLDecoder.decode(skuName, "UTF-8");
            }
            Integer groupTypeId = null;
            if (!StringUtil.isEmpty(skuGroupTypeId)) {
                groupTypeId = StringUtil.getIntValue(skuGroupTypeId, 0);
            }
            Integer typeId = null;
            if (!StringUtil.isEmpty(skuTypeId)) {
                typeId = StringUtil.getIntValue(skuTypeId, 0);
            }
            List totalList = SkuUtils.getSkuList(typeId, groupTypeId, StringUtil.splitToIntegerArray(",", skuIds, 0), skuName, skuCode, true);
            List subList = PageUtil.formatList(pageNo, 45, totalList);
            String html = createHtml(subList, pageNo == 1);
            map.put("html", html);
            map.put("type", "success");
            if (StringUtil.isEmpty(html)) {
                map.put("next", pageNo);
            } else {
                map.put("next", pageNo + 1);
            }
            if (subList == null || subList.isEmpty() || subList.size() < 45) {
                map.put("message", "商品已全部加载");
                map.put("finish", true);
            } else {
                map.put("message", "");
                map.put("finish", false);
            }
            return map;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            map.put("type", "error");
            map.put("message", "加载商品失败");
            return map;
        }

    }

    private String createHtml(List list, boolean isPage1) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            TMtSku sku = (TMtSku) list.get(i);
            if (i == 0 && isPage1) {
                builder.append("<li class=\"on\">");
            } else {
                builder.append("<li>");
            }
            builder.append("<section><img src=\"").append(sku.getImgUrl()).append("\"></section><i></i>");
            builder.append("<p><a href=\"#\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"").append(sku.getSkuName()).append(sku.getSkuUnit()).append("\" data-original-title=\"").append(sku.getSkuName()).append(sku.getSkuUnit()).append("\"><span>").append(sku.getSkuName()).append("</span></a></p>");
            builder.append("<input type=\"hidden\" value=\"").append(sku.getId()).append("\" name=\"skuId\">");
            builder.append("<input type=\"hidden\" value=\"").append(sku.getSkuName()).append(sku.getSkuUnit()).append("\" name=\"skuName\">");
            builder.append("<input type=\"hidden\" value=\"").append(sku.getOfferPrice()).append("\" name=\"offerPrice\">");
            builder.append(" <input type=\"hidden\" value=\"").append(sku.getImgUrl()).append("\" name=\"skuImg\">");
            builder.append("</li>");
        }
        return builder.toString();
    }

    private Map<String, Object> validateImgFile(MultipartFile imgFile ,Integer skuId ,String newFileName ,Integer index){
        try {
            if (null != imgFile) {
                String maxImageSize = SysSettingsCache.getSetting(TSysSettings.SKU_MAX_IMAGE_SIZE, TSysSettings.UPLOAD_TYPE, "200");
                if(TMtSkuImg.IMG_DESC_URL == index){
                    maxImageSize = "500";
                }
                if (!imgFile.getOriginalFilename().toLowerCase().endsWith(".png")) { // 文件后缀png
                    return ReturnCode.IMAGE_SUF.toMap();
                }
                if (!ImageUtil.isImage(imgFile.getInputStream())) { // 是否图片文件
                    return ReturnCode.IMAGE_DENY.toMap();
                } else if (imgFile.getSize() > StringUtil.getIntValue(maxImageSize, 200) * 1024) { // 文件大小
                    Map<String, Object> map = ReturnCode.IMAGE_SIZE.toMap();
                    map.put("message", "上传失败，上传文件不能大于" + maxImageSize + "KB");
                    return map;
                } else { // 满足要求
                    // 查询旧文件名
                    TMtSkuImg tMtSkuImg = iSkuService.findBySkuId(TokenUtil.getToken().getSysUser().getObjectContext(), skuId, index, 1);
                    String oldFileName = null;
                    if(null != tMtSkuImg && index == tMtSkuImg.getImgIndex()){
                        oldFileName = tMtSkuImg.getImgUrl();
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
