package com.kdx.vms.web.controller;

import java.io.OutputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TTaskVm;
import com.kdx.vms.orm.dao.SysUserDao;
import com.kdx.vms.service.ITaskVmService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;

@RequestMapping("searchTask")
@Controller
public class SearchTaskController {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource
	private ITaskVmService taskVmService;
	@Resource
	private SysUserDao sysUserDao;
	@RequestMapping("list")
	public String list(){
		
		return "searchTask/search_task_list";
	}
	@RequestMapping("listPage")
	public String listPage(HttpServletRequest request, Integer pageSize, Integer pageNo, 
			@CookieValue(value = "pageSize",required  = false) Integer cookiePageSize,Integer searchField,String searchFieldValue,Integer searchTaskVmStatus,String startTime,String endTime,Integer searchTaskType){
		
		if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
    	if(pageNo == null) pageNo = 1;
    	if(pageSize == null) pageSize = 10;
		String url = "/searchTask/listPage";
		try {
			SSOToken token = TokenUtil.getToken();
			List list = taskVmService.findPage(token.getObjectContext(), token.getSysUser(), searchField, searchFieldValue, searchTaskVmStatus, startTime, endTime, searchTaskType, pageSize);
			request.setAttribute("url", url);
			request.setAttribute("pageSize",pageSize);
			request.setAttribute("pageNo", pageNo);
			request.setAttribute("totalSize", null != list?list.size():0);
			request.setAttribute("taskVmList", PageUtil.formatList(pageNo, pageSize, list));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		return "searchTask/page/search_task_page";
	}
	
	/**
	 * 导出工单
	 * @param response
	 * @param taskVmIds
	 */
	@RequestMapping("exportWorkOrder")
	public void exportWorkOrder( HttpServletResponse response, Integer[] taskVmIds){
		try {
			SSOToken token = TokenUtil.getToken();
			List<TTaskVm> list = taskVmService.exportTaskByIds(token.getObjectContext(),taskVmIds);
			response.setContentType("application/x-download");//设置生成的文件类型
			OutputStream out = response.getOutputStream();
			response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-Disposition", "filename="
						+ new String("工单.xls".getBytes("gb2312"), "iso8859-1"));
				
			HSSFWorkbook wb = new HSSFWorkbook();//excel文件,一个excel文件包含多个表
			int sheetIndex = 0;
			for (TTaskVm taskVm: list) {
				Integer taskType = taskVm.getTaskType();
				//3补货 5维修 7投放 8撤机
				HSSFSheet sheet = wb.createSheet();//表，一个表包含多个行
				
				if(taskType == 3){
					packDistributionWorkOrder(taskVm,wb,sheet);
					wb.setSheetName(sheetIndex, "补货工单"+taskVm.getTaskVmId());// 设置sheet名称中文编码；
				}else if(taskType == 5){
					packMaintenanceWorkOrder(taskVm,wb,sheet);
					wb.setSheetName(sheetIndex, "维修工单"+taskVm.getTaskVmId());// 设置sheet名称中文编码；
				}else if(taskType == 7){
					packLaunchWorkOrder(taskVm,wb,sheet);
					wb.setSheetName(sheetIndex, "投放工单"+taskVm.getTaskVmId());// 设置sheet名称中文编码；
				}else if(taskType == 8){
					packRecallWorkOrder(taskVm,wb,sheet);
					wb.setSheetName(sheetIndex, "撤机工单"+taskVm.getTaskVmId());// 设置sheet名称中文编码；
				}
				sheetIndex++;
			}
			wb.write(out);
	        out.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 撤机工单
	 * @param taskVm
	 * @param wb
	 * @param sheet
	 */
	private void packRecallWorkOrder(TTaskVm taskVm, HSSFWorkbook wb,
			HSSFSheet sheet) {
		
		HSSFCellStyle style = setStype(wb);
        HSSFRow row = null;//行，一行包括多个单元格
        HSSFCell cell;//单元格
        SSOToken token = TokenUtil.getToken();
        String ownerName = "";
        if(taskVm.getOwner() != null){
        	TSysUser owner = sysUserDao.findUserByPk(token.getObjectContext(), taskVm.getOwner());
        	if(null != owner){
        		ownerName = owner.getUserName();
        	}
        }
        String[] testString = {"任务ID","售货机硬件编号","售货机编号","原点位","原具体位置","负责人","状态","执行日期","创建人","创建时间"};
        String[] names = {taskVm.getTaskVmId()+"",taskVm.getMtVm().getHardwareCode(),taskVm.getMtVm().getInnerCode(),taskVm.getMtVm().getNode().getNodeName(),taskVm.getMtVm().getNode().getNodeAddress(),
        		ownerName
        		,"",StringUtil.dateToStr(taskVm.getTaskTime(), StringUtil.YMDPattern),
        		sysUserDao.findUserByPk(token.getObjectContext(), taskVm.getCtor()).getUserName(),StringUtil.dateToStr(taskVm.getCtime(), StringUtil.Pattern)};
        for (int i = 0; i < testString.length; i++) {
        	row = sheet.createRow(i);
            row.setHeightInPoints((float) 30);
            cell = row.getCell(0);
    		if (null == cell) {//列不为空
    			cell = row.createCell(0);
    		}
    		if(i==6){
    			String offStr = "";
    			Integer status = taskVm.getTaskVmStatus();
    			switch (status) {
    			//任务配送售货机状态 1未开始 2进行中 4完成 7忽略/关闭
				case 1:
					offStr = "未发布";
					break;
				case 2:
					offStr = "已发布";
					break;
				case 4:
					offStr = "完成";
					break;
				case 7:
					offStr = "忽略/关闭";
					break;

				default:
					offStr = "未知";
					break;
				}
    			cell.setCellStyle(style);
    			//定义单元格为字符串类型 
    			cell.setCellType(HSSFCell.ENCODING_UTF_16);  
    			cell.setCellValue(new HSSFRichTextString(testString[i]));
    			sheet.setColumnWidth(0, (short) (5000));
    			cell = row.getCell(1);
    			if (null == cell) {//列不为空
    				cell = row.createCell(1);
    			}
    			cell.setCellStyle(style);
    			//定义单元格为字符串类型  
    			cell.setCellType(HSSFCell.ENCODING_UTF_16);
    			cell.setCellValue(new HSSFRichTextString(offStr));
    			sheet.setColumnWidth(1, (short) (10000));
    		}else{
    			cell.setCellStyle(style);
    			//定义单元格为字符串类型 
    			cell.setCellType(HSSFCell.ENCODING_UTF_16);  
    			cell.setCellValue(new HSSFRichTextString(testString[i]));
    			sheet.setColumnWidth(0, (short) (5000));
    			cell = row.getCell(1);
    			if (null == cell) {//列不为空
    				cell = row.createCell(1);
    			}
    			cell.setCellStyle(style);
    			//定义单元格为字符串类型  
    			cell.setCellType(HSSFCell.ENCODING_UTF_16);
    			cell.setCellValue(new HSSFRichTextString(names[i]));
    			sheet.setColumnWidth(1, (short) (10000));
    		}
		}
	}
	
	/**
	 * 投放工单
	 * @param taskVm
	 * @param wb
	 * @param sheet
	 */
	private void packLaunchWorkOrder(TTaskVm taskVm, HSSFWorkbook wb,
			HSSFSheet sheet) {


		HSSFCellStyle style = setStype(wb);
        HSSFRow row = null;//行，一行包括多个单元格
        HSSFCell cell;//单元格
        SSOToken token = TokenUtil.getToken();
        String ownerName = "";
        if(taskVm.getOwner() != null){
        	TSysUser owner = sysUserDao.findUserByPk(token.getObjectContext(), taskVm.getOwner());
        	if(null != owner){
        		ownerName = owner.getUserName();
        	}
        }
        String[] testString = {"任务ID","售货机编号","点位","具体位置","负责人","状态","执行日期","创建人","创建时间"};
        String[] names = {taskVm.getTaskVmId()+"",taskVm.getMtVm().getInnerCode(),taskVm.getMtVm().getNode().getNodeName(),taskVm.getMtVm().getNode().getNodeAddress(),
        		ownerName
        		,"",StringUtil.dateToStr(taskVm.getTaskTime(), StringUtil.YMDPattern),
        		sysUserDao.findUserByPk(token.getObjectContext(), taskVm.getCtor()).getUserName(),StringUtil.dateToStr(taskVm.getCtime(), StringUtil.Pattern)};
        for (int i = 0; i < testString.length; i++) {
        	row = sheet.createRow(i);
            row.setHeightInPoints((float) 30);
            cell = row.getCell(0);
    		if (null == cell) {//列不为空
    			cell = row.createCell(0);
    		}
    		
    		if(i==5){
    			String offStr = "";
    			Integer status = taskVm.getTaskVmStatus();
    			switch (status) {
    			//任务配送售货机状态 1未开始 2进行中 4完成 7忽略/关闭
				case 1:
					offStr = "未发布";
					break;
				case 2:
					offStr = "已发布";
					break;
				case 4:
					offStr = "完成";
					break;
				case 7:
					offStr = "忽略/关闭";
					break;

				default:
					offStr = "未知";
					break;
				}
    			cell.setCellStyle(style);
    			//定义单元格为字符串类型 
    			cell.setCellType(HSSFCell.ENCODING_UTF_16);  
    			cell.setCellValue(new HSSFRichTextString(testString[i]));
    			sheet.setColumnWidth(0, (short) (5000));
    			cell = row.getCell(1);
    			if (null == cell) {//列不为空
    				cell = row.createCell(1);
    			}
    			cell.setCellStyle(style);
    			//定义单元格为字符串类型  
    			cell.setCellType(HSSFCell.ENCODING_UTF_16);
    			cell.setCellValue(new HSSFRichTextString(offStr));
    			sheet.setColumnWidth(1, (short) (10000));
    		}else{
    			cell.setCellStyle(style);
    			//定义单元格为字符串类型 
    			cell.setCellType(HSSFCell.ENCODING_UTF_16);  
    			cell.setCellValue(new HSSFRichTextString(testString[i]));
    			sheet.setColumnWidth(0, (short) (5000));
    			cell = row.getCell(1);
    			if (null == cell) {//列不为空
    				cell = row.createCell(1);
    			}
    			cell.setCellStyle(style);
    			//定义单元格为字符串类型  
    			cell.setCellType(HSSFCell.ENCODING_UTF_16);
    			cell.setCellValue(new HSSFRichTextString(names[i]));
    			sheet.setColumnWidth(1, (short) (10000));
    		}
		}
	}
	/**
	 * 维修工单
	 * @param taskVm
	 * @param wb
	 * @param sheet
	 */
	private void packMaintenanceWorkOrder(TTaskVm taskVm,
			HSSFWorkbook wb, HSSFSheet sheet) {

		HSSFCellStyle style = setStype(wb);
        HSSFRow row = null;//行，一行包括多个单元格
        HSSFCell cell;//单元格
        SSOToken token = TokenUtil.getToken();
        String ownerName = "";
        if(taskVm.getOwner() != null){
        	TSysUser owner = sysUserDao.findUserByPk(token.getObjectContext(), taskVm.getOwner());
        	if(null != owner){
        		ownerName = owner.getUserName();
        	}
        }
        String[] testString = {"任务ID","售货机编号","点位","具体位置","负责人","状态","执行日期","任务说明","创建人","创建时间"};
        String[] names = {taskVm.getTaskVmId()+"",taskVm.getMtVm().getInnerCode(),taskVm.getMtVm().getNode().getNodeName(),taskVm.getMtVm().getNode().getNodeAddress()
        		,ownerName,"",StringUtil.dateToStr(taskVm.getTaskTime(), StringUtil.YMDPattern),taskVm.getTaskRemarks(),
        		sysUserDao.findUserByPk(token.getObjectContext(), taskVm.getCtor()).getUserName(),StringUtil.dateToStr(taskVm.getCtime(), StringUtil.Pattern)};
        for (int i = 0; i < testString.length; i++) {
        	row = sheet.createRow(i);
            row.setHeightInPoints((float) 30);
            cell = row.getCell(0);
    		if (null == cell) {//列不为空
    			cell = row.createCell(0);
    		}
    		if(i==5){
    			String offStr = "";
    			Integer status = taskVm.getTaskVmStatus();
    			switch (status) {
    			//任务配送售货机状态 1未开始 2进行中 4完成 7忽略/关闭
				case 1:
					offStr = "未发布";
					break;
				case 2:
					offStr = "已发布";
					break;
				case 4:
					offStr = "完成";
					break;
				case 7:
					offStr = "忽略/关闭";
					break;

				default:
					offStr = "未知";
					break;
				}
    			cell.setCellStyle(style);
    			//定义单元格为字符串类型 
    			cell.setCellType(HSSFCell.ENCODING_UTF_16);  
    			cell.setCellValue(new HSSFRichTextString(testString[i]));
    			sheet.setColumnWidth(0, (short) (5000));
    			cell = row.getCell(1);
    			if (null == cell) {//列不为空
    				cell = row.createCell(1);
    			}
    			cell.setCellStyle(style);
    			//定义单元格为字符串类型  
    			cell.setCellType(HSSFCell.ENCODING_UTF_16);
    			cell.setCellValue(new HSSFRichTextString(offStr));
    			sheet.setColumnWidth(1, (short) (10000));
    		}else{
    			cell.setCellStyle(style);
    			//定义单元格为字符串类型 
    			cell.setCellType(HSSFCell.ENCODING_UTF_16);  
    			cell.setCellValue(new HSSFRichTextString(testString[i]));
    			sheet.setColumnWidth(0, (short) (5000));
    			cell = row.getCell(1);
    			if (null == cell) {//列不为空
    				cell = row.createCell(1);
    			}
    			cell.setCellStyle(style);
    			//定义单元格为字符串类型  
    			cell.setCellType(HSSFCell.ENCODING_UTF_16);
    			cell.setCellValue(new HSSFRichTextString(names[i]));
    			sheet.setColumnWidth(1, (short) (10000));
    		}
		}
	
		
	}
	private void packDistributionWorkOrder(TTaskVm taskVm, HSSFWorkbook wb,
			HSSFSheet sheet) {

		SSOToken token = TokenUtil.getToken();
		HSSFCellStyle style = setStype(wb);
        HSSFRow row = null;//行，一行包括多个单元格
        HSSFCell cell;//单元格
        String ownerName = "";
        if(taskVm.getOwner() != null){
        	TSysUser owner = sysUserDao.findUserByPk(token.getObjectContext(), taskVm.getOwner());
        	if(null != owner){
        		ownerName = owner.getUserName();
        	}
        }
        String[] testString = {"任务ID","售货机编号","点位","具体位置","负责人","状态","执行日期","创建人","创建时间"};
        String[] names = {taskVm.getTaskVmId()+"",taskVm.getMtVm().getInnerCode(),taskVm.getMtVm().getNode().getNodeName(),taskVm.getMtVm().getNode().getNodeAddress(),
        		ownerName
        		,"",StringUtil.dateToStr(taskVm.getTaskTime(), StringUtil.YMDPattern),
        		sysUserDao.findUserByPk(token.getObjectContext(), taskVm.getCtor()).getUserName(),StringUtil.dateToStr(taskVm.getCtime(), StringUtil.Pattern)};
        for (int i = 0; i < testString.length; i++) {
        	row = sheet.createRow(i);
            row.setHeightInPoints((float) 30);
            cell = row.getCell(0);
    		if (null == cell) {//列不为空
    			cell = row.createCell(0);
    		}
    		if(i==5){
    			String offStr = "";
    			Integer status = taskVm.getTaskVmStatus();
    			switch (status) {
    			//任务配送售货机状态 1未开始 2进行中 4完成 7忽略/关闭
				case 1:
					offStr = "未发布";
					break;
				case 2:
					offStr = "已发布";
					break;
				case 4:
					offStr = "完成";
					break;
				case 7:
					offStr = "忽略/关闭";
					break;

				default:
					offStr = "未知";
					break;
				}
    			cell.setCellStyle(style);
    			//定义单元格为字符串类型 
    			cell.setCellType(HSSFCell.ENCODING_UTF_16);  
    			cell.setCellValue(new HSSFRichTextString(testString[i]));
    			sheet.setColumnWidth(0, (short) (5000));
    			cell = row.getCell(1);
    			if (null == cell) {//列不为空
    				cell = row.createCell(1);
    			}
    			cell.setCellStyle(style);
    			//定义单元格为字符串类型  
    			cell.setCellType(HSSFCell.ENCODING_UTF_16);
    			cell.setCellValue(new HSSFRichTextString(offStr));
    			sheet.setColumnWidth(1, (short) (10000));
    		}else{
    			cell.setCellStyle(style);
    			//定义单元格为字符串类型 
    			cell.setCellType(HSSFCell.ENCODING_UTF_16);  
    			cell.setCellValue(new HSSFRichTextString(testString[i]));
    			sheet.setColumnWidth(0, (short) (5000));
    			cell = row.getCell(1);
    			if (null == cell) {//列不为空
    				cell = row.createCell(1);
    			}
    			cell.setCellStyle(style);
    			//定义单元格为字符串类型  
    			cell.setCellType(HSSFCell.ENCODING_UTF_16);
    			cell.setCellValue(new HSSFRichTextString(names[i]));
    			sheet.setColumnWidth(1, (short) (10000));
    		}
		}
	
		
	}
	public HSSFCellStyle setStype(HSSFWorkbook wb) {
		//设置标题字体
        HSSFFont font = wb.createFont();
        /* 产品要求字体10，不加粗
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);*/
		font.setFontHeightInPoints((short) 10);
        //设置标题样式
        HSSFCellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setWrapText(true);//自动换行
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);//内容左右居中
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//内容垂直居中
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);//表格的上面的线
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);//下边框
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        
        //设置表格内容字体
        HSSFFont font2 = wb.createFont();  
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);  
        //设置表格内容样式
        HSSFCellStyle style2 = wb.createCellStyle();  
        style2.setFont(font2);
        style2.setAlignment(HSSFCellStyle.ALIGN_LEFT);//内容左对齐
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//内容垂直居中
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);//表格的上面的线
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);//下边框
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
		return style;
	}
}
