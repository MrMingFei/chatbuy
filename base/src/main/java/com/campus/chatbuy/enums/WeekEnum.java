package com.campus.chatbuy.enums;

/**
 * Created by jinku on 2017/12/19.
 */
public enum WeekEnum {

    Week_01(1, "周一"), Week_02(2, "周二"), Week_03(3, "周三"), Week_04(4, "周四"),
    Week_05(5, "周五"), Week_06(6, "周六"), Week_07(7, "周日");

    public int key;
    public String remark;

    WeekEnum(int key, String remark) {
        this.key = key;
        this.remark = remark;
    }

    public static WeekEnum fromKey(Integer key) {
        if(key == null){
            return null;
        }
        for (WeekEnum we : WeekEnum.values()) {
            if (we.key == key) {
                return we;
            }
        }
        return null;
    }
}
