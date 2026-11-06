package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SyslogEnum;
import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.model.bean.TMtSim;
import com.kdx.vms.model.bean.TSysLog;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.ISimService;
import com.kdx.vms.web.log.AddLog;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 *
 * Created by liyang on 16-11-30.
 */
@Controller
@RequestMapping(value = "sim")
public class SimController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource()
    private ISimService simService;

    @RequestMapping("list")
    public String list() {
        return "sim/sim_list";
    }

    @RequestMapping("listPage")
    public String listPage(HttpServletRequest request, Integer pageSize, Integer pageNo, @CookieValue(value = "pageSize", required  = false) Integer cookiePageSize){
        logger.info("[listPage]");
        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null)   pageNo = 1;
        if (pageSize == null) pageSize = 10;
        String url = "listPage";
        Integer totalSize = 1000;
        try {
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            TSysUser sysUser = token.getSysUser();
            String selectTypePar      = request.getParameter("selectType");
            logger.debug("selectType:" + selectTypePar);
            String selectTextPar      = request.getParameter("selectText");
            logger.debug("selectText:" + selectTextPar);
            String serviceProviderPar = request.getParameter("serviceProvider");
            logger.debug("serviceProvider:" + serviceProviderPar);
            String simUsePar          = request.getParameter("simUse");
            logger.debug("simUse:" + simUsePar);
            String simPhone  = null;
            String simNumber = null;
            if (null != selectTypePar) {
                switch (selectTypePar) {
                    case "1":
                        simPhone = selectTextPar;
                        break;
                    case "2":
                        simNumber = selectTextPar;
                        break;
                }
            }
            Integer serviceProvider = null;
            if (!StringUtil.isEmpty(serviceProviderPar)) {
                try {
                    serviceProvider = Integer.parseInt(serviceProviderPar);
                } catch (Exception ignored) {
                    serviceProvider = null;
                }
            }
            Boolean simUse = null;
            if (null != simUsePar) {
                switch (simUsePar) {
                    case "1":
                        simUse = true;
                        break;
                    case "2":
                        simUse = false;
                        break;
                }
            }
            List list = simService.getSimList(context,sysUser, simPhone, simNumber, serviceProvider, simUse, pageSize);
            logger.info("list:" + list);
            if (null != list) {
                totalSize = list.size();
                Integer start = (pageNo - 1) * pageSize;
                Integer end = pageNo * pageSize;
                start = start > list.size() ? list.size() : start;
                end = end > list.size() ? list.size() : end;
                logger.info("start:" + start + ", end:" + end);
                request.setAttribute("simList", list.subList(start, end));
            }
            request.setAttribute("url"      , url);
            request.setAttribute("pageSize" , pageSize);
            request.setAttribute("pageNo"   , pageNo);
            request.setAttribute("totalSize", totalSize);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "sim/table/sim_table";
    }

    @RequestMapping("addPage")
    public String addPage() {
        return "sim/dialog/sim_add_dialog";
    }

    @RequestMapping("editPage")
    public String editPage(HttpServletRequest request, Integer ids) {
        try {
            if (null != ids && ids >= 0) {
                SSOToken token = TokenUtil.getToken();
                ObjectContext context = token.getObjectContext();
                TMtSim tMtSim = Cayenne.objectForPK(context, TMtSim.class, ids);
                request.setAttribute("simObj", tMtSim);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "sim/dialog/sim_edit_dialog";
    }

    @ResponseBody
    @RequestMapping("addSim")
    @AddLog(logDesc="sim卡" ,logOperation =TSysLog.INSERT ,syslogEnum =SyslogEnum.SIM_CODE)
    public Map<String, Object> addSim(HttpServletRequest request) {
        try {
            SSOToken token = TokenUtil.getToken();
            TSysUser      currentUser = token.getSysUser();
            String simPhone        = request.getParameter("addSimPhone");
            String simNumber       = request.getParameter("addSimNumber");
            String serviceProvider = request.getParameter("addServiceProvider");
            SystemCode systemCode = simService.createSim(simPhone, simNumber, Integer.parseInt(serviceProvider), currentUser);
            return systemCode.toMap();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return SystemCode.FAIL.toMap();
        }
    }

    @ResponseBody
    @RequestMapping("editSim")
    public Map<String, Object> editSim(HttpServletRequest request) {
        try {
            SSOToken token = TokenUtil.getToken();
            TSysUser      currentUser = token.getSysUser();
            String simId           = request.getParameter("simId");
            String simPhone        = request.getParameter("editSimPhone");
            String simNumber       = request.getParameter("editSimNumber");
            String serviceProvider = request.getParameter("editServiceProvider");
            SystemCode systemCode = simService.editSim(Integer.parseInt(simId), simPhone, simNumber, Integer.parseInt(serviceProvider), currentUser);
            return systemCode.toMap();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return SystemCode.FAIL.toMap();
        }
    }

    @ResponseBody
    @RequestMapping("deleteSim")
    public Map<String, Object> deleteSim(Integer[] ids) {
        try {
            SSOToken token = TokenUtil.getToken();
            TSysUser      currentUser = token.getSysUser();
            SystemCode systemCode = simService.deleteSim(Arrays.asList(ids), currentUser);
            return systemCode.toMap();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return SystemCode.FAIL.toMap();
        }
    }

}
