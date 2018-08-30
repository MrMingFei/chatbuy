package com.campus.chatbuy.bean;

public class DonateGoodsStat {
    private long waitAuditCount;    //待审核捐赠品数量
    private long auditRefuseCount;     //审核拒绝捐赠品数量
    private long waitRecivedCount;     //待接收捐赠品数量
    private long recivedRefuseCount;     //接收拒绝捐赠品数量
    private long waitDeliveryCount;     //待投递捐赠品数量
    private long inDeliveryCount;     //投递中捐赠品数量
    private long haveArrivedCount;     //已送达捐赠品数量

    public void setWaitAuditCount(long waitAuditCount) {
        this.waitAuditCount = waitAuditCount;
    }

    public long getWaitAuditCount() {
        return waitAuditCount;
    }

    public void setAuditRefuseCount(long auditRefuseCount) {
        this.auditRefuseCount = auditRefuseCount;
    }

    public long getAuditRefuseCount() {
        return auditRefuseCount;
    }

    public void setWaitRecivedCount(long waitRecivedCount) {
        this.waitRecivedCount = waitRecivedCount;
    }

    public long getWaitRecivedCount() {
        return waitRecivedCount;
    }

    public void setRecivedRefuseCount(long recivedRefuseCount) {
        this.recivedRefuseCount = recivedRefuseCount;
    }

    public long getRecivedRefuseCount() {
        return recivedRefuseCount;
    }

    public void setWaitDeliveryCount(long waitDeliveryCount) {
        this.waitDeliveryCount = waitDeliveryCount;
    }

    public long getWaitDeliveryCount() {
        return waitDeliveryCount;
    }

    public void setInDeliveryCount(long inDeliveryCount) {
        this.inDeliveryCount = inDeliveryCount;
    }

    public long getInDeliveryCount() {
        return inDeliveryCount;
    }

    public void setHaveArrivedCount(long haveArrivedCount) {
        this.haveArrivedCount = haveArrivedCount;
    }

    public long getHaveArrivedCount() {
        return haveArrivedCount;
    }
}
