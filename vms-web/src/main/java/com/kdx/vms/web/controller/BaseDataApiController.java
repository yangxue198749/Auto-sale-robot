package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtBusinessDistrict;
import com.kdx.vms.model.bean.TMtSim;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IMtBusinessDistrictService;
import com.kdx.vms.service.IMtVmService;
import com.kdx.vms.service.IMtWarehouseService;
import com.kdx.vms.service.ISimService;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.util.*;

/**
 * 基础资料 REST API：商圈 / 仓库 / SIM 卡 / 售货机 MAC
 */
@Controller
public class BaseDataApiController {

    private static final Logger logger = LoggerFactory.getLogger(BaseDataApiController.class);
    private static final int MAX_QUERY = 10000;

    @Resource
    private IMtBusinessDistrictService businessDistrictService;

    @Resource
    private IMtWarehouseService warehouseService;

    @Resource
    private ISimService simService;

    @Resource
    private IMtVmService mtVmService;

    private TSysUser currentUser() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) return null;
        Object principal = subject.getPrincipal();
        if (!(principal instanceof SSOToken)) return null;
        return ((SSOToken) principal).getSysUser();
    }

    private static void requireAuth() {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            throw new UnauthorizedException("未登录");
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String msg) { super(msg); }
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleUnauthorized(UnauthorizedException ex) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", 401);
        err.put("message", ex.getMessage());
        return err;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleException(Exception ex) {
        logger.error("Internal error", ex);
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", 500);
        err.put("message", "服务器内部错误");
        return err;
    }

    // ==================== 商圈 ====================

    @RequestMapping(value = "/vms/api/businessDistrict/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String districtList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        Map<String, Object> model = businessDistrictService.list(pageSize, page, user, user.getObjectContext());
        Object nodeList = model.get("nodeList");
        List<Map<String, Object>> rows = new ArrayList<>();
        if (nodeList instanceof List) {
            for (Object obj : (List<?>) nodeList) {
                if (obj instanceof TMtBusinessDistrict) {
                    TMtBusinessDistrict d = (TMtBusinessDistrict) obj;
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", Cayenne.intPKForObject(d));
                    row.put("name", d.getBusinessDistrictName());
                    row.put("ctime", d.getCtime() != null ? d.getCtime().toInstant().toString() : null);
                    rows.add(row);
                }
            }
        }
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", model.get("totalSize"));
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", rows);
        return JSON.toJSONString(response);
    }

    @RequestMapping(value = "/vms/api/businessDistrict/all", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String districtAll() {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        List<Map<String, Object>> rows = new ArrayList<>();
        List<TMtBusinessDistrict> list = businessDistrictService.allList(user.getObjectContext());
        if (list != null) {
            for (TMtBusinessDistrict d : list) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", Cayenne.intPKForObject(d));
                row.put("name", d.getBusinessDistrictName());
                rows.add(row);
            }
        }
        return JSON.toJSONString(rows);
    }

    @RequestMapping(value = "/vms/api/businessDistrict/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String districtCreate(@RequestParam(value = "name", required = false) String name) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(name)) return errorJson("请填写商圈名称");

        Integer id = businessDistrictService.createBusinessDistrict(name, user, user.getObjectContext());
        if (id != null && id > 0) return successJson("新建成功");
        return errorJson("新建失败（可能重名）");
    }

    @RequestMapping(value = "/vms/api/businessDistrict/update", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String districtUpdate(
            @RequestParam(value = "ids", required = false) Integer ids,
            @RequestParam(value = "name", required = false) String name) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (ids == null || StringUtil.isEmpty(name)) return errorJson("参数不完整");

        Integer result = businessDistrictService.updateBusinessDistrict(name, String.valueOf(ids), user, user.getObjectContext());
        if (result != null && result > 0) return successJson("保存成功");
        return errorJson("保存失败");
    }

    @RequestMapping(value = "/vms/api/businessDistrict/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String districtDelete(@RequestParam(value = "ids", required = false) String idsCsv) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(idsCsv)) return errorJson("请选择要删除的商圈");

        Boolean ok = businessDistrictService.deleteBusinessDistrict(idsCsv.split(","), user, user.getObjectContext());
        if (Boolean.TRUE.equals(ok)) return successJson("删除成功");
        return errorJson("删除失败（可能已被点位使用）");
    }

    // ==================== 仓库 ====================

    @RequestMapping(value = "/vms/api/warehouse/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String warehouseList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "searchType", required = false) Integer searchType,
            @RequestParam(value = "searchValue", required = false) String searchValue) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        String idCode = searchType != null && searchType == 1 ? searchValue : null;
        String name = searchType != null && searchType == 2 ? searchValue : null;
        Map<String, Object> model = warehouseService.list(idCode, name, pageSize, page, user.getObjectContext());
        Object nodeList = model.get("nodeList");
        List<Map<String, Object>> rows = new ArrayList<>();
        if (nodeList instanceof List) {
            for (Object obj : (List<?>) nodeList) {
                if (obj instanceof com.kdx.vms.model.bean.TMtWarehouse) {
                    com.kdx.vms.model.bean.TMtWarehouse w =
                            (com.kdx.vms.model.bean.TMtWarehouse) obj;
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", Cayenne.intPKForObject(w));
                    row.put("warehouseCode", w.getWarehouseCode());
                    row.put("warehouseName", w.getWarehouseName());
                    row.put("warehouseAddress", w.getWarehouseAddress());
                    row.put("warehouseManager", w.getWarehouseManager());
                    row.put("warehousePhone", w.getWarehousePhone());
                    row.put("warehouseType", w.getWarehouseType());
                    row.put("areaCount", w.getWareHouseArea() != null ? w.getWareHouseArea().size() : 0);
                    rows.add(row);
                }
            }
        }
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", model.get("totalSize"));
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", rows);
        return JSON.toJSONString(response);
    }

    /**
     * 仓库已绑定区域
     */
    @RequestMapping(value = "/vms/api/warehouse/{id}/areas", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String warehouseAreas(@PathVariable("id") Integer id) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        List<Map<String, Object>> list = warehouseService.lookUniteArea(id, user.getObjectContext());
        return JSON.toJSONString(list == null ? Collections.emptyList() : list);
    }

    /**
     * 绑定区域（areaIds 逗号分隔 + 城市）
     */
    @RequestMapping(value = "/vms/api/warehouse/uniteArea", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String warehouseUniteArea(
            @RequestParam(value = "areaIds", required = false) String areaIdsCsv,
            @RequestParam(value = "warehouseId", required = false) Integer warehouseId,
            @RequestParam(value = "cityId", required = false) Integer cityId) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(areaIdsCsv) || warehouseId == null || cityId == null) {
            return errorJson("参数不完整");
        }
        Integer result = warehouseService.uniteArea(areaIdsCsv.split(","), warehouseId, cityId,
                user.getId(), user.getObjectContext());
        if (result != null && result > 0) return successJson("绑定成功");
        return errorJson("绑定失败");
    }

    // ==================== SIM 卡 ====================

    @RequestMapping(value = "/vms/api/sim/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String simList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "selectType", required = false) String selectType,
            @RequestParam(value = "selectText", required = false) String selectText,
            @RequestParam(value = "serviceProvider", required = false) Integer serviceProvider,
            @RequestParam(value = "simUse", required = false) String simUse) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        String simPhone = null;
        String simNumber = null;
        if ("1".equals(selectType) && !StringUtil.isEmpty(selectText)) simPhone = selectText;
        else if ("2".equals(selectType) && !StringUtil.isEmpty(selectText)) simNumber = selectText;

        Boolean use = null;
        if ("1".equals(simUse)) use = Boolean.TRUE;
        else if ("2".equals(simUse)) use = Boolean.FALSE;

        List list = simService.getSimList(user.getObjectContext(), user, simPhone, simNumber,
                serviceProvider, use, MAX_QUERY);
        int total = list == null ? 0 : list.size();
        int from = Math.min((page - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);

        List<Map<String, Object>> rows = new ArrayList<>();
        if (list != null) {
            for (int i = from; i < to; i++) {
                Object obj = list.get(i);
                if (obj instanceof TMtSim) {
                    TMtSim sim = (TMtSim) obj;
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", Cayenne.intPKForObject(sim));
                    row.put("simPhone", sim.getSimPhone());
                    row.put("simNumber", sim.getSimNumber());
                    row.put("serviceProvider", sim.getServiceProvider());
                    row.put("used", sim.getMtVm() != null);
                    row.put("vmInnerCode", sim.getMtVm() != null ? sim.getMtVm().getInnerCode() : null);
                    rows.add(row);
                }
            }
        }
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", total);
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", rows);
        return JSON.toJSONString(response);
    }

    @RequestMapping(value = "/vms/api/sim/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String simCreate(
            @RequestParam(value = "simPhone", required = false) String simPhone,
            @RequestParam(value = "simNumber", required = false) String simNumber,
            @RequestParam(value = "serviceProvider", required = false) Integer serviceProvider) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(simPhone) || StringUtil.isEmpty(simNumber) || serviceProvider == null) {
            return errorJson("请填写 SIM 卡信息");
        }
        return JSON.toJSONString(simService.createSim(simPhone, simNumber, serviceProvider, user).toMap());
    }

    @RequestMapping(value = "/vms/api/sim/edit", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String simEdit(
            @RequestParam(value = "simId", required = false) Integer simId,
            @RequestParam(value = "simPhone", required = false) String simPhone,
            @RequestParam(value = "simNumber", required = false) String simNumber,
            @RequestParam(value = "serviceProvider", required = false) Integer serviceProvider) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (simId == null) return errorJson("参数不完整");
        return JSON.toJSONString(simService.editSim(simId, simPhone, simNumber, serviceProvider, user).toMap());
    }

    @RequestMapping(value = "/vms/api/sim/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String simDelete(@RequestParam(value = "ids", required = false) String idsCsv) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(idsCsv)) return errorJson("请选择要删除的 SIM 卡");

        List<Integer> ids = new ArrayList<>();
        for (String part : idsCsv.split(",")) {
            if (!part.trim().isEmpty()) ids.add(Integer.parseInt(part.trim()));
        }
        return JSON.toJSONString(simService.deleteSim(ids, user).toMap());
    }

    // ==================== 售货机 MAC（仅一级公司） ====================

    @RequestMapping(value = "/vms/api/uniqueCode/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String uniqueCodeList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "searchType", required = false) Integer searchType,
            @RequestParam(value = "searchValue", required = false) String searchValue) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null || user.getCompany() == null) throw new UnauthorizedException("用户信息获取失败");
        if (user.getCompany().getCompanyLevel() == null
                || user.getCompany().getCompanyLevel() != com.kdx.vms.model.bean.TMtCompany.LEVEL_ONE) {
            return errorJson("仅一级公司可访问");
        }
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        List list = mtVmService.findMtVmList(user.getObjectContext(), searchType, searchValue,
                null, null, null, null, null, null, user, MAX_QUERY, Boolean.TRUE);
        int total = list == null ? 0 : list.size();
        int from = Math.min((page - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);

        List<Map<String, Object>> rows = new ArrayList<>();
        if (list != null) {
            for (int i = from; i < to; i++) {
                Object obj = list.get(i);
                if (obj instanceof TMtVm) {
                    TMtVm vm = (TMtVm) obj;
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("vmId", Cayenne.intPKForObject(vm));
                    row.put("innerCode", vm.getInnerCode());
                    row.put("uniqueCode", vm.getUniqueCode());
                    row.put("vmDesc", vm.getVmDesc());
                    if (vm.getNode() != null) row.put("nodeName", vm.getNode().getNodeName());
                    rows.add(row);
                }
            }
        }
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", total);
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", rows);
        return JSON.toJSONString(response);
    }

    /**
     * 解绑 MAC
     */
    @RequestMapping(value = "/vms/api/uniqueCode/unbind", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String uniqueCodeUnbind(@RequestParam(value = "ids", required = false) String idsCsv) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(idsCsv)) return errorJson("请选择设备");

        List<Integer> ids = new ArrayList<>();
        for (String part : idsCsv.split(",")) {
            if (!part.trim().isEmpty()) ids.add(Integer.parseInt(part.trim()));
        }
        return JSON.toJSONString(mtVmService.unbind(user, user.getObjectContext(),
                ids.toArray(new Integer[0])));
    }

    private static String successJson(String message) {
        Map<String, Object> ok = new LinkedHashMap<>();
        ok.put("code", 0);
        ok.put("type", "success");
        ok.put("message", message);
        return JSON.toJSONString(ok);
    }

    private static String errorJson(String message) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("success", false);
        err.put("type", "error");
        err.put("message", message);
        return JSON.toJSONString(err);
    }
}
