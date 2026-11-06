package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.dao.MtVmChannelDao;
import com.kdx.vms.service.IMcsService;
import com.kdx.vms.service.ISysUserService;
import com.kdx.vms.service.ITaskVmService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.*;

/**
 * 补货工单
 *
 * @author zhangguodong
 */
@RequestMapping("deliveryTask")
@Controller
public class DeliveryTaskController {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private ITaskVmService taskVmService;
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private MtVmChannelDao mtVmChannelDao;
    @Resource
    private SearchTaskController searchTaskController;
    @Autowired
    private IMcsService mcsService;

    @RequestMapping("list")
    public String list() {

        return "deliveryTask/delivery_task_list";
    }

    @RequestMapping("listPage")
    public String listPage(HttpServletRequest request, Integer pageSize, Integer pageNo,
                           @CookieValue(value = "pageSize", required = false) Integer cookiePageSize, Integer searchField, String searchFieldValue, Integer searchTaskVmStatus, String startTime, String endTime) {

        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 10;
        String url = "/deliveryTask/listPage";
        try {
            SSOToken token = TokenUtil.getToken();
            List list = taskVmService.findPage(token.getObjectContext(), token.getSysUser(), searchField, searchFieldValue, searchTaskVmStatus, startTime, endTime, TTaskVm.TYPE_DELIVERY, pageSize);
            request.setAttribute("url", url);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", null != list ? list.size() : 0);
            request.setAttribute("taskVmList", PageUtil.formatList(pageNo, pageSize, list));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "deliveryTask/page/delivery_task_page";
    }

    @RequestMapping("addTaskDialog")
    public String addTaskDialog() {

        return "deliveryTask/dialog/add_task_dialog";
    }

    @RequestMapping("addTask")
    @ResponseBody
    public Map<String, Object> addTask(String[] vmIds, String ownerId, Integer deliveryPattern, String frmjson) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            SSOToken token = TokenUtil.getToken();
            List<TaskChannelModel> taskChannelModels = JSON.parseArray(frmjson, TaskChannelModel.class);
            Integer own = null;
            try {
                own = Integer.parseInt(ownerId);
            } catch (Exception e) {
                resultMap.put("type", "error");
                resultMap.put("message", "请选择负责人");
                return resultMap;
            }
            String errMsg = taskVmService.createDeliveryTaskByIds(token.getObjectContext(), token.getSysUser(), vmIds, own, deliveryPattern, taskChannelModels);
            if (StringUtil.isEmpty(errMsg)) {
                resultMap.put("type", "success");
                resultMap.put("message", "新建成功");
            } else {
                resultMap.put("type", "error");
                resultMap.put("message", errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return resultMap;
    }

    @RequestMapping("cancelTask")
    @ResponseBody
    public Map<String, Object> cancelTask(Integer[] ids) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            SSOToken token = TokenUtil.getToken();
            String errMsg = taskVmService.cancelTask(token.getObjectContext(), token.getSysUser(), ids);
            if (StringUtil.isEmpty(errMsg)) {
                resultMap.put("type", "success");
                resultMap.put("message", "取消成功");
                new Thread(() -> {
                    ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
                    Arrays.stream(ids)
                            .map(id -> Cayenne.objectForPK(context, TTaskVm.class, id))
                            .map(TTaskVm::getMtVm)
                            .map(TMtVm::getId)
                            .forEach(vmId -> mcsService.notifyOpenDoorPwd(context, vmId));
                }).start();
            } else {
                resultMap.put("type", "error");
                resultMap.put("message", errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return resultMap;
    }

    @RequestMapping("mtVmList")
    public String mtVmList() {

        return "deliveryTask/dialog/select_mt_vm_dialog";
    }

    @RequestMapping("editTaskDialog")
    public String editTaskDialog(HttpServletRequest request, Integer ids) {
        try {
            SSOToken token = TokenUtil.getToken();
            TTaskVm taskVm = taskVmService.findByPk(token.getObjectContext(), ids);
            if (taskVm != null) {
                String html = sysUserService.findSysUsersByRoleLevelForCompany(token.getObjectContext(), TSysRole.ROLE_LEVEL_DELIVERY, token.getSysUser().getCompany().getId(), taskVm.getOwner());
                request.setAttribute("taskVm", taskVm);
                request.setAttribute("html", html);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return "deliveryTask/dialog/edit_task_vm_dialog";
    }

    @RequestMapping("mtVmPage")
    public String mtVmPage(HttpServletRequest request, Integer pageSize, Integer pageNo,
                           @CookieValue(value = "pageSize", required = false) Integer cookiePageSize, String searchNodeName, String searchInnerCode) {
        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 10;
        String url = "/deliveryTask/mtVmPage";
        try {
            SSOToken token = TokenUtil.getToken();
            List list = taskVmService.mtVmPage(token.getObjectContext(), token.getSysUser(), searchNodeName, searchInnerCode, pageSize, TMtVm.STATUS_SALE);
            request.setAttribute("url", url);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", null != list ? list.size() : 0);
            request.setAttribute("mtVmList", PageUtil.formatList(pageNo, pageSize, list));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "deliveryTask/page/mt_vm_page";
    }

    @RequestMapping("editTaskVm")
    @ResponseBody
    public Map<String, Object> editTaskVm(Integer taskVmId, String editOwnerId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            SSOToken token = TokenUtil.getToken();
            Integer own = null;
            try {
                own = Integer.parseInt(editOwnerId);
            } catch (Exception e) {
                resultMap.put("type", "error");
                resultMap.put("message", "请选择负责人");
                return resultMap;
            }
            String errMsg = taskVmService.editTaskVm(token.getObjectContext(), token.getSysUser(), taskVmId, own, null, null);
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
        }
        return resultMap;
    }

    @RequestMapping("showTaskDetalDialog")
    public String showTaskDetalDialog(HttpServletRequest request, Integer taskId) {
        try {
            SSOToken token = TokenUtil.getToken();
            if (taskId != null) {
                TTaskVm taskVm = taskVmService.findByPk(token.getObjectContext(), taskId);
                if (null != taskVm) {
                    Collections.sort(taskVm.getTaskVmChannel(), new Comparator<TTaskVmChannel>() {
                        @Override
                        public int compare(TTaskVmChannel o1, TTaskVmChannel o2) {
                            String a1 = o1.getChannel().getChannelCode();
                            String[] code1 = a1.split("-");
                            int q1 = Integer.valueOf(code1[0]);
                            int w1 = Integer.valueOf(code1[1]);
                            int e1 = q1 * 100 + w1;
                            String a2 = o2.getChannel().getChannelCode();
                            String[] code2 = a2.split("-");
                            int q2 = Integer.valueOf(code2[0]);
                            int w2 = Integer.valueOf(code2[1]);
                            int e2 = q2 * 100 + w2;
                            int i = e1 - e2;
                            return i;
                        }
                    });
                    request.setAttribute("taskChannelList", taskVm.getTaskVmChannel());
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "deliveryTask/dialog/show_task_detail_dialog";
    }

    @RequestMapping("adJustTask")
    public String adJustTask(HttpServletRequest request, String[] vmIds) {
        try {
            SSOToken token = TokenUtil.getToken();
            //List<TMtVmChannel> tMtVmChannels = mtVmChannelDao.findByVmId(token.getObjectContext(),Integer.parseInt(vmIds[0]));
            List<TaskChannelModel> taskChannelModels = taskVmService.searchChannelByVmId(token.getObjectContext(), Integer.parseInt(vmIds[0]));
            if (null != taskChannelModels) {
                Collections.sort(taskChannelModels, new Comparator<TaskChannelModel>() {
                    @Override
                    public int compare(TaskChannelModel o1, TaskChannelModel o2) {
                        String a1 = o1.getChannelCode();
                        String[] code1 = a1.split("-");
                        int q1 = Integer.valueOf(code1[0]);
                        int w1 = Integer.valueOf(code1[1]);
                        int e1 = q1 * 100 + w1;
                        String a2 = o2.getChannelCode();
                        String[] code2 = a2.split("-");
                        int q2 = Integer.valueOf(code2[0]);
                        int w2 = Integer.valueOf(code2[1]);
                        int e2 = q2 * 100 + w2;
                        int i = e1 - e2;
                        return i;
                    }
                });
                request.setAttribute("taskChannelModels", taskChannelModels);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "deliveryTask/dialog/manual_adjustment_dialog";
    }

    /**
     * 导出配送工单数据
     *
     * @param response
     */
    @RequestMapping("exportDelivery")
    public void exportDeliveryTask(HttpServletResponse response, Integer[] taskVmIds) {
        try {
            SSOToken token = TokenUtil.getToken();
            List<TTaskVm> tTaskVms = new ArrayList<>(taskVmIds.length);
            for (Integer id : taskVmIds) {
                TTaskVm tTaskVm = taskVmService.findByPk(token.getObjectContext(), id);
                tTaskVms.add(tTaskVm);
            }
            response.setContentType("application/x-download");//设置生成的文件类型
            OutputStream out = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "filename="
                    + new String("配送工单.xls".getBytes("gb2312"), "iso8859-1"));

            HSSFWorkbook wb = new HSSFWorkbook();//excel文件,一个excel文件包含多个表
            HSSFSheet sheet = wb.createSheet();//表，一个表包含多个行
            HSSFCellStyle style = searchTaskController.setStype(wb);
            HSSFRow row = null;//行，一行包括多个单元格
            HSSFCell cell;//单元格
            String[] testString = {"售货机编号", "点位", "商品+规格", "货道", "补前余量", "清", "应补", "实补", "补后余量", "补货时间"};

            row = sheet.createRow(0);
            for (int i = 0; i < testString.length; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(style);
                cell.setCellType(HSSFCell.ENCODING_UTF_16);
                cell.setCellValue(new HSSFRichTextString(testString[i]));
                sheet.setColumnWidth(i, (short) (5000));
            }
            int j = 0;
            for (TTaskVm tTaskVm : tTaskVms) {
                for (TTaskVmChannel taskVmChannel : tTaskVm.getTaskVmChannel()) {
                    String[] names = {
                            Optional.of(tTaskVm).map(TTaskVm::getMtVm).map(TMtVm::getInnerCode).orElse(""),
                            Optional.of(tTaskVm).map(TTaskVm::getMtVm).map(TMtVm::getNode).map(TMtNode::getNodeAddress).orElse(""),
                            Optional.of(taskVmChannel).map(TTaskVmChannel::getSku).map(TMtSku::getSkuName).orElse("") + "+" + Optional.of(taskVmChannel).map(TTaskVmChannel::getSku).map(TMtSku::getSkuUnit).orElse(""),
                            Optional.of(taskVmChannel).map(TTaskVmChannel::getChannel).map(TMtVmChannel::getChannelCode).orElse(""),
                            Optional.of(taskVmChannel).map(TTaskVmChannel::getBeforeDeliveryStock).map(String::valueOf).orElse(""),
                            null == taskVmChannel.getRealClear() ? (null == taskVmChannel.getClearCount() ? "0" : taskVmChannel.getClearCount() + "") : taskVmChannel.getRealClear() + "",
                            null == taskVmChannel.getSkuCount() ? "0" : taskVmChannel.getSkuCount() + "",
                            null == taskVmChannel.getRealCount() ? "" : taskVmChannel.getRealCount() + "",
                            null == taskVmChannel.getAfterDeliveryStock() ? "" : taskVmChannel.getAfterDeliveryStock() + "",
                            null == tTaskVm.getFinishTime() ? "" : StringUtil.dateToStr(tTaskVm.getFinishTime(), StringUtil.Pattern)
                    };
                    row = sheet.createRow(++j);
                    for (int i = 0; i < names.length; i++) {
                        cell = row.createCell(i);
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
}
