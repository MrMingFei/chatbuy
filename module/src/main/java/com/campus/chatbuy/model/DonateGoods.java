package com.campus.chatbuy.model;

import java.util.Date;

/**
 * Created by jinku on 2017/12/3.
 *
 * 商品捐赠实体
 */
public class DonateGoods {
    private Long goodsId;//商品的id
    private Long userId;//捐赠人Id
    private Integer deliveryType;//配送方式：1=自己送；2=上门取
    private String pickPlace;//取货地点
    private Integer pickTimeWeek;//取货时间：星期几
    private Integer pickTimeDay;//取货时间段 1= 18:00-20:00/ 2=20:00-22:00
    private Integer donateStatus;//处理状态：：0=待审核;1=审核拒绝;2=待接收;3=接收拒绝;4=待投递;5=投递中;6=已送达
    private String refuseReason;//拒收原因
    private Date refuseTime;// 拒收时间
    private Date deliveryTime;//送达时间
    private Date createTime;
    private Date updateTime;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getPickPlace() {
        return pickPlace;
    }

    public void setPickPlace(String pickPlace) {
        this.pickPlace = pickPlace;
    }

    public Integer getPickTimeWeek() {
        return pickTimeWeek;
    }

    public void setPickTimeWeek(Integer pickTimeWeek) {
        this.pickTimeWeek = pickTimeWeek;
    }

    public Integer getPickTimeDay() {
        return pickTimeDay;
    }

    public void setPickTimeDay(Integer pickTimeDay) {
        this.pickTimeDay = pickTimeDay;
    }

    public Integer getDonateStatus() {
        return donateStatus;
    }

    public void setDonateStatus(Integer donateStatus) {
        this.donateStatus = donateStatus;
    }

    public String getRefuseReason() {
        return refuseReason;
    }

    public void setRefuseReason(String refuseReason) {
        this.refuseReason = refuseReason;
    }

    public Date getRefuseTime() {
        return refuseTime;
    }

    public void setRefuseTime(Date refuseTime) {
        this.refuseTime = refuseTime;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
