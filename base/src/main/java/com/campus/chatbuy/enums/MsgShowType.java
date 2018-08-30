package com.campus.chatbuy.enums;

/**
 * Created by jinku on 2017/11/21.
 */
public enum MsgShowType {

    Both(0, "双方都可见"), Only_Sender(1,"仅发送方可见"), Only_Receiver(2, "仅接收方可见");

    public int key;
    public String remark;

    MsgShowType(int key, String remark) {
        this.key = key;
        this.remark = remark;
    }
}
