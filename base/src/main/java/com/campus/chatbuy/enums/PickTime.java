package com.campus.chatbuy.enums;

/**
 * Created by jinku on 2017/12/19.
 */
public enum PickTime {

    Time_01(1, "18:00-20:00"), Time_02(2, "20:00-22:00");

    public int key;
    public String remark;

    PickTime(int key, String remark) {
        this.key = key;
        this.remark = remark;
    }

    public static PickTime fromKey(Integer key) {
        if(key == null){
            return null;
        }
        for (PickTime pt : PickTime.values()) {
            if (pt.key == key) {
                return pt;
            }
        }
        return null;
    }
}
