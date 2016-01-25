package com.asiainfo.selforder.model.order;

import java.util.ArrayList;

/**
 * 订单商品内容子表
 *
 * @author zhouwx
 */
public class ServerOrderGoods {

    private Long orderId;

    private Long salesId;

    private String salesName;

    private Integer salesNum;

    private Long salesPrice;

    private ArrayList<String> remark;

    private String  oldremark;

    private String dishesSurl;

    private Long dishesPrice;

    private String salesState;

    private String createTime;

    private String dishesTypeCode;

    private String dishesTypeName;

    private String tradeStaffId;

    private String tradeRemark;

    private Long interferePrice;

    private Integer exportId;

    private String instanceId;

    private String isComp;

    /**
     * 用来存放该数据接下来的处理类型
     * 0 删除
     * 1 新增
     */
    private String dataType;

    private String dishesCode;

    private Long deskId;

    private boolean isCompDish;

    private int action;

    private Long compId;

    private String isZdzk;

    public Long getMemberPrice() {
        return memberPrice;
    }

    public void setMemberPrice(Long memberPrice) {
        this.memberPrice = memberPrice;
    }

    public String getIsZdzk() {
        return isZdzk;
    }

    public void setIsZdzk(String isZdzk) {
        this.isZdzk = isZdzk;
    }

    private Long memberPrice;

    public String getTradeStaffId() {
        return tradeStaffId;
    }

    public void setTradeStaffId(String tradeStaffId) {
        this.tradeStaffId = tradeStaffId;
    }

    public String getTradeRemark() {
        return tradeRemark;
    }

    public void setTradeRemark(String tradeRemark) {
        this.tradeRemark = tradeRemark;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getSalesId() {
        return salesId;
    }

    public void setSalesId(Long salesId) {
        this.salesId = salesId;
    }

    public String getSalesName() {
        return salesName;
    }

    public void setSalesName(String salesName) {
        this.salesName = salesName;
    }

    public Integer getSalesNum() {
        return salesNum;
    }

    public void setSalesNum(Integer salesNum) {
        this.salesNum = salesNum;
    }

    public Long getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(Long salesPrice) {
        this.salesPrice = salesPrice;
    }

    public ArrayList<String> getRemark() {
        return remark;
    }

    public void setRemark(ArrayList<String> remark) {
        this.remark = remark;
    }


    public String getDishesSurl() {
        return dishesSurl;
    }


    public void setDishesSurl(String dishesSurl) {
        this.dishesSurl = dishesSurl;
    }


    public Long getDishesPrice() {
        return dishesPrice;
    }


    public void setDishesPrice(Long dishesPrice) {
        this.dishesPrice = dishesPrice;
    }

    public String getDishesTypeCode() {
        return dishesTypeCode;
    }

    public void setDishesTypeCode(String dishesTypeCode) {
        this.dishesTypeCode = dishesTypeCode;
    }

    public String getDishesTypeName() {
        return dishesTypeName;
    }

    public void setDishesTypeName(String dishesTypeName) {
        this.dishesTypeName = dishesTypeName;
    }

    public String getSalesState() {
        return salesState;
    }

    public void setSalesState(String salesState) {
        this.salesState = salesState;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Long getInterferePrice() {
        return interferePrice;
    }

    public String getDishesCode() {
        return dishesCode;
    }

    public void setDishesCode(String dishesCode) {
        this.dishesCode = dishesCode;
    }

    public void setInterferePrice(Long interferePrice) {
        this.interferePrice = interferePrice;
    }

    public Integer getExportId() {
        return exportId;
    }

    public void setExportId(Integer exportId) {
        this.exportId = exportId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getIsComp() {
        return isComp;
    }

    public void setIsComp(String isComp) {
        this.isComp = isComp;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Long getDeskId() {
        return deskId;
    }

    public void setDeskId(Long deskId) {
        this.deskId = deskId;
    }

    public boolean isCompDish() {
        return isCompDish;
    }

    public void setCompDish(boolean isCompDish) {
        this.isCompDish = isCompDish;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }


    public Long getCompId() {
        return compId;
    }

    public void setCompId(Long compId) {
        this.compId = compId;
    }

    public String getOldremark() {
        return oldremark;
    }

    public void setOldremark(String oldremark) {
        this.oldremark = oldremark;
    }

}

