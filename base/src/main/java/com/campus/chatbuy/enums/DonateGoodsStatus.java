package com.campus.chatbuy.enums;

/**
 * Created by jinku on 2017/12/12.
 * 捐赠商品状态:处理状态：0=待审核;1=审核拒绝;2=待接收;3=接收拒绝;4=待投递;5=投递中;6=已送达
 */
public enum DonateGoodsStatus {

    ToDoAudit(0, "待审核"), AuditRefused(1,"审核拒绝"), ToDoReceived(2, "待接收"), ReceiveRefused(3, "接收拒绝"),
    ToDoDelivery(4, "待投递"), Delivering(5, "投递中"), Delivered(6, "已送达" );

    public int key;
    public String remark;

    DonateGoodsStatus(int key, String remark) {
        this.key = key;
        this.remark = remark;
    }
}
