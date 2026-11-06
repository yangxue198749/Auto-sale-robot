package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.*;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.custom.ChannelConfigCache;
import com.kdx.vms.service.IMtVmChannelService;
import com.kdx.vms.service.IStVmChannelService;
import com.kdx.vms.service.ISysLogService;
import com.kdx.vms.service.ITaskVmService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yangshuxi on 2016/12/14.
 */
@Controller
@RequestMapping(value = "mtVmChannel")
public class MtVmChannelController {
    static String PATH = "mtVmChannel";
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IMtVmChannelService mtVmChannelService;
    @Autowired
    private IStVmChannelService stVmChannelService;
    @Autowired
    private ISysLogService sysLogService;
    @Resource
    private ITaskVmService taskVmService;

    @RequestMapping("list")
    public String list(HttpServletRequest request, Integer pageNo, Integer[] ids) {
        try {
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            TMtVm firstMtVm = Cayenne.objectForPK(context, TMtVm.class, ids[0]);
            if (ids.length == 1) {
                //允许单独配置
                //获取即将应用的货道信息或者现在的货道信息
                ChannelConfigCache cache = mtVmChannelService.getChannelCache(token.getSysUser(), firstMtVm);
                String html = mtVmChannelService.makeStVmChannelTableString(firstMtVm, cache);
                request.setAttribute("html", html);
            }
            request.setAttribute("mtVm", firstMtVm);
            request.setAttribute("ids", ids);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mtVm/dialog/channel/mt_vm_channel";
    }

    @RequestMapping("page")
    public String page(HttpServletRequest request, Integer pageNo, String strategyName, Integer modelId) {
        if (pageNo == null) pageNo = 1;
        SSOToken token = TokenUtil.getToken();
        ObjectContext context = token.getObjectContext();
        TSysUser currentUser = token.getSysUser();
        String url = PATH + "/page";
        List totalList = stVmChannelService.getPage(context, currentUser, strategyName, modelId, 5);
        List subList = PageUtil.formatList(pageNo, 5, totalList);
        try {
            request.setAttribute("strategyName", strategyName);
            request.setAttribute("subList", subList);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("pageSize", 5);
            request.setAttribute("url", url);
            request.setAttribute("totalSize", totalList.size());
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mtVm/dialog/channel/mt_vm_channel_table";
    }

    /**
     * 操作后刷新货道详情
     */
    @RequestMapping("getChannelDetail")
    @ResponseBody
    public Map<String, Object> getChannelDetail(HttpServletRequest request, Integer vmId) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            SSOToken token = TokenUtil.getToken();
            TSysUser currentUser = token.getSysUser();
            TMtVm mtVm = Cayenne.objectForPK(currentUser.getObjectContext(), TMtVm.class, vmId);
            if (null == mtVm) {
                resultMap.put("type", "error");
                return resultMap;
            }
            ChannelConfigCache cache = mtVmChannelService.getChannelCache(token.getSysUser(), mtVm);
            String html = mtVmChannelService.makeStVmChannelTableString(mtVm, cache);
            resultMap.put("html", html);
            resultMap.put("type", "success");
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            resultMap.put("type", "error");
        }
        return resultMap;
    }


    @RequestMapping("relateSku")
    @ResponseBody
    public Map<String, Object> relateSku(HttpServletRequest request, Integer[] channelIds, Integer skuId, Integer vmId) {

        Map<String, Object> resultMap = new HashMap<>();
        try {
            SSOToken token = TokenUtil.getToken();
            TMtVm mtVm = Cayenne.objectForPK(token.getObjectContext(), TMtVm.class, vmId);
            ChannelConfigCache cache = mtVmChannelService.getChannelCache(token.getSysUser(), mtVm);
            resultMap = mtVmChannelService.relateSku(token.getSysUser(), cache, channelIds, skuId);
            mtVmChannelService.updateChannelCache(cache);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            resultMap.put("type", "error");
        }
        return resultMap;
    }

    @RequestMapping("cancelRelate")
    @ResponseBody
    public Map<String, Object> cancelRelate(HttpServletRequest request, Integer[] channelIds, Integer vmId) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            SSOToken token = TokenUtil.getToken();
            TMtVm mtVm = Cayenne.objectForPK(token.getObjectContext(), TMtVm.class, vmId);
            ChannelConfigCache cache = mtVmChannelService.getChannelCache(token.getSysUser(), mtVm);
            String errMsg = mtVmChannelService.cancelRelateSku(cache, Arrays.asList(channelIds));
            if (StringUtil.isEmpty(errMsg)) {
                String html = mtVmChannelService.makeStVmChannelTableString(mtVm, cache);
                resultMap.put("type", "success");
                resultMap.put("html", html);
            } else {
                resultMap.put("type", "error");
                resultMap.put("message", errMsg);
            }
            mtVmChannelService.updateChannelCache(cache);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            resultMap.put("type", "error");
        }
        return resultMap;
    }

    @RequestMapping("mergeChannel")
    @ResponseBody
    public Map<String, Object> mergeChannel(HttpServletRequest request, Integer firstChannelId, Integer secondChannelId, Integer vmId) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            SSOToken token = TokenUtil.getToken();
            TMtVm mtVm = Cayenne.objectForPK(token.getObjectContext(), TMtVm.class, vmId);
            ChannelConfigCache cache = mtVmChannelService.getChannelCache(token.getSysUser(), mtVm);
            logger.info("merge channel begin" + firstChannelId + "," + secondChannelId + "，cache=" + cache);
            String errMsg = mtVmChannelService.mergeChannel(firstChannelId, secondChannelId, cache, mtVm);
            logger.info("merge channel end" + firstChannelId + "," + secondChannelId + "，cache=" + cache);
            if (StringUtil.isEmpty(errMsg)) {
                String html = mtVmChannelService.makeStVmChannelTableString(mtVm, cache);
                resultMap.put("type", "success");
                resultMap.put("html", html);
            } else {
                resultMap.put("type", "error");
                resultMap.put("message", errMsg);
            }
            mtVmChannelService.updateChannelCache(cache);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            resultMap.put("type", "error");
        }

        return resultMap;
    }

    @RequestMapping("cancelMerge")
    @ResponseBody
    public Map<String, Object> cancelMerge(HttpServletRequest request, Integer channelId, Integer vmId) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            SSOToken token = TokenUtil.getToken();
            TMtVm mtVm = Cayenne.objectForPK(token.getObjectContext(), TMtVm.class, vmId);
            ChannelConfigCache cache = mtVmChannelService.getChannelCache(token.getSysUser(), mtVm);
            logger.info("cancel merge channel channelId=" + channelId + "，cache=" + cache.toJson());
            String errMsg = mtVmChannelService.cancelMergeChannel(cache, channelId);
            logger.info("cancel merge channel channelId=" + channelId + "，cache=" + cache.toJson());
            if (StringUtil.isEmpty(errMsg)) {
                String html = mtVmChannelService.makeStVmChannelTableString(mtVm, cache);
                resultMap.put("type", "success");
                resultMap.put("html", html);
            } else {
                resultMap.put("type", "error");
                resultMap.put("message", errMsg);
            }
            mtVmChannelService.updateChannelCache(cache);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            resultMap.put("type", "error");
        }
        return resultMap;
    }

    @RequestMapping("adjustCapacity")
    @ResponseBody
    public Map<String, Object> adjustCapacity(HttpServletRequest request, Integer channelId, Integer vmId, Integer adjustNum) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            SSOToken token = TokenUtil.getToken();
            if (!CheckUtils.check(channelId) || !CheckUtils.check(vmId) || !CheckUtils.check(adjustNum)) {
                resultMap.put("code", 102);
                return ReturnCodeUtil.fillResultMap(resultMap);
            }
            if (adjustNum < 1 || adjustNum > 100) {
                resultMap.put("code", 105);
                return ReturnCodeUtil.fillResultMap(resultMap);
            }
            TMtVm mtVm = Cayenne.objectForPK(token.getObjectContext(), TMtVm.class, vmId);
            ChannelConfigCache cache = mtVmChannelService.getChannelCache(token.getSysUser(), mtVm);
            resultMap = mtVmChannelService.adjustCapacity(cache, channelId, adjustNum);
            mtVmChannelService.updateChannelCache(cache);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            resultMap.put("code", 101);
        }
        return ReturnCodeUtil.fillResultMap(resultMap);
    }

    @RequestMapping("sku")
    public String sku(HttpServletRequest request, String ids) {
        try {
            String id = ids.replace(",0", "");
            request.setAttribute("relateChannelIds", id);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return "mtVm/dialog/channel/sku_table";
    }

    @RequestMapping("sku/page")
    public String skuPage(HttpServletRequest request, Integer skuGroupTypeId, Integer skuTypeId, String skuName, String skuCode) {
        request.setAttribute("skuIds", "");
        request.setAttribute("skuName", skuName);
        request.setAttribute("skuCode", skuCode);
        request.setAttribute("skuGroupTypeId", skuGroupTypeId);
        request.setAttribute("skuTypeId", skuTypeId);
        return "mtVm/dialog/channel/sku_table_page";
    }

    @RequestMapping("save")
    @ResponseBody
    public Map<String, Object> save(Integer vmId, HttpServletRequest request) {
        SSOToken token = TokenUtil.getToken();
        ObjectContext context = token.getObjectContext();
        Map<String, Object> respMap = new HashMap<>();
        try {
            TMtVm mtVm = Cayenne.objectForPK(token.getObjectContext(), TMtVm.class, vmId);
            if (taskVmService.hasUnDealTask(context, mtVm, TTaskVm.TYPE_DELIVERY)) {
                respMap.put("type", "error");
                respMap.put("message", "售货机有未完成的配送工单,无法设置货道");
                return respMap;
            }
            if (taskVmService.hasUnDealRepairTask(context, mtVm)) {
                respMap.put("type", "error");
                respMap.put("message", "售货机有未完成的维护工单,无法设置货道");
                return respMap;
            }
            ChannelConfigCache cache = mtVmChannelService.getChannelCache(token.getSysUser(), mtVm);
            mtVmChannelService.saveChannelConfig(mtVm, token.getSysUser(), cache);
            mtVmChannelService.deleteChannelCache(vmId);//清除所有人的缓存
            List<TMtVmStrategy> stList = mtVm.getStrategy();
            boolean flag = false;
            for (TMtVmStrategy mtVmStrategy : stList) {
                if (mtVmStrategy.getStrategyType().equals(TStVmStrategy.TYPE_ST_VM_CHANNEL)) {
                    mtVmStrategy.clear(token.getSysUser().getId());
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                TMtVmStrategy mtVmStrategy = context.newObject(TMtVmStrategy.class);
                mtVmStrategy.setStrategyType(TStVmStrategy.TYPE_ST_VM_CHANNEL);
                mtVmStrategy.setMtVm(mtVm);
                mtVmStrategy.clear(token.getSysUser().getId());
            }
            context.commitChanges();
            respMap.put("code", 0);
            sysLogService.saveSysLog("单独配置售货机货道" + vmId, TSysLog.SAVE, SyslogEnum.MT_VM_CODE, token.getSysUser().getId(), context);

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            respMap.put("code", 101);
        }
        return ReturnCodeUtil.fillResultMap(respMap);
    }

    @RequestMapping("save/st")
    @ResponseBody
    public Map<String, Object> saveBySt(Integer strategyId, Integer[] ids, HttpServletRequest request) {
        SSOToken token = TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        Map<String, Object> respMap = new HashMap<>();
        ObjectContext context = token.getObjectContext();
        for (Integer id : ids) {
            TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, id);
            if (taskVmService.hasUnDealTask(context, mtVm, TTaskVm.TYPE_DELIVERY)) {
                respMap.put("type", "error");
                respMap.put("message", "售货机编号为" + mtVm.getInnerCode() + "的售货机有未完成的配送工单,无法设置货道");
                return respMap;
            }
            if (taskVmService.hasUnDealRepairTask(context, mtVm)) {
                respMap.put("type", "error");
                respMap.put("message", "售货机编号为" + mtVm.getInnerCode() + "的售货机有未完成的维护工单,无法设置货道");
                return respMap;
            }
        }
        respMap = mtVmChannelService.saveChannel(currentUser, context, ids, strategyId);
        for (Integer vmId : ids) {
            //清除缓存
            mtVmChannelService.deleteChannelCache(vmId);////清除所有人的缓存
        }
        return ReturnCodeUtil.fillResultMap(respMap);
    }

//    public void distinct(TMtVm mtVm) {
//        try {
//            List<TMtVmSkuSortNext> skuSortNextList = mtVm.getSkuSortNext();
//            List<TMtVmSkuSortNext> distinctSortList = skuSortNextList.stream().filter(SystemUtils.distinctByKey(TMtVmSkuSortNext::getSkuId)).collect(Collectors.toList());
//            List<TMtVmSkuSortNext> repeatSortList = new ArrayList<>();
//            repeatSortList.addAll(skuSortNextList);
//            repeatSortList.removeAll(distinctSortList);
//            mtVm.getObjectContext().deleteObjects(repeatSortList);
//            mtVm.getObjectContext().commitChanges();
//        } catch (Exception e) {
//            logger.error(e.getLocalizedMessage(), e);
//            mtVm.getObjectContext().rollbackChanges();
//        }
//    }

    public void clean(TMtVm mtVm) {
        try {
            List<TMtVmSkuSortNext> skuSortNextList = mtVm.getSkuSortNext();
            Map<String, TMtVmChannelNext> channelNextMap = mtVm.getChannelNext();
            List<TMtVmChannelNext> channelNextList = new ArrayList<>();
            channelNextMap.forEach((s, tMtVmChannelNext) -> channelNextList.add(tMtVmChannelNext));
            List<Integer> channelSkuIdList = channelNextList.stream()
                    .filter(tMtVmChannelNext -> tMtVmChannelNext.getSku() != null)
                    .map(TMtVmChannelNext::getSku)
                    .map(TMtSku::getId)
                    .distinct()
                    .collect(Collectors.toList());
            skuSortNextList.forEach(tMtVmSkuSortNext -> {
                if (!channelSkuIdList.contains(tMtVmSkuSortNext.getSkuId())) {
                    tMtVmSkuSortNext.getObjectContext().deleteObject(tMtVmSkuSortNext);
                }
            });
            mtVm.getObjectContext().commitChanges();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            mtVm.getObjectContext().rollbackChanges();
        }
    }

    @RequestMapping("showFullDialog")
    public String showFullDialog(HttpServletRequest request, Integer vmId, String channelId) {
        request.setAttribute("vmId", vmId);
        request.setAttribute("channelId", channelId);
        return "mtVm/dialog/channel/set_full_sku_dialog";
    }

    @RequestMapping("editSkuFull")
    @ResponseBody
    public Map<String, Object> editSkuFull(Integer vmId, String channelCode, Integer fullval) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            Objects.nonNull(vmId);
            Objects.nonNull(fullval);
            Objects.nonNull(channelCode);
            TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, vmId);
            resultMap = mtVmChannelService.setFullSku(context, mtVm, channelCode, fullval);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            resultMap.put("type", "error");
            resultMap.put("message", "售货机货道或满量不能为空");

        }
        return resultMap;
    }
}
