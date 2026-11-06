package com.kdx.vms.web.controller;

import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TMtCustomer;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IMtCompanyService;
import com.kdx.vms.service.IMtCustomerService;
import com.kdx.vms.service.IMtVmService;
import com.kdx.vms.service.ISysUserService;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.ObjectContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "company")
public class MtCompanyController {

	@Resource
	private IMtCompanyService iMtCompanyService;
	
	@Resource
	private IMtCustomerService iMtCustomerService;
	
	@Resource
	private IMtVmService iMtVmService;
	
	@Resource
	private ISysUserService iSysUserService;
	
	/**
	 * 公司管理首页
	 */
	@RequestMapping("home")
	@ResponseBody
	public ModelAndView homePage(){
		Boolean isUpgrade = false;
		TMtCompany company = TokenUtil.getToken().getSysUser().getCompany();
		if(company.getCompanyLevel() == TMtCompany.LEVEL_THREE && company.getParentCompany().getCompanyLevel() == TMtCompany.LEVEL_ONE) isUpgrade = true;
		ModelAndView modelAndView = new ModelAndView("mtCompany/company_list", "isUpgrade", isUpgrade);
		if(company.getCompanyLevel() != TMtCompany.LEVEL_THREE){//如果不是是三级公司
			modelAndView.addObject("maxSum", company.getMaxVmCapacity());//未使用的售货机
			modelAndView.addObject("isCreateCompany", true);//可以新建公司
		}
        return modelAndView;
	}

	@RequestMapping(value = "create")
	@ResponseBody
	public Map<String, Object> createCompany(String companyName, String customerId, String adminName, String adminAccount,
			String userPhone, Integer maxVmCapacity) {
		return iMtCompanyService.addCompany(companyName, customerId, adminName, adminAccount, userPhone,
				maxVmCapacity, TokenUtil.getToken().getSysUser(), TokenUtil.getToken().getObjectContext());
	}
	
	/**
	 * 升级公司
	 */
	@RequestMapping(value = "upgrade")
	@ResponseBody
	public Map<String, Object> upgradeCompany(String companyName ,String newAdminUserId){
		return iMtCompanyService.upgradeCompany(companyName, newAdminUserId, TokenUtil.getToken().getSysUser(), TokenUtil.getToken().getObjectContext());
	}
	
	/**
	 * 升级公司页面
	 */
	@RequestMapping(value = "upgradePage")
	@ResponseBody
	public ModelAndView upgradeCompanyPage(){
		TSysUser sysUser = TokenUtil.getToken().getSysUser();
		List<TSysUser> userList = new ArrayList<TSysUser>();
		for (TSysUser tSysUser : sysUser.getCompany().getUser()) {
			if(sysUser.getId() != tSysUser.getId()){
				userList.add(tSysUser);
			}
		}
		return new ModelAndView("mtCompany/dialog/upgrade_company_dialog" ,"userList" ,userList);
	}
	
	/**
	 * 公司列表
	 */
	@RequestMapping(value = "list")
	@ResponseBody
	public ModelAndView list(Integer searchType ,String searchName ,Integer pageSize ,Integer pageNo){
		if(searchType == null) searchType =0;
		String companyName = null;
		String customerName = null;
		String adminName = null;
		switch (searchType) {
		case 0:
			break;
		case 1:
			companyName = searchName;
			break;
		case 2:
			customerName = searchName;
			break;
		case 3:
			adminName = searchName;
			break;
		}
		TSysUser user = TokenUtil.getToken().getSysUser();
		ObjectContext context = TokenUtil.getToken().getObjectContext();
		return new ModelAndView("mtCompany/page/company_page", iMtCompanyService.list(companyName, customerName, adminName, pageSize, pageNo, user, context));
	}
	
	/**
	 * 未被公司关联的客户
	 */
	@RequestMapping(value = "unusedCustomer")
	@ResponseBody
	public List<TMtCustomer> getUnusedTMtCustomer(){
		return iMtCustomerService.getUnusedTMtCustomer(TokenUtil.getToken().getSysUser(), TokenUtil.getToken().getObjectContext());
	}
	
	/**
	 * 创建公司页面
	 */
	@RequestMapping(value = "createPage")
	@ResponseBody
	public ModelAndView addPage(){
		Map<String,Object> map = new HashMap<String, Object>();
		TSysUser user = TokenUtil.getToken().getSysUser();
		TMtCompany company = user.getCompany();
		if(company.getCompanyLevel() == TMtCompany.LEVEL_TWO){//如果是二级公司
			map.put("maxSum", company.getMaxVmCapacity());//未使用的售货机
			map.put("customerName", company.getCustomer().getCustomerName());
		}else{
			map.put("maxSum", company.getMaxVmCapacity());
			map.put("customerList", iMtCustomerService.getUnusedTMtCustomer(TokenUtil.getToken().getSysUser(), TokenUtil.getToken().getObjectContext()));
		}
		return new ModelAndView("mtCompany/dialog/add_company_dialog", map);
	}
	
	/**
	 * 更新公司
	 */
	@RequestMapping(value = "update")
	@ResponseBody
	public Integer updateCompany(String ids ,String companyName){
		return iMtCompanyService.updateCompany(ids, companyName, TokenUtil.getToken().getSysUser(), TokenUtil.getToken().getObjectContext());
	}
	
	/**
	 * 更新公司页面
	 */
	@RequestMapping(value = "updatePage")
	@ResponseBody
	public ModelAndView updatePage(HttpServletRequest request, String ids){
		request.setAttribute("ids", ids);
		return new ModelAndView("mtCompany/dialog/edit_company_dialog");
	}
	
	/**
	 * 分配售货机页面
	 */
	@RequestMapping(value = "addCapacityPage")
	public ModelAndView addCapacityPage(Integer ids){
		TSysUser user = TokenUtil.getToken().getSysUser();
		ObjectContext objectContext = TokenUtil.getToken().getObjectContext();
		TMtCompany company = user.getCompany();
		ModelAndView modelAndView = new ModelAndView("mtCompany/dialog/addCapacity_company_dialog","ids",ids);
		if(company.getId().equals(ids)){//不能给自己分配机器
			modelAndView.addObject("isAddCapacityPage", false);
			return modelAndView;
		}
		
		List<TMtVm> tMtVmList = iMtVmService.findMtVmListByCompanyId(objectContext, ids);
		modelAndView.addObject("minSum", tMtVmList.size());//已使用数量
		
		Integer vmCapacitySum = iMtCompanyService.getTMtCompanyById(ids, objectContext).getMaxVmCapacity();
		modelAndView.addObject("useSum", vmCapacitySum);//当前数量
		modelAndView.addObject("maxSum", vmCapacitySum + company.getMaxVmCapacity());//最大可分配的数量
		return modelAndView;
	}
	
	/**
	 * 分配售货机
	 */
	@RequestMapping(value = "addCapacity")
	@ResponseBody
	public Boolean addCapacity(String ids ,Integer oldVmCapacitySum ,Integer newVmCapacitySum){
		TSysUser user = TokenUtil.getToken().getSysUser();
		TMtCompany company = user.getCompany();
		if(company.getCompanyLevel() == TMtCompany.LEVEL_THREE){
			return false;
		}
		return iMtCompanyService.addCapacity(ids, newVmCapacitySum-oldVmCapacitySum, user, TokenUtil.getToken().getObjectContext());
	}
	
	/**
	 * 验证账号是否被使用
	 * @param adminAccount
	 * @date 2016年12月22日 下午2:40:26
	 */
	@RequestMapping(value = "verifyAccount")
	@ResponseBody
	public Boolean verifyAccount(String adminAccount){
		if( iSysUserService.getUserByAccount(TokenUtil.getToken().getObjectContext(), adminAccount) != null) return false;
		return true;
	}

	@RequestMapping("exportOrUnStock")
	@ResponseBody
	public Map<String,Object> exportOrUnStock(String ids){
		ObjectContext context = TokenUtil.getToken().getObjectContext();
		return iMtCompanyService.exportOrUnStock(ids,context);

	}

}
