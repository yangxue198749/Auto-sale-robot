package com.kdx.mp.web.controller;

import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TTaskVm;
import com.kdx.vms.service.IMcsService;
import com.kdx.vms.web.oauth.UserMap;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/23.
 */
@Controller
@RequestMapping("mp/mpTask")
public class MpTaskController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IMcsService mcsService;

    @RequestMapping("list")
    public String list() {
        return "mp/task/task_list";
    }

    @RequestMapping("lock")
    public
    @ResponseBody
    Map<String, String> lock(Integer taskId, HttpServletRequest request) {
//        TSysUser currentUser = (TSysUser) request.getSession().getAttribute("mpUser");
        TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
        ObjectContext context = currentUser.getObjectContext();
        Map<String, String> resMap = new HashMap<>();
        try {
            TTaskVm taskVm = Cayenne.objectForPK(context, TTaskVm.class, taskId);
            TMtVm mtVm = taskVm.getMtVm();
            logger.info("获取开锁密码" + mtVm.getId());
            String password = mcsService.getOpenDoorPwd(context, mtVm.getId(), null);
            logger.info("获取开锁密码" + mtVm.getId() + "结果" + password);
            resMap.put("text", password);
            logger.error("获取开锁密码" + mtVm.getId() + "结果" + password);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return resMap;
    }


}


