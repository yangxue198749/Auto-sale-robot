package com.kdx.vms.service.impl;

import com.kdx.vms.model.bean.TSysElement;
import com.kdx.vms.model.bean.TSysRole;
import com.kdx.vms.model.bean.TSysRoleElement;
import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;

import java.util.Date;

/**
 * Created by yangshuxi on 2017/4/25.
 */
public class AddMenu {

    public static void main(String[] args) {
        add(2010200, "menu", "/uniqueCode/list", 2010000, "uniqueCode", 1, "售货机MAC地址",false);
        add(2010201, "button", "/uniqueCode/unbind", 2010200, "uniqueCode-unbind", 1, "解除绑定",false);
        add(6010800, "menu", "/mp/delivery/list", 6010000, "mp-delivery-list", 1, "立即补货列表",true);
        add(6010801, "button", "/mp/delivery/page", 6010800, "mp-delivery-page", 1, "立即补货数据",true);
        add(6010802, "button", "/mp/delivery/check", 6010800, "mp-delivery-check", 2, "立即补货检查",true);
        add(6010803, "button", "/mp/delivery/create", 6010800, "mp-delivery-create", 3, "立即补货创建工单",true);

        add(1040500, "menu", "/theme/list", 1040000, "st-theme-list", 5, "皮肤策略",true);
        add(1040501, "button", "/theme/addTheme", 1040500, "st-vm-theme-add", 1, "新建皮肤策略",true);
        add(1040502, "button", "/theme/deleteTheme", 1040500, "st-vm-theme-delete", 2, "删除皮肤策略",true);

        add(1030113, "button", "/mtVm/mtVmTheme", 1030100, "vm-set-theme", 13, "皮肤设置",false);


        add(5010700, "menu", "/settings/list", 5010000, "settings-list", 7, "系统配置",false);
        add(5010701, "button", "/settings/type/add", 5010700, "settings-type-add", 1, "新增类型",false);
        add(5010702, "button", "/settings/setting/add", 5010700, "settings-setting-add", 2, "新增配置",false);
        add(5010703, "button", "/settings/setting/delete", 5010700, "settings-setting-delete", 3, "删除配置",false);
        add(5010704, "button", "/settings/setting/update", 5010700, "settings-setting-update", 4, "更新配置",false);
        add(5010705, "button", "/settings/type/table", 5010700, "settings-type-table", 5, "类型数据",false);
        add(5010706, "button", "/settings/setting/table", 5010700, "settings-setting-table", 6, "配置数据",false);
        add(5010707, "button", "/settings/save", 5010700, "settings-save", 7, "保存数据",false);


        add(1040600, "menu", "/mtVmResources/list", 1040000, "st-mtvm-resources-list", 5, "闪购键跳转页",false);
        add(1040601, "button", "/mtVmResources/addPage", 1040600, "st-mtvm-resources-add", 1, "新增",false);
        add(1040602, "button", "/mtVmResources/deleteMtVmResources", 1040600, "st-mtvm-resources-delete", 2, "闪购键跳转页",false);

        //升级包相关  start
        add(2070000, "little-module", "#", 2000000, "", 7, "升级管理",false);
        add(2070100, "menu", "/upgradePackage/list", 2070000, "upgradePackage-list", 1, "升级包管理",false);
        add(2070101, "button", "/upgradePackage/addUpgradePackage", 2070100, "upgradePackage-addUpgradePackage", 1, "新增升级包",false);
        add(2070102, "button", "/upgradePackage/disable", 2070100, "upgradePackage-disable", 2, "禁用/启用升级包",false);

        //升级计划相关 start
        add(2070200, "menu", "/vmBoxUpgrade/list", 2070000, "vmBoxUpgrade-list", 2, "升级计划",false);
        add(2070201, "button", "/vmBoxUpgrade/createDialog", 2070200, "vmBoxUpgrade-add", 1, "新增",false);
        //升级计划相关 end
        //升级包相关  end

        //4.0
        add(3010600, "menu", "/reserveStatistics/list", 3010000, "reserve-statistics-list", 6, "预定统计",false);

        //4.2
        add(3030000, "little-module", "#", 3000000, "", 3, "报表下载",false);
        add(3030100, "menu", "/export/report/list", 3030000, "export-report-list", 1, "报表下载",false);


        add(1030114, "button", "/mtVm/exportMtVmList", 1030100, "export-report-vm", 14, "导出",false);//售货机导出
        add(1080000, "little-module", "#", 1000000, "sku-validity", 8, "商品效期",false);//商品效期
        add(1080100, "menu", "/skuValidity/home", 1080000, "sku-validity-list", 1, "商品效期监控",false);//商品效期监控
        add(1020104, "button", "/deliveryTask/exportDelivery", 1020100, "export-delivery", 4, "导出",false);//配送工单导出
//
        add(3010500, "menu", "/vendout/home", 3010000, "vendout-list", 5, "出货流水",false);//出货流水
        add(3010501, "button", "/vendout/exportVendout", 3010500, "vendout-export", 1, "导出",false);//出货流水导出
        add(3010502, "button", "/vendout/refund", 3010500, "vendout-refund", 2, "退款",false);//出货流水导出
        add(2010105, "button", "/vmModel/lockedOrUnlocked", 2010100, "vm-model-change-locked", 1, "禁用/启用",false);//售货机型号启用/禁用

        //vms5.0.0

        add(2060200, "menu", "#", 2060000, "mbms-login", 1, "商城后台",false);//商城后台登录权限
        add(1030115, "button", "/mtVm/extendMtVmDialog", 1030100, "vm-extend", 1, "拓展",false);//拓展售货机
        add(1030116, "button", "/mtVmShelf/list", 1030100, "vm-set-shelf", 1, "列表页设置",false);//列标页设置独立
        //接口管理
        add(2080000, "little-module", "#", 2000000, "open-api", 8, "开放接口",false);
        add(2080100,"menu","/openApi/list", 2080000,"openApi-list",1,"接口管理",false);
        //独立收款
        add(4010100, "menu", "/mtBeneficiary/list", 4010000, "mt-beneficiary-list", 1, "收款参数",false);
        add(4010200, "menu", "/beneficiaryManage/list", 4010000, "beneficiary-manage-list", 2, "收款管理",false);
        //工单状态
        add(3010301, "button", "/vmStatus/dispatch", 3010300, "vm-status-dispatch", 1, "派单",false);
        add(3010302, "button", "/vmStatus/unDispatch", 3010300, "vm-status-unDispatch", 2, "取消派单",false);
        add(3010303, "button", "/vmStatus/getCompanyUserList", 3010300, "vm-alarm-subscription", 3, "告警订阅",false);

    }


    private static void add(Integer elementId, String elementType, String elementUrl, Integer pId, String elementCode, Integer elementIndex, String elementName,boolean isMpModule) {
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TSysElement element = Cayenne.objectForPK(context,TSysElement.class,elementId);
        if(null!=element)return;
        TSysElement sysElement = new TSysElement();
        sysElement.setObjectId(new ObjectId(TSysElement.class.getSimpleName(), TSysElement.ELEMENT_ID_PK_COLUMN, elementId));
        sysElement.setElementType(elementType);
        sysElement.setElementUrl(elementUrl);
        sysElement.setParentElement(Cayenne.objectForPK(context, TSysElement.class, pId));
        sysElement.setElementCode(elementCode);
        sysElement.setElementIndex(elementIndex);
        sysElement.setElementName(elementName);
        sysElement.setElementModule(0);
        if(isMpModule)sysElement.setElementModule(1);
        context.registerNewObject(sysElement);
        context.commitChanges();
        TSysRoleElement sysRoleElement = context.newObject(TSysRoleElement.class);
        sysRoleElement.setCtime(new Date());
        sysRoleElement.setCtor(1);
        sysRoleElement.setElement(sysElement);
        sysRoleElement.setRole(Cayenne.objectForPK(context, TSysRole.class,1));
        context.commitChanges();
    }
}
