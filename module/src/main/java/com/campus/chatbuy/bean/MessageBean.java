package com.campus.chatbuy.bean;

import com.campus.chatbuy.model.Message;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jinku on 2017/11/9.
 */
public class MessageBean {

    private String messageId;
    private String sessionId;
    private String fromUserId;
    private String toUserId;
    private Integer msgType;
    private Integer businessType;
    private String msgContent;

    private Date createTime;
    private Date updateTime;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public static List<MessageBean> convertMessage(List<Message> msgList) {
        if (CollectionUtils.isEmpty(msgList)) {
            return null;
        }
        List<MessageBean> messageList = new ArrayList<>();
        for (Message message : msgList) {
            if (message == null) {
                continue;
            }
            MessageBean messageBean = new MessageBean();
            messageBean.setBusinessType(message.getBusinessType());
            messageBean.setCreateTime(message.getCreateTime());
            messageBean.setFromUserId(String.valueOf(message.getFromUserId()));
            messageBean.setToUserId(String.valueOf(message.getToUserId()));
            messageBean.setMessageId(String.valueOf(message.getMessageId()));
            messageBean.setMsgContent(message.getMsgContent());
            messageBean.setMsgType(message.getMsgType());
            messageBean.setSessionId(String.valueOf(message.getSessionId()));
            messageBean.setUpdateTime(message.getUpdateTime());
            messageList.add(messageBean);
        }
        return messageList;
    }
}
