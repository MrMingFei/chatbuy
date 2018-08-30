package com.campus.chatbuy.enums;

/**
 * Created by jinku on 2017/12/19.
 * 捐赠商品配送方式:1=自己送;2上门取
 */
public enum DeliveryType {

    Self_Song(1,"自己送"), Other_Qu(2, "上门取");

    public int key;
    public String remark;

    DeliveryType(int key, String remark) {
        this.key = key;
        this.remark = remark;
    }

    public static DeliveryType fromKey(Integer key) {
        if(key == null){
            return null;
        }
        for (DeliveryType dt : DeliveryType.values()) {
            if (dt.key == key) {
                return dt;
            }
        }
        return null;
    }
}
