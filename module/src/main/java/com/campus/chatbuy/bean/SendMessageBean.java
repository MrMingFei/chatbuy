package com.campus.chatbuy.bean;

/**
 * Created by jinku on 2017/8/30.
 */
public class SendMessageBean {

    private Long sessionId;
    private Long toUserId;
    private Integer msgType;
    private Integer businessType;
    private String msgContent;
    private Integer isComet;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public Integer getIsComet() {
        return isComet;
    }

    public void setIsComet(Integer isComet) {
        this.isComet = isComet;
    }

    @Override
    public String toString() {
        return "SendMessageBean{" +
                "sessionId=" + sessionId +
                ", toUserId=" + toUserId +
                ", msgType=" + msgType +
                ", businessType=" + businessType +
                ", msgContent='" + msgContent + '\'' +
                ", isComet=" + isComet +
                '}';
    }
}
