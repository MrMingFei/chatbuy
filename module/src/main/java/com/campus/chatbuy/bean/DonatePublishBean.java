package com.campus.chatbuy.bean;

/**
 * Created by jinku on 2017/12/12.
 */
public class DonatePublishBean extends GoodsPublishBean {

    private Integer deliveryType;//配送方式：1=自己送；2=上门取
    private String pickPlace;//取货地点
    private Integer pickTimeWeek;//取货时间：星期几，例如1=星期一
    private Integer pickTimeDay;//取货时间：1=上午;2=下午

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
}
