package com.campus.chatbuy.bean;

/**
 * Author: baoxuebin
 * Date: 2017/8/1
 */
public class ComplainBean {

    private Long initiatorId;
    private Long targetId;
    private Long goodsId;
    private Integer reasonType;
    private String reasonDesc;

    public String getReasonDesc() {
        return reasonDesc;
    }

    public void setReasonDesc(String reasonDesc) {
        this.reasonDesc = reasonDesc;
    }

    public Long getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(Long initiatorId) {
        this.initiatorId = initiatorId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getReasonType() {
        return reasonType;
    }

    public void setReasonType(Integer reasonType) {
        this.reasonType = reasonType;
    }

    @Override
    public String toString() {
        return "ComplainBean{" +
                "initiatorId=" + initiatorId +
                ", targetId=" + targetId +
                ", goodsId=" + goodsId +
                ", reasonType=" + reasonType +
                ", reasonDesc='" + reasonDesc + '\'' +
                '}';
    }
}
