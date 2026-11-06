package com.kdx.vms.model.bean;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.auto._TMtNode;
import org.apache.cayenne.Cayenne;

import java.util.List;

public class TMtNode extends _TMtNode {

    public Integer getId() {
        return Cayenne.intPKForObject(this);
    }
    public void setALLProperties(TMtNode mtNode){
        this.setCompany(mtNode.getCompany());
        this.setNodeName(mtNode.getNodeName());
        this.setProvinceId(mtNode.getProvinceId());
        this.setCityId(mtNode.getCityId());
        this.setAreaId(mtNode.getAreaId());
        this.setBusinessDistrictId(mtNode.getBusinessDistrictId());
        this.setNodeAddress(mtNode.getNodeAddress());
        this.setLongitude(mtNode.getLongitude());
        this.setLatitude(mtNode.getLatitude());
        this.setPartner(mtNode.getPartner());
        this.setFillType(mtNode.getFillType());
        this.setOptStartTime(mtNode.getOptStartTime());
        this.setOptEndTime(mtNode.getOptEndTime());
        this.setPutStartTime(mtNode.getPutStartTime());
        this.setPutEndTime(mtNode.getPutEndTime());
        this.setPutMethod(mtNode.getPutMethod());
        this.setPropertyName(mtNode.getPropertyName());
        this.setPropertyManager(mtNode.getPropertyManager());
        this.setPropertyPhone(mtNode.getPropertyPhone());
        this.setPropertyLevel(mtNode.getPropertyLevel());
//        this.setDelivery(mtNode.getDelivery());
//        this.setRepair(mtNode.getRepair());
//        this.setUtime(mtNode.getUtime());
//        this.setUtor(mtNode.getUtor());
//        this.setCtime(mtNode.getCtime());
//        this.setCtor(mtNode.getCtor());
    }

    /**
     * 页面用nodeName会导致冲突，增加setNodeName1方法
     * @param nodeName1
     */
    public void setNodeName1(String nodeName1){
        this.setNodeName(nodeName1);
    }
        @Override
        public  List<TMtVm>  getVm(){
            List<TMtVm> list=super.getVm();
            for (int i = 0; i <list.size() ; i++) {
                if(null!=list.get(i).getDeleteMark()&&!list.get(i).getDeleteMark().equals(0)){
                    list.remove(i);i--;
                }
            }
            return list;
        }

    public  Integer  getVmSize(){
        return this.getVm()==null?0:this.getVm().size();
    }

    public String getPutTime(){
        String startTime = getPutStartTime()==null?"":getPutStartTime();
        String endTime = getPutEndTime()==null?"":getPutEndTime();
        if(StringUtil.isEmpty(startTime)&&StringUtil.isEmpty(endTime))return "";
        else{
            return startTime+"-"+endTime;
        }
    }
    public String getOptTime(){
        String startTime = getOptStartTime()==null?"":getOptStartTime();
        String endTime = getOptEndTime()==null?"":getOptEndTime();
        if(StringUtil.isEmpty(startTime)&&StringUtil.isEmpty(endTime))return "";
        else{
            return startTime+"-"+endTime;
        }
    }
}
