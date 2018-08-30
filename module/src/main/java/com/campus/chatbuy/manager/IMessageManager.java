package com.campus.chatbuy.manager;

import com.campus.chatbuy.bean.MessageBean;
import com.campus.chatbuy.bean.MessagePullBean;
import com.campus.chatbuy.bean.SendMessageBean;
import com.campus.chatbuy.model.Message;

import java.util.List;
import java.util.Map;

/**
 * Created by jinku on 2017/8/30.
 */
public interface IMessageManager {

    Message sendMessage(SendMessageBean msgInfo);

    List<MessageBean> queryByIdList(List<Long> msgIdList);

    MessageBean queryById(Long msgId);

    MessagePullBean pullNewMsg(Long sessionId, Long msgId);

    MessagePullBean pullHistoryMsg(Long sessionId, Long msgId);

    int getUnreadNum() throws Exception;

    MessageBean getLatestMsg(Long sessionId);
}
