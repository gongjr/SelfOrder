package com.asiainfo.selforder.model;

/**
 * Created by gjr on 2015/12/22.
 */
public class MerchantDesk {
    /**分店ID*/
    private Long childMerchantId;

    /**餐桌号*/
    private String deskId;

    /**餐桌名称*/
    private String deskName;

    /**被并餐桌名称*/
    private String mergeDeskName;

    /**最多容纳人数*/
    private Integer maxNum;

    /**区域位置编码*/
    private Long locationCode;

    /**餐桌类型*/
    private String deskType;

    /**餐桌状态：0-空闲可用，1-正在点菜，2-正在用餐，3-预订中*/
    private String deskState;

    private String deskStateValue;

    /**餐桌状态：0-empty，1-正在点菜，2-consume，3-locked*/
    private String deskStateCss;

    private Long operateTime;

    private String locationName;

    private String itemNames;

    private String deskTypeValue;

    public Long getChildMerchantId() {
        return childMerchantId;
    }

    public void setChildMerchantId(Long childMerchantId) {
        this.childMerchantId = childMerchantId;
    }

    public String getDeskId() {
        return deskId;
    }

    public void setDeskId(String deskId) {
        this.deskId = deskId;
    }

    public String getDeskName() {
        return deskName;
    }

    public void setDeskName(String deskName) {
        this.deskName = deskName;
    }

    public String getMergeDeskName() {
        return mergeDeskName;
    }

    public void setMergeDeskName(String mergeDeskName) {
        this.mergeDeskName = mergeDeskName;
    }

    public Integer getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Integer maxNum) {
        this.maxNum = maxNum;
    }

    public Long getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(Long locationCode) {
        this.locationCode = locationCode;
    }

    public String getDeskType() {
        return deskType;
    }

    public void setDeskType(String deskType) {
        this.deskType = deskType;
    }

    public String getDeskState() {
        return deskState;
    }

    public void setDeskState(String deskState) {
        this.deskState = deskState;
    }

    public String getDeskStateValue() {
        return deskStateValue;
    }

    public void setDeskStateValue(String deskStateValue) {
        this.deskStateValue = deskStateValue;
    }

    public String getDeskStateCss() {
        return deskStateCss;
    }

    public void setDeskStateCss(String deskStateCss) {
        this.deskStateCss = deskStateCss;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getItemNames() {
        return itemNames;
    }

    public void setItemNames(String itemNames) {
        this.itemNames = itemNames;
    }

    public String getDeskTypeValue() {
        return deskTypeValue;
    }

    public void setDeskTypeValue(String deskTypeValue) {
        this.deskTypeValue = deskTypeValue;
    }
}
