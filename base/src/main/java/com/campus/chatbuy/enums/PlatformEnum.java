package com.campus.chatbuy.enums;

/**
 * Created by jinku on 2018/1/14.
 */
public enum PlatformEnum {

    PC(1, "pc"), Android(2, "android"), IOS(3, "ios"), Client(4, "client");

    public int key;
    public String remark;

    PlatformEnum(int key, String remark) {
        this.key = key;
        this.remark = remark;
    }

    public static boolean isClient(String platformStr) {
        if (Client.remark.equals(platformStr) ||
                Android.remark.equals(platformStr) || IOS.equals(platformStr)) {
            return true;
        }
        return false;
    }

    public static PlatformEnum fromKey(Integer key) {
        if(key == null){
            return null;
        }
        for (PlatformEnum we : PlatformEnum.values()) {
            if (we.key == key) {
                return we;
            }
        }
        return null;
    }

    public static PlatformEnum fromStr(String platformStr) {
        if(platformStr == null){
            return null;
        }
        for (PlatformEnum we : PlatformEnum.values()) {
            if (we.remark.equals(platformStr)) {
                return we;
            }
        }
        return null;
    }
}
