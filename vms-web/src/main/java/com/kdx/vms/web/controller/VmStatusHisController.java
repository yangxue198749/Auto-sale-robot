package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IVmStatusHisService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author zhangguodong
 *
 */
@Controller
@RequestMapping("vmStatusHis")
public class VmStatusHisController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private IVmStatusHisService vmStatusHisService;
    @RequestMapping("list")
    public String list(HttpServletRequest request){
    	
    	try {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			
			request.setAttribute("startTime", df.format(calendar.getTime()));
			request.setAttribute("endTime", df.format(new Date()));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
    	return "vmStatusHis/vm_status_his_list";
    }
    @RequestMapping("listPage")
    public String listPage(HttpServletRequest request,
                           Integer pageSize,
                           Integer pageNo,
                           @CookieValue(value = "pageSize", required  = false) Integer cookiePageSize){
        logger.info("[listPage]");
        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null)   pageNo = 1;
        if (pageSize == null) pageSize = 10;
        String url = "/vmStatusHis/listPage";
        Integer totalSize = 1000;
        try {
            // 从缓存中得到当前用户TSysUser
            SSOToken token = TokenUtil.getToken();
            TSysUser sysUser = token.getSysUser();

            String selectType = request.getParameter("selectType");
            logger.info("selectType:" + selectType);
            String selectValue = request.getParameter("selectValue");
            logger.info("selectText:" + selectValue);
            String vmStatus = request.getParameter("vmStatus");
            logger.info("vmStatus:" + vmStatus);
            String startTime = request.getParameter("startTime");
            logger.info("startTime:" + startTime);
            String endTime = request.getParameter("endTime");
            logger.info("endTime:" + endTime);
            String innerCode = null;
            String nodeName = null;
            if (!StringUtil.isEmpty(selectType)) {
                switch (selectType) {
                    case "1":
                        innerCode = selectValue;
                        break;
                    case "2":
                        nodeName = selectValue;
                        break;
                }
            }
            Integer vmStatusInt = null;
            if(!StringUtil.isEmpty(vmStatus)){
            	try {
            		vmStatusInt = Integer.parseInt(vmStatus);
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(),e);
				}
            }
            if(!StringUtil.isEmpty(startTime)){
                	Date start = StringUtil.strToDate(startTime+":00", StringUtil.Pattern);
                	if(null == start){
                		logger.debug("查询售货机历史状态时,开始时间不合法");
                		startTime = null;
                	}else{
                		startTime = startTime+":00";
                	}
            }
            if(!StringUtil.isEmpty(endTime)){
            	Date end = StringUtil.strToDate(endTime+":59", StringUtil.Pattern);
            	if(null == end){
            		logger.debug("查询售货机历史状态时,开始时间不合法");
            		endTime = null;
            	}else{
            		endTime = endTime + ":59";
            	}
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            request.setAttribute("startTime", df.format(calendar.getTime()));
            request.setAttribute("endTime", df.format(new Date()));
            if(null == startTime){
                startTime = df.format(calendar.getTime());
            }
            if(null == endTime){
                endTime =  df.format(new Date());
            }
            List list = vmStatusHisService.findListPage(token.getObjectContext(),sysUser,vmStatusInt,innerCode,nodeName,startTime,endTime,pageSize);
            logger.info("list:" + list);
            if (null != list) {
                totalSize = list.size();
                Integer start = (pageNo - 1) * pageSize;
                Integer end = pageNo * pageSize;
                start = start > list.size() ? list.size() : start;
                end = end > list.size() ? list.size() : end;
                logger.info("start:" + start + ", end:" + end);
                request.setAttribute("vmStatusHisList", list.subList(start, end));
            }
            request.setAttribute("url"      , url);
            request.setAttribute("pageSize" , pageSize);
            request.setAttribute("pageNo"   , pageNo);
            request.setAttribute("totalSize", totalSize);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    	return "vmStatusHis/page/vm_status_his_page";
    }
}
