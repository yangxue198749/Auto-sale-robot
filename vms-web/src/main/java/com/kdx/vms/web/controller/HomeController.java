package com.kdx.vms.web.controller;

import com.kdx.vms.model.bean.*;
import com.kdx.vms.service.ISysUserService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.Cayenne;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class HomeController {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private ISysUserService sysUserService;
	@RequestMapping("home")
	public String home(HttpServletRequest request,String code){
		try {
			SSOToken token = TokenUtil.getToken();
			List<TSysElement> moduleList = getModual(token);
			request.setAttribute("moduleCode", code);
			request.setAttribute("moduleList", moduleList);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "home/home";
	}

	private List<TSysElement> getModual(SSOToken token) {
		List<TSysUserRole> roleList = token.getSysUser().getUserRole();
		Map<Integer,Integer> moduleMap = new HashMap<Integer,Integer>();
		for (TSysUserRole tSysUserRole : roleList) {
			List<TSysRoleElement> roleEleList = tSysUserRole.getRole().getRoleElement();
			for(TSysRoleElement roleEle:roleEleList ){
				TSysElement ele = roleEle.getElement();
				if(TSysElement.MODULE.equals(ele.getElementType())&&TSysElement.VMS_MODULE == ele.getElementModule()){
					moduleMap.put(ele.getElementIndex(), ele.getEleMentId());
				}
			}
		}
		Integer[] moduleIndexList = new Integer[moduleMap.size()];
		int i = 0;
		for (Integer elementIndex : moduleMap.keySet()){
			moduleIndexList[i] = elementIndex;
			i++;
		}
		Arrays.sort(moduleIndexList);
		List<TSysElement> moduleList = new ArrayList<TSysElement>();
		for (Integer elementIndex : moduleIndexList) {
			moduleList.add(Cayenne.objectForPK(token.getObjectContext(), TSysElement.class, moduleMap.get(elementIndex)));
		}
		return moduleList;
	}
	
	@RequestMapping("loadMenu")
	public String loadMenu(HttpServletRequest request,String menuCode,String code){
		try {
			SSOToken token = TokenUtil.getToken();
			List<TSysUserRole> roleList = token.getSysUser().getUserRole();
			Map<Integer,Integer> littleModuleMap = new HashMap<>();
			Map<Integer,Integer> menuMap = new HashMap<>();
			for (TSysUserRole tSysUserRole : roleList) {
				List<TSysRoleElement> roleEleList = tSysUserRole.getRole().getRoleElement();
				for(TSysRoleElement roleEle:roleEleList ){
					TSysElement ele = roleEle.getElement();
					if(TSysElement.LITTLE_MODULE.equals(ele.getElementType())&&ele.getParentElement().getElementCode().equals(code)){
						littleModuleMap.put(ele.getElementIndex(), ele.getEleMentId());
					}else if(TSysElement.MENU.equals(ele.getElementType())&&ele.getParentElement().getParentElement().getElementCode().equals(code)){
						menuMap.put(ele.getEleMentId(), ele.getElementIndex());
					}
				}
			}
			
			
			//已经获取到当前账号所拥有权限的菜单
			Integer[] littleModuleIndexList = new Integer[littleModuleMap.size()];
			int i = 0;
			for (Integer elementIndex : littleModuleMap.keySet()){
				littleModuleIndexList[i] = elementIndex;
				i++;
			}
			Arrays.sort(littleModuleIndexList);
			List<TSysElement> littleModuleList = new ArrayList<TSysElement>();
			for (Integer elementIndex : littleModuleIndexList) {
				littleModuleList.add(Cayenne.objectForPK(token.getObjectContext(), TSysElement.class, littleModuleMap.get(elementIndex)));
			}
			//menu
			Integer[] menuIndexList = new Integer[menuMap.size()];
			int j = 0;
			for (Integer elementIndex : menuMap.keySet()){
				menuIndexList[j] = elementIndex;
				j++;
			}
			Arrays.sort(menuIndexList);
			List<TSysElement> menuList = new ArrayList<TSysElement>();
			for (Integer elementIndex : menuIndexList) {
				menuList.add(Cayenne.objectForPK(token.getObjectContext(), TSysElement.class, elementIndex));
			}
			
			request.setAttribute("menuCode", menuCode);
			request.setAttribute("littleModuleList", littleModuleList);
			sortElement(menuList);
			request.setAttribute("menuList", menuList);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "home/leftMenu";
	}

	private void sortElement(List<TSysElement> menuList) {
		Collections.sort(menuList, new Comparator<TSysElement>(){
            /*
			* int compare(Student o1, Student o2) 返回一个基本类型的整型，
			* 返回负数表示：o1 小于o2，
			* 返回0 表示：o1和o2相等，
			* 返回正数表示：o1大于o2。
			*/
            public int compare(TSysElement o1, TSysElement o2) {

                //按照学生的年龄进行升序排列
                if(o1.getElementIndex() > o2.getElementIndex()){
                    return 1;
                }
                if(o1.getElementIndex() == o2.getElementIndex()){
                    return 0;
                }
                return -1;
            }
        });
	}

	@RequestMapping("toloadMenu")
	public String toloadMenu(HttpServletRequest request,String code){
		try {
			//对没有加载权限的用户加载权限
			SSOToken token = TokenUtil.getToken();
			if(token != null ){
				if(null ==  token.getAuthInfo() || null == token.getAuthInfo().getStringPermissions() || token.getAuthInfo().getStringPermissions().size()==0){
					TSysUser user = token.getSysUser();
					if(user != null ){
						//角色集合
						Set<String> roleSet = new HashSet<String>();
						//权限
						Set<String> permissionList = new HashSet<String>();
						sysUserService.flashRoleCache(user);
						List<TSysUserRole> roleList = user.getUserRole();
						if(roleList != null && roleList.size()>0){
							for (TSysUserRole tSysUserRole : roleList) {
								TSysRole role = tSysUserRole.getRole();
								if( null != role){
									roleSet.add(role.getId()+"");
									List<TSysRoleElement> eleList = role.getRoleElement();
									if(eleList != null && eleList.size()>0){
										for (TSysRoleElement tSysRoleElement : eleList) {
											if(null != tSysRoleElement){
												TSysElement element = tSysRoleElement.getElement();
												if(element != null && !StringUtils.isEmpty(element.getElementCode())){
													permissionList.add(element.getElementCode());
												}
											}
										}
									}
								}
							}
						}
						//为当前用户设置角色和权限
						SimpleAuthorizationInfo authorization = new SimpleAuthorizationInfo();
						authorization.addRoles(roleSet);
						authorization.addStringPermissions(permissionList);
						token.setAuthInfo(authorization);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return "home/index";
	}
	/**
	 * 无权限访问拦截页面
	 * @param request
	 */
	@RequestMapping("/unauthorized")
	public String unzuthorized(HttpServletRequest request) {
		
		return "home/unauthorized";
	}
}
