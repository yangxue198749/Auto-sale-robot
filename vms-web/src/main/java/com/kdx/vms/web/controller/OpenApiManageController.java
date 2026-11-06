package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.custom.OpenApiInfo;
import com.kdx.vms.service.IMtCompanyService;
import com.kdx.vms.service.IOpenSecretService;
import com.kdx.vms.service.IOpenService;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.kdx.vms.commons.util.PageUtil.formatList;
import static com.kdx.vms.web.tags.TokenUtil.getToken;

/**
 * Created by guoyang on 2018/2/8.
 */
@RequestMapping("openApi")
@Controller
public class OpenApiManageController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IOpenSecretService openSecretService;
    @Autowired
    private IMtCompanyService mtCompanyService;
    @Autowired
    private IOpenService openService;

    @RequestMapping("list")
    public String list(){
        return "openAPI/openAPI";
    }
    /**
     * 接口管理列表
     * @param request
     * @param pageSize
     * @param pageNo
     * @param cookiePageSize
     * @return
     */
    @RequestMapping("listPage")
    public String listPage(HttpServletRequest request, Integer pageSize, Integer pageNo,
                           @CookieValue(value = "pageSize",required  = false) Integer cookiePageSize){
        if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
        if(pageNo == null) pageNo = 1;
        if(pageSize == null) pageSize = 10;
        String url = "listPage";
        try {
            SSOToken token = getToken();
            ObjectContext context = token.getObjectContext();
            List<TOpenSecret> list = openSecretService.findOpenApiInfo(context,pageSize);
            List<TOpenSecret> subList = PageUtil.formatList(pageNo, pageSize, list);
            request.setAttribute("url", url);
            request.setAttribute("pageSize",pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", null != list?list.size():0);
            request.setAttribute("serviceList", buildOpenApiInfo(subList));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return "openAPI/page/open_api_page";
    }

    /**
     * 添加访问接口权限页面
     * @return
     */
    @RequestMapping("addOpenApiDialog")
    public String addOpenApiDialog(HttpServletRequest request){
        try{
            request.setAttribute("openService",openService.findOpenServiceList(getToken().getObjectContext()));
        }catch(Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return "openAPI/dialog/add_open_api_dialog";
    }

    /**
     * 跳转到查询公司的模态框
     * @return
     */
    @RequestMapping("companyList")
    public String addCompanyListDialog() {

        return "openAPI/dialog/select_company_dialog";
    }
    /**
     * 公司的列表页
     * @param request
     * @param pageSize
     * @param pageNo
     * @param cookiePageSize
     * @return
     */
    @RequestMapping("companyPage")
    public String companyList(HttpServletRequest request, Integer pageSize, Integer pageNo,
                              @CookieValue(value = "pageSize",required  = false) Integer cookiePageSize,String companyName){
        if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
        if(pageNo == null) pageNo = 1;
        if(pageSize == null) pageSize = 10;
        String url = "companyPage";
        try {
            SSOToken token = getToken();
            ObjectContext context = token.getObjectContext();
            TSysUser user = token.getSysUser();
            List<TMtCompany> companyList =mtCompanyService.findCompanyPage(companyName,user.getCompany(),pageSize,context);
            request.setAttribute("url", url);
            request.setAttribute("pageSize",pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", null != companyList?companyList.size():0);
            request.setAttribute("companyList", formatList(pageNo, pageSize, companyList));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "openAPI/page/company_page";
    }

    /**
     *添加接口
     * @param serviceIds
     * @param companyId
     * @return
     */
    @RequestMapping("addOpenApiConfirm")
    @ResponseBody
    public Map<String,Object> addOpenApiConfirm(Integer[] serviceIds,Integer companyId){
        SSOToken token = getToken();
        TSysUser user =token.getSysUser();
        ObjectContext context =token.getObjectContext();
        return openSecretService.createOpenApiManage(context, serviceIds, companyId, user.getId());
    }


    /**
     * 修改访问接口权限页面
     * @param request
     * @param companyName
     * @param serviceId
     * @param customerId
     * @return
     */
    @RequestMapping("updateOpenApiDialog")
    public String updateOpenApiDialog(HttpServletRequest request, String companyName, String serviceId,String customerId){
        try {
            companyName =URLDecoder.decode(companyName,"UTF-8");
            List<String> serviceIdList =JSON.parseObject(serviceId,List.class);
            request.setAttribute("openService",openService.findOpenServiceList(getToken().getObjectContext()));
            request.setAttribute("companyName",companyName);
            request.setAttribute("serviceId", serviceIdList);
            request.setAttribute("serviceId", serviceIdList);
            request.setAttribute("customerId",customerId);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage(),e);
        }
        return "openAPI/dialog/update_open_api_dialog";
    }

    /**
     *
     * @param serviceIds
     * @param customerId
     * @return
     */
    @RequestMapping("updateOpenApiConfirm")
    @ResponseBody
    public Map<String,Object> updateOpenApiConfirm(Integer[] serviceIds,String customerId){
        SSOToken token = getToken();
        TSysUser user =token.getSysUser();
        ObjectContext context =token.getObjectContext();
        return openSecretService.updateOpenApiManage(context, serviceIds, customerId, user.getId());
    }

    @RequestMapping("deleteOpenApiDialog")
    public String deleteOpenApiDialog(HttpServletRequest request,String customerId){
        request.setAttribute("customerId",customerId);
        return "openAPI/dialog/delete_open_api_dialog";
    }

    @RequestMapping("deleteOpenApiConfirm")
    @ResponseBody
    public Map<String,Object> delete(String customerId){
        SSOToken token = getToken();
        ObjectContext context =token.getObjectContext();
        return openSecretService.deleteOpenApiManage(context,customerId);
    }

    private List<OpenApiInfo> buildOpenApiInfo(List<TOpenSecret> list) {
        List<OpenApiInfo> openApilist = new ArrayList<>();
        try {
            for (TOpenSecret tOpenSecret : list) {
                OpenApiInfo info = new OpenApiInfo();
                TMtCompany company =tOpenSecret.getCompany();
                if(company != null){
                    info.setCompanyName(company.getCompanyName());
                }
                info.setCustomerId(tOpenSecret.getPublicId());
                info.setKey(tOpenSecret.getSecretValue());
                List<TOpenSecretService> secretServiceList = tOpenSecret.getOpenSecretService();
                List<String> serviceNameList = new ArrayList<>();
                List<Integer> serviceIdList =new ArrayList<>();
                for (TOpenSecretService tOpenSecretService : secretServiceList) {
                    TOpenService openService = tOpenSecretService.getOpenServcie();
                    serviceNameList.add(openService.getServiceDesc());
                    serviceIdList.add(openService.getId());
                }
                info.setServiceId(serviceIdList);
                info.setServiceName(serviceNameList);
                openApilist.add(info);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return openApilist;
    }

}
