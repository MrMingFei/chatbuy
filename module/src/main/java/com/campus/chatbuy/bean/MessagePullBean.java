package com.campus.chatbuy.bean;

import com.campus.chatbuy.model.Message;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinku on 2017/11/9.
 */
public class MessagePullBean {

//    private int moreCount;//还有多少条
    private List<MessageBean> messageList = new ArrayList<>();// 拉取消息列表

    public MessagePullBean() {

    }

    public MessagePullBean(List<Message> msgList) {
        if (CollectionUtils.isEmpty(msgList)) {
            return;
        }
        for (Message message : msgList) {
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
    }

    public List<MessageBean> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<MessageBean> messageList) {
        this.messageList = messageList;
    }

//    public int getMoreCount() {
//        return moreCount;
//    }
//
//    public void setMoreCount(int moreCount) {
//        this.moreCount = moreCount;
//    }
}
