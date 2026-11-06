package com.kdx.vms.commons.util.mcs.message.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by liyang on 2017/2/22.
 */
public class AreaRelation implements Serializable{

    private static final long serialVersionUID = 6660955046106015857L;
    private List<Node>         nodes        = new ArrayList<>();
    private List<BusinessArea> businessArea = new ArrayList<>();
    private List<Area>         area         = new ArrayList<>();
    private List<Integer>      customer     = new ArrayList<>();
    private List<Vm>           vm           = new ArrayList<>();

    public void addNode(Integer id, String name, String address, Integer businessAreaId, Integer cityId, Integer customerId) {
        nodes.add(new Node(id, name, address, businessAreaId, cityId, customerId));
    }

    public void addBusinessArea(Integer id, String name) {
        businessArea.add(new BusinessArea(id, name));
    }

    public void addArea(Integer id, String name, Integer pid, Integer level) {
        area.add(new Area(id, name, pid, level));
    }

    public void addCustomer(Integer id) {
        customer.add(id);
    }

    public void addVm(String innerCode, Integer nodeId) {
        vm.add(new Vm(innerCode, nodeId));
    }

    class Node {

        private Integer    id;
        private String name;
        private String address;
        private Integer    businessAreaId;
        private Integer    cityId;
        private Integer    customerId;

        Node(Integer id, String name, String address, Integer businessAreaId, Integer cityId, Integer customerId) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.businessAreaId = businessAreaId;
            this.cityId = cityId;
            this.customerId = customerId;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Integer getBusinessAreaId() {
            return businessAreaId;
        }

        public void setBusinessAreaId(Integer businessAreaId) {
            this.businessAreaId = businessAreaId;
        }

        public Integer getCityId() {
            return cityId;
        }

        public void setCityId(Integer cityId) {
            this.cityId = cityId;
        }

        public Integer getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Integer customerId) {
            this.customerId = customerId;
        }
    }

    class BusinessArea {

        private Integer    id;
        private String name;

        BusinessArea(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    class Area {

        private Integer    id;
        private String  name;
        private Integer    pid;
        private Integer    level;

        Area(Integer id, String name, Integer pid, Integer level) {
            this.id = id;
            this.name = name;
            this.pid = pid;
            this.level = level;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getPid() {
            return pid;
        }

        public void setPid(Integer pid) {
            this.pid = pid;
        }

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }
    }

    class Vm {

        private String innerCode;
        private Integer    nodeId;

        Vm(String innerCode, Integer nodeId) {
            this.innerCode = innerCode;
            this.nodeId = nodeId;
        }

        public String getInnerCode() {
            return innerCode;
        }

        public void setInnerCode(String innerCode) {
            this.innerCode = innerCode;
        }

        public Integer getNodeId() {
            return nodeId;
        }

        public void setNodeId(Integer nodeId) {
            this.nodeId = nodeId;
        }
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<BusinessArea> getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(List<BusinessArea> businessArea) {
        this.businessArea = businessArea;
    }

    public List<Area> getArea() {
        return area;
    }

    public void setArea(List<Area> area) {
        this.area = area;
    }

    public List<Integer> getCustomer() {
        return customer;
    }

    public void setCustomer(List<Integer> customer) {
        this.customer = customer;
    }

    public List<Vm> getVm() {
        return vm;
    }

    public void setVm(List<Vm> vm) {
        this.vm = vm;
    }
}
