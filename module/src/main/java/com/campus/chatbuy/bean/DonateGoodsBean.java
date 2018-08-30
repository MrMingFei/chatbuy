package com.campus.chatbuy.bean;

import com.campus.chatbuy.enums.DeliveryType;
import com.campus.chatbuy.enums.PickTime;
import com.campus.chatbuy.enums.WeekEnum;
import org.apache.poi.ss.formula.functions.WeekNum;

import java.util.List;

/**
 * Created by jinku on 2017/12/12.
 */
public class DonateGoodsBean {
    private String goodsId;
    private String userName;
    private String userId;
    private String mobile;
    private String goodsName;
    private String goodsDesc;
    private int donateStatus;
    private int categoryId;
    private String categoryName;
    private int deliveryType;
    private String pickPlace;
    private int pickTimeWeek;
    private int pickTimeDay;
    private String refuseReason;
    private Long refuseTime;
    private Long deliveryTime;
    private Long createTime;

    private List<PicInfoBean> picList;

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public int getDonateStatus() {
        return donateStatus;
    }

    public void setDonateStatus(int donateStatus) {
        this.donateStatus = donateStatus;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(int deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getPickPlace() {
        return pickPlace;
    }

    public void setPickPlace(String pickPlace) {
        this.pickPlace = pickPlace;
    }

    public int getPickTimeWeek() {
        return pickTimeWeek;
    }

    public void setPickTimeWeek(int pickTimeWeek) {
        this.pickTimeWeek = pickTimeWeek;
    }

    public int getPickTimeDay() {
        return pickTimeDay;
    }

    public void setPickTimeDay(int pickTimeDay) {
        this.pickTimeDay = pickTimeDay;
    }

    public String getRefuseReason() {
        return refuseReason;
    }

    public void setRefuseReason(String refuseReason) {
        this.refuseReason = refuseReason;
    }

    public Long getRefuseTime() {
        return refuseTime;
    }

    public void setRefuseTime(Long refuseTime) {
        this.refuseTime = refuseTime;
    }

    public Long getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Long deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public List<PicInfoBean> getPicList() {
        return picList;
    }

    public void setPicList(List<PicInfoBean> picList) {
        this.picList = picList;
    }

    /**
     * 获取取货时间
     *
     * 周六/周日 18:00-20:00/20:00-22:00
     *
     * @return
     */
    public String getPickTimeStr() {
        if (deliveryType == DeliveryType.Other_Qu.key) {//上门送
            String weekStr = WeekEnum.fromKey(pickTimeWeek).remark;
            String timeStr = PickTime.fromKey(pickTimeDay).remark;
            return weekStr + " " + timeStr;
        }
        return "";
    }
}
