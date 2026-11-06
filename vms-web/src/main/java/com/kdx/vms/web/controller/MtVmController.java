package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.orm.dao.MtVmDao;
import com.kdx.vms.service.*;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.BusinessDistrictUtils;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 售货机
 *
 * @author zhangguodong
 */
@Controller
@RequestMapping("mtVm")
public class MtVmController {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private IMtVmModelService mtVmModelService;
    @Resource
    private IMtBusinessDistrictService mtBusinessDistrictservice;
    @Resource
    private IMtVmService mtVmService;
    @Resource
    private IMtNodeService mtNodeService;
    @Resource
    private IMtVmChannelService mtVmChannelService;
    @Resource
    private MtVmDao mtVmDao;
    @Resource
    private ITaskVmService taskVmService;
    @Resource
    private SearchTaskController searchTaskController;

    @RequestMapping("list")
    public String list(HttpServletRequest request) {
        try {
            SSOToken token = TokenUtil.getToken();
            List vmModelList = mtVmModelService.findMtVmModel(token.getObjectContext(), false);
            List<TMtBusinessDistrict> mtBusinessDistrictList = BusinessDistrictUtils.getBusinessDistrictList();
            request.setAttribute("vmModelList", vmModelList);
            request.setAttribute("mtBusinessDistrictList", mtBusinessDistrictList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mtVm/mt_vm_list";
    }

    @RequestMapping("listPage")
    public String listPage(HttpServletRequest request, Integer pageSize, Integer pageNo,
                           @CookieValue(value = "pageSize", required = false) Integer cookiePageSize,
                           Integer searchFiled, String searchFiledValue, Integer searchVmModelId, Integer searchVmStatus, Integer province, Integer city,
                           Integer area, Integer seardchMtBusinessDistrictId) {
        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 10;
        String url = "/mtVm/listPage";
        try {
            SSOToken token = TokenUtil.getToken();
            logger.info("-----token-----" + token);
            List list = mtVmService.findMtVmList(token.getObjectContext(), searchFiled, searchFiledValue, searchVmModelId, searchVmStatus, province, city, area, seardchMtBusinessDistrictId, token.getSysUser(), pageSize, false);
            request.setAttribute("url", url);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", null != list ? list.size() : 0);
            request.setAttribute("mtVmList", PageUtil.formatList(pageNo, pageSize, list));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "mtVm/page/mt_vm_page";
    }

    @RequestMapping("validateVmModel")
    @ResponseBody
    public Map<String, Object> validateVmModel(Integer[] ids) {
        Map<String, Object> resultmMap = new HashMap<String, Object>();
        resultmMap.put("type", "success");
        try {
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            TMtVm firstMtVm = Cayenne.objectForPK(context, TMtVm.class, ids[0]);
            for (int i = 1; i < ids.length; i++) {
                TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, ids[i]);
                if (!mtVm.getVmModelId().equals(firstMtVm.getVmModelId())) {
                    resultmMap.put("type", "error");
                    resultmMap.put("message", "售货机型号不一致");
                    return resultmMap;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            resultmMap.put("type", "error");
            resultmMap.put("message", "售货机型号不一致！");
            return resultmMap;
        }
        return resultmMap;
    }

    @RequestMapping("validateTask")
    @ResponseBody
    public Map<String, Object> validateTask(Integer[] ids) {
        Map<String, Object> resultmMap = new HashMap<>();
        resultmMap.put("type", "success");
        try {
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            for (Integer id : ids) {
                TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, id);
                if (taskVmService.hasUnDealTask(context, mtVm, TTaskVm.TYPE_DELIVERY)) {
                    resultmMap.put("type", "error");
                    resultmMap.put("message", "售货机编号为" + mtVm.getInnerCode() + "的售货机有未完成的配送工单,无法设置货道");
                    return resultmMap;
                }
                if (taskVmService.hasUnDealRepairTask(context, mtVm)) {
                    resultmMap.put("type", "error");
                    resultmMap.put("message", "售货机编号为" + mtVm.getInnerCode() + "的售货机有未完成的维护工单,无法设置货道");
                    return resultmMap;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            resultmMap.put("type", "error");
            resultmMap.put("message", "有未完成的配送工单");
            return resultmMap;
        }
        return resultmMap;
    }

    /**
     * 新建售货机弹窗
     *
     * @param request
     * @return
     */
    @RequestMapping("addMtVmDialog")
    public String addMtVmDialog(HttpServletRequest request) {
        try {
            SSOToken token = TokenUtil.getToken();
//            List vmModelList = mtVmModelService.findMtVmModelByCombinatorialPatterns(token.getSysUser(), TMtVmModel.COMBINATORIAL_SINGLE, TMtVmModel.COMBINATORIAL_MAIN);
//            request.setAttribute("vmModelList", vmModelList);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mtVm/dialog/add_mt_vm_dialog";

    }

    /**
     * 新建售货机弹窗
     *
     * @param request
     * @return
     */
    @RequestMapping("extendMtVmDialog")
    public String extendMtVmDialog(HttpServletRequest request,Integer ids) {
        try {
            SSOToken token = TokenUtil.getToken();
//            List vmModelList = mtVmModelService.findMtVmModelByCombinatorialPatterns(token.getSysUser(), TMtVmModel.COMBINATORIAL_SINGLE, TMtVmModel.COMBINATORIAL_MAIN);
            request.setAttribute("mainVmId", ids);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mtVm/dialog/extend_mt_vm_dialog";

    }

    /**
     * 选择点位弹窗
     *
     * @return
     */
    @RequestMapping("selectNodeDialog")
    public String selectNodeDialog() {

        return "mtVm/dialog/select_node_dialog";
    }

    @RequestMapping("searchNodeListPage")
    public String searchNodeListPage(HttpServletRequest request, Integer pageSize, Integer pageNo,
                                     @CookieValue(value = "pageSize", required = false) Integer cookiePageSize, String searchNodeName, Integer province, Integer city, Integer area) {
        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 10;
        String url = "/mtVm/searchNodeListPage";
        try {
            SSOToken token = TokenUtil.getToken();
            List list = mtNodeService.findNodeList(token.getSysUser(), token.getObjectContext(), "1", searchNodeName, province, city, area, null, pageSize);
            request.setAttribute("url", url);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", null != list ? list.size() : 0);
            request.setAttribute("mtNodeList", PageUtil.formatList(pageNo, pageSize, list));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "mtVm/page/select_node_page";
    }

    /**
     * 选择复制售货机策略弹窗
     *
     * @return
     */
    @RequestMapping("selectCopyMtVmDialog")
    public String selectCopyMtVmDialog(HttpServletRequest request) {

        return "mtVm/dialog/select_copy_mt_vm_dialog";
    }

    @RequestMapping("selectCopyMtVmPage")
    public String selectCopyMtVmPage(HttpServletRequest request, Integer pageSize, Integer pageNo,
                                     @CookieValue(value = "pageSize", required = false) Integer cookiePageSize,
                                     String searchInnerCode, String searchNodeName) {
        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 10;
        String url = "/mtVm/selectCopyMtVmPage";
        try {
            SSOToken token = TokenUtil.getToken();
            List list = taskVmService.mtVmPage(token.getObjectContext(), token.getSysUser(), searchNodeName, searchInnerCode, pageSize, null);
            request.setAttribute("url", url);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", null != list ? list.size() : 0);
            request.setAttribute("mtVmList", PageUtil.formatList(pageNo, pageSize, list));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }

        return "mtVm/page/select_copy_mt_vm_page";
    }

    @RequestMapping("addMtVm")
    @ResponseBody
    public Map<String, Object> addMtVm(Integer copyMtVmId, Integer nodeId, Integer vmModelId, String vmDesc, Integer printer, Integer reserve, Integer[] payType) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            SSOToken token = TokenUtil.getToken();
            String errMsg = mtVmService.addMtVm(token.getObjectContext(), nodeId, vmModelId, copyMtVmId, vmDesc, token.getSysUser(), printer, reserve, payType);
            if (StringUtil.isEmpty(errMsg)) {
                resultMap.put("type", "success");
                resultMap.put("message", "新建成功");
            } else {
                resultMap.put("type", "error");
                resultMap.put("message", errMsg);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return resultMap;
    }

    @RequestMapping("extendMtVm")
    @ResponseBody
    public Map<String, Object> extendMtVm(Integer copyMtVmId, Integer mainVmId, Integer vmModelId) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            SSOToken token = TokenUtil.getToken();
            TMtVm mainVm = Cayenne.objectForPK(token.getObjectContext(), TMtVm.class, mainVmId);
            String errMsg = mtVmService.extendMtVm(mainVm, token.getSysUser(), copyMtVmId, vmModelId);
            if (StringUtil.isEmpty(errMsg)) {
                resultMap.put("type", "success");
                resultMap.put("message", "拓展成功");
            } else {
                resultMap.put("type", "error");
                resultMap.put("message", errMsg);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return resultMap;
    }


    @RequestMapping("showChannelDialog")
    public String showChannelDialog(HttpServletRequest request, Integer mtVmId) {
        try {
            SSOToken token = TokenUtil.getToken();
            String html = mtVmService.makeMtVmChannelTableString(token.getObjectContext(), mtVmId);
            request.setAttribute("realChannel", getRealChannel(token.getObjectContext(), mtVmId));
            request.setAttribute("html", html);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mtVm/dialog/show_channel_dialog";
    }

    /**
     * 得到售货机上报的真实货道情况
     *
     * @param objectContext
     * @param mtVmId
     * @return
     */
    private String getRealChannel(ObjectContext objectContext, Integer mtVmId) {
        try {
            TMtVm mtvm = mtVmDao.findByVmId(objectContext, mtVmId);
            if (null != mtvm) {
                String channelStructure = mtvm.getChannelStructure();
                if (!StringUtil.isEmpty(channelStructure)) {
                    String realChannel = "实际货道结构<br>";
                    List<Object> list = JSON.parseArray(channelStructure, Object.class);
                    if (null != list && list.size() > 0) {
                        int i = 1;
                        for (Object object : list) {
                            if (object instanceof Double) {
                                realChannel += "第" + i + "层:" + ((Double) object).intValue() + "列<br>";
                            } else if (object instanceof String) {
                                realChannel += "第" + i + "层:" + object + "列<br>";
                            } else if (object instanceof Integer) {
                                realChannel += "第" + i + "层:" + object + "列<br>";
                            }
                            i++;
                        }
                    }
                    return realChannel;
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "";
    }

    @RequestMapping("showSkuSortDialog")
    public String showSkuSortDialog(HttpServletRequest request, Integer mtVmId) {
        try {
            SSOToken token = TokenUtil.getToken();
            String html = mtVmService.makeSkuSortTableString(token.getObjectContext(), mtVmId);
            request.setAttribute("html", html);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mtVm/dialog/show_sku_sort_dialog";
    }

    @RequestMapping("showMtStrategyDialog")
    public String showMtStrategyDialog(HttpServletRequest request, Integer mtVmId) {
        try {
            SSOToken token = TokenUtil.getToken();
            TMtVm mtVm = mtVmDao.findByVmId(token.getObjectContext(), mtVmId);
            /*List<TMtVmStrategy> strategyList = new ArrayList<>();
            if(TMtVmModel.COMBINATORIAL_EXTENTION == mtVm.getVmType()){
                strategyList = mtVm.getParentVm().getStrategy();
            }else {
                strategyList = mtVm.getStrategy();
            }*/
            List<TMtVmStrategy> strategyList =mtVm.getStrategy();
            Map<Integer, String> map = new HashMap<Integer, String>();
            if (strategyList != null && strategyList.size() > 0) {
                for (TMtVmStrategy tMtVmStrategy : strategyList) {
                    map.put(tMtVmStrategy.getStrategyType(), tMtVmStrategy.getStrategyName());
                }
            }
            if(TMtVmModel.COMBINATORIAL_EXTENTION == mtVm.getVmType()){
                request.setAttribute("skuEventName", mtVm.getParentVm().getSkuEvent());
                request.setAttribute("vmEventName", mtVm.getParentVm().getVmEvent());
                List<TMtVmStrategy> strategies = mtVm.getParentVm().getStrategy();
                if (strategies != null && strategies.size() > 0) {
                    for (TMtVmStrategy tMtVmStrategy : strategies) {
                        if(tMtVmStrategy.getStrategyType().equals(TStVmStrategy.TYPE_ST_VM_PRICE)){
                            request.setAttribute("stVmPriceName",tMtVmStrategy.getStrategyName() );
                            break;
                        }
                    }
                }
            }else {
                request.setAttribute("skuEventName", mtVm.getSkuEvent());
                request.setAttribute("vmEventName", mtVm.getVmEvent());
                request.setAttribute("stVmPriceName", map.get(TStVmStrategy.TYPE_ST_VM_PRICE));
            }
            request.setAttribute("stChannelName", map.get(TStVmStrategy.TYPE_ST_VM_CHANNEL));
            request.setAttribute("stEnergySaveName", map.get(TStVmStrategy.TYPE_ST_VM_ENERGY_SAVE));
            request.setAttribute("stShortcutsName", map.get(TStVmStrategy.TYPE_ST_VM_SHORTCUTS));
            request.setAttribute("stTextName", map.get(TStVmStrategy.TYPE_ST_VM_TEXT));
            request.setAttribute("stThemeName", (null != mtVm.getVmTheme() && mtVm.getVmTheme().size() > 0) ? mtVm.getVmTheme().get(0).getTheme().getThemeName() : "");
            request.setAttribute("stThemePlate", null != mtVm.getThemeFormat()  ? mtVm.getThemeFormat().getThemeFormat() : "");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mtVm/dialog/show_mt_vm_strategy_dialog";
    }

    @RequestMapping("showMtSkuPriceDialog")
    public String showMtSkuPriceDialog(HttpServletRequest request, Integer mtVmId) {
        try {
            SSOToken token = TokenUtil.getToken();
            TMtVm tMtVm = mtVmDao.findByVmId(token.getObjectContext(), mtVmId);
            List<Integer> skuIdList = new ArrayList<>();
            List<TMtVmChannel> tMtVmChannels = tMtVm.getChannelInfo();
            for(TMtVmChannel tMtVmChannel: tMtVmChannels){
                TMtSku sku = tMtVmChannel.getSku();
                if(null != sku){
                    skuIdList.add(sku.getId());
                }
            }
           List<TMtVmSkuSort> skuSorts = tMtVm.getSkuSort().stream().filter(tMtVmSkuSort -> skuIdList.contains(tMtVmSkuSort.getSkuId())).collect(Collectors.toList());
           //tMtVm.getSkuSort().stream().filter(tMtVmSkuSort -> !skuIdList.stream().distinct().map(Integer::intValue).c).collect(Collectors.toList());
            request.setAttribute("skuSortList", skuSorts);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mtVm/dialog/show_mt_vm_sku_price_dialog";

    }

    @RequestMapping("editMtVmDialog")
    public String editMtVmDialog(HttpServletRequest request, Integer ids) {
        try {
            SSOToken token = TokenUtil.getToken();
            TMtVm mtVm = mtVmDao.findByVmId(token.getObjectContext(), ids);
            List<TMtPayType> tMtPayTypes = mtVmDao.findPayTypeByVmId(token.getObjectContext(), ids);
            List<Integer> list = new ArrayList<>(tMtPayTypes.size());
            for (TMtPayType tMtPayType : tMtPayTypes) {
                list.add(tMtPayType.getCode());
            }
            request.setAttribute("ids", ids);
            request.setAttribute("reserve", mtVm.getReserve());
            request.setAttribute("mtVm", mtVm);
            request.setAttribute("tMtPayTypes", list);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mtVm/dialog/edit_mt_vm_dialog";
    }

    @RequestMapping("editMtVm")
    @ResponseBody
    public Map<String, Object> editMtVm(Integer vmId, String vmDesc, Integer printer, Integer reserve, Integer[] payType) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            SSOToken token = TokenUtil.getToken();
            String errMsg = mtVmService.editMtVm(token.getObjectContext(), vmId, vmDesc, printer, reserve, payType, token.getSysUser());
            if (StringUtil.isEmpty(errMsg)) {
                resultMap.put("type", "success");
                resultMap.put("message", "修改成功");
            } else {
                resultMap.put("type", "error");
                resultMap.put("message", errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            resultMap.put("type", "error");
            resultMap.put("message", "系统内部异常");
        }
        return resultMap;
    }

    @RequestMapping("changeSale")
    @ResponseBody
    public Map<String, Object> changeSale(Integer vmId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            SSOToken token = TokenUtil.getToken();
            String errMsg = mtVmService.changeSale(token.getObjectContext(), vmId);
            if (StringUtil.isEmpty(errMsg)) {
                resultMap.put("tyep", "success");
                resultMap.put("message", "切换成功");
            } else {
                resultMap.put("tyep", "error");
                resultMap.put("message", errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            resultMap.put("tyep", "error");
            resultMap.put("message", "系统内部异常");
        }
        return resultMap;
    }

    /**
     * 选择售货机型号
     *
     * @param request
     * @return
     */
    @RequestMapping("selectVmModelDialog")
    public String selectVmModelDialog(HttpServletRequest request) {
        try {
            List vmModelList = mtVmModelService.findMtVmModelByCombinatorialPatterns(TokenUtil.getToken().getSysUser(), TMtVmModel.COMBINATORIAL_SINGLE, TMtVmModel.COMBINATORIAL_MAIN);
            request.setAttribute("vmModelList", vmModelList);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mtVm/dialog/select_vm_model_dialog";
    }

    /**
     * 选择扩展售货机型号
     *
     * @param request
     * @return
     */
    @RequestMapping("selectExtendVmModelDialog")
    public String selectExtendVmModelDialog(HttpServletRequest request) {
        try {
            List vmModelList = mtVmModelService.findMtVmModelByCombinatorialPatterns(TokenUtil.getToken().getSysUser(), TMtVmModel.COMBINATORIAL_EXTENTION);
            request.setAttribute("vmModelList", vmModelList);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mtVm/dialog/select_vm_model_dialog";
    }

    /**
     * 导出售货机数据
     *
     * @param response
     */
    @RequestMapping("exportMtVmList")
    public void exportDeliveryWorkOrder(HttpServletResponse response) {
        try {
            SSOToken token = TokenUtil.getToken();
            List<TMtVm> list = mtVmService.findMtVmList(token.getObjectContext(), null, null, null, null, null, null, null, null, token.getSysUser(), 10, false);
            response.setContentType("application/x-download");//设置生成的文件类型
            OutputStream out = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "filename="
                    + new String("售货机.xls".getBytes("gb2312"), "iso8859-1"));

            HSSFWorkbook wb = new HSSFWorkbook();//excel文件,一个excel文件包含多个表
            HSSFSheet sheet = wb.createSheet();//表，一个表包含多个行
            HSSFCellStyle style = searchTaskController.setStype(wb);
            HSSFRow row = null;//行，一行包括多个单元格
            HSSFCell cell;//单元格

            String[] testString = {"售货机编号", "省", "市", "区", "点位", "具体位置", "售货机型号", "状态", "所属组织", "硬件编号", "sim卡号", "打印机有无", "售卖类型", "场景", "创建人", "创建时间", "修改人", "修改时间"};

            row = sheet.createRow(0);
            for (int i = 0; i < testString.length; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(style);
                cell.setCellType(HSSFCell.ENCODING_UTF_16);
                cell.setCellValue(new HSSFRichTextString(testString[i]));
                sheet.setColumnWidth(i, (short) (5000));
            }
            int j = 0;
            for (TMtVm tMtVm : list) {
                String Username = null;
                if (null != tMtVm.getUtor()) {
                    Username = Cayenne.objectForPK(token.getObjectContext(), TSysUser.class, tMtVm.getUtor()).getUserName();
                }
                String[] names = {
                        Optional.of(tMtVm).map(TMtVm::getInnerCode).orElse(""),
                        Optional.of(tMtVm).map(TMtVm::getNode).map(TMtNode::getProvinceId).map(id -> Cayenne.objectForPK(token.getObjectContext(), TMtArea.class, id)).map(TMtArea::getAreaName).orElse(""),
                        Optional.of(tMtVm).map(TMtVm::getNode).map(TMtNode::getCityId).map(id -> Cayenne.objectForPK(token.getObjectContext(), TMtArea.class, id)).map(TMtArea::getAreaName).orElse(""),
                        Optional.of(tMtVm).map(TMtVm::getNode).map(TMtNode::getAreaId).map(id -> Cayenne.objectForPK(token.getObjectContext(), TMtArea.class, id)).map(TMtArea::getAreaName).orElse(""),
                        Optional.of(tMtVm).map(TMtVm::getNode).map(TMtNode::getNodeName).orElse(""),
                        Optional.of(tMtVm).map(TMtVm::getNode).map(TMtNode::getNodeAddress).orElse(""),
                        Optional.of(tMtVm).map(TMtVm::getVmModelId).map(id -> Cayenne.objectForPK(token.getObjectContext(), TMtVmModel.class, id)).map(TMtVmModel::getModelName).orElse(""),
                        "",
                        Optional.of(tMtVm).map(TMtVm::getCompany).map(TMtCompany::getCompanyName).orElse(""),
                        Optional.of(tMtVm).map(TMtVm::getHardwareCode).orElse(""),
                        Optional.of(tMtVm).map(TMtVm::getSimNumber).orElse(""),
                        Optional.of(tMtVm).map(TMtVm::getPrintStatus).orElse(""),
                        null,
                        Optional.of(tMtVm).map(TMtVm::getNode).map(TMtNode::getBusinessDistrictId).map(id -> Cayenne.objectForPK(token.getObjectContext(), TMtBusinessDistrict.class, id)).map(TMtBusinessDistrict::getBusinessDistrictName).orElse(""),
                        Optional.of(tMtVm).map(TMtVm::getCtor).map(id -> Cayenne.objectForPK(token.getObjectContext(), TSysUser.class, id)).map(TSysUser::getUserName).orElse(""),
                        null == tMtVm.getCtime() ? "" : StringUtil.dateToStr(tMtVm.getCtime(), StringUtil.Pattern),
                        Username,
                        null == tMtVm.getUtime() ? "" : StringUtil.dateToStr(tMtVm.getUtime(), StringUtil.Pattern),
                };
                row = sheet.createRow(++j);
                for (int i = 0; i < names.length; i++) {
                    if (null != row.getCell(i)) {
                        continue;
                    }
                    cell = row.createCell(i);
                    if (i == 6) {
                        String offStr = "";
                        Integer status = tMtVm.getVmStatus();
                        switch (status) {
                            //售货机状态 0未投放 1投放中 2在售 3停售 4撤回中
                            case 0:
                                offStr = "未投放";
                                break;
                            case 1:
                                offStr = "投放中";
                                break;
                            case 2:
                                offStr = "在售";
                                break;
                            case 3:
                                offStr = "停售";
                                break;
                            case 4:
                                offStr = "撤回中";
                                break;
                            default:
                                offStr = "未知";
                                break;
                        }
                        cell.setCellStyle(style);
                        cell.setCellType(HSSFCell.ENCODING_UTF_16);
                        cell.setCellValue(new HSSFRichTextString(names[i]));

                        cell = row.createCell(i + 1);
                        cell.setCellStyle(style);
                        cell.setCellType(HSSFCell.ENCODING_UTF_16);
                        cell.setCellValue(new HSSFRichTextString(offStr));
                        sheet.setColumnWidth(i, (short) (10000));
                    } else if (i == 11) {
                        String offStr = "";
                        Integer status = 3;
                        if (tMtVm.getReserve() != null) {
                            status = tMtVm.getReserve();
                        }
                        switch (status) {
                            //售卖类型 0即售 1预定 2预售
                            case 0:
                                offStr = "即售";
                                break;
                            case 1:
                                offStr = "预定";
                                break;
                            case 2:
                                offStr = "预售";
                                break;
                            default:
                                offStr = "—";
                                break;
                        }
                        cell.setCellStyle(style);
                        cell.setCellType(HSSFCell.ENCODING_UTF_16);
                        cell.setCellValue(new HSSFRichTextString(names[i]));

                        cell = row.createCell(i + 1);
                        cell.setCellStyle(style);
                        cell.setCellType(HSSFCell.ENCODING_UTF_16);
                        cell.setCellValue(new HSSFRichTextString(offStr));
                        sheet.setColumnWidth(i, (short) (10000));
                    } else {
                        cell.setCellStyle(style);
                        cell.setCellType(HSSFCell.ENCODING_UTF_16);
                        cell.setCellValue(new HSSFRichTextString(names[i]));
                    }
                }
            }
            wb.write(out);
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @RequestMapping("validateModelEdit")
    @ResponseBody
    public Map<String, Object> validateModelEdit(Integer[] ids) {
        Map<String, Object> resultmMap = new HashMap<>();
        resultmMap.put("type", "success");
        try {
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            TMtVm firstMtVm = Cayenne.objectForPK(context, TMtVm.class, ids[0]);
            Integer modelId = firstMtVm.getVmModelId();
            TMtVmModel model = Cayenne.objectForPK(context, TMtVmModel.class, modelId);
            if (null == model.getCombinatorialPattern() || model.getCombinatorialPattern().equals(TMtVmModel.COMBINATORIAL_EXTENTION)) {
                resultmMap.put("type", "error");
                resultmMap.put("message", "副机不允许修改！");
                return resultmMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            resultmMap.put("type", "error");
            resultmMap.put("message", "副机不允许修改！");
            return resultmMap;
        }
        return resultmMap;
    }

    @RequestMapping("validateModelShelf")
    @ResponseBody
    public Map<String, Object> validateModelShelf(Integer[] ids) {
        Map<String, Object> resultmMap = new HashMap<>();
        resultmMap.put("type", "success");
        try {
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            TMtVm firstMtVm = Cayenne.objectForPK(context, TMtVm.class, ids[0]);
            Integer modelId = firstMtVm.getVmModelId();
            TMtVmModel model = Cayenne.objectForPK(context, TMtVmModel.class, modelId);
            if (null == model.getCombinatorialPattern() || model.getCombinatorialPattern().equals(TMtVmModel.COMBINATORIAL_EXTENTION)) {
                resultmMap.put("type", "error");
                resultmMap.put("message", "该机型无列表页！");
                return resultmMap;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            resultmMap.put("type", "error");
            resultmMap.put("message", "该机型无列表页！");
            return resultmMap;
        }
        return resultmMap;
    }

    @RequestMapping("validateModelExtend")
    @ResponseBody
    public Map<String, Object> validateModelExtend(Integer[] ids) {
        Map<String, Object> resultmMap = new HashMap<>();
        resultmMap.put("type", "success");
        try {
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            TMtVm firstMtVm = Cayenne.objectForPK(context, TMtVm.class, ids[0]);
            Integer modelId = firstMtVm.getVmModelId();
            TMtVmModel model = Cayenne.objectForPK(context, TMtVmModel.class, modelId);
            if (null == model.getCombinatorialPattern() || !model.getCombinatorialPattern().equals(TMtVmModel.COMBINATORIAL_MAIN)) {
                resultmMap.put("type", "error");
                resultmMap.put("message", "只有主机才可拓展！");
                return resultmMap;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            resultmMap.put("type", "error");
            resultmMap.put("message", "只有主机才可拓展！");
            return resultmMap;
        }
        return resultmMap;
    }


    @RequestMapping("validateModelShortcuts")
    @ResponseBody
    public Map<String, Object> validateModelShortcuts(Integer[] ids) {
        Map<String, Object> resultmMap = new HashMap<String, Object>();
        resultmMap.put("type", "success");
        try {
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            for(Integer id : ids){
                TMtVm firstMtVm = Cayenne.objectForPK(context, TMtVm.class, id);
                TMtVmModel mtVmModel = Cayenne.objectForPK(context, TMtVmModel.class, firstMtVm.getVmModelId());
                if(mtVmModel.getShortcutsQty() == 0){
                    resultmMap.put("type", "error");
                    resultmMap.put("message", "该机型无闪购键");
                    return resultmMap;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            resultmMap.put("type", "error");
            resultmMap.put("message", "该机型无闪购键！");
            return resultmMap;
        }
        return resultmMap;
    }

    @RequestMapping("validateModelEnergy")
    @ResponseBody
    public Map<String, Object> validateModelEnergy(Integer[] ids) {
        Map<String, Object> resultmMap = new HashMap<String, Object>();
        resultmMap.put("type", "success");
        try {
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            for(Integer id : ids){
                TMtVm firstMtVm = Cayenne.objectForPK(context, TMtVm.class, id);
                TMtVmModel mtVmModel = Cayenne.objectForPK(context, TMtVmModel.class, firstMtVm.getVmModelId());
                if(mtVmModel.getTemperatureHandlerMode().equals(TMtVmModel.TEMPERATURE_HANDLER_MODE_NO) && !mtVmModel.getScreenHave().equals(TMtVmModel.SCREENHAVE_TRUE)){
                    resultmMap.put("type", "error");
                    resultmMap.put("message", "当前机型无法设置");
                    return resultmMap;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            resultmMap.put("type", "error");
            resultmMap.put("message", "当前机型无法设置！");
            return resultmMap;
        }
        return resultmMap;
    }

}
