package com.campus.chatbuy.manager.impl;

import com.campus.chatbuy.bean.GoodsSessionBean;
import com.campus.chatbuy.bean.MessageBean;
import com.campus.chatbuy.bean.MessagePullBean;
import com.campus.chatbuy.bean.SendMessageBean;
import com.campus.chatbuy.constans.RedisKey;
import com.campus.chatbuy.id.generator.IdGeneratorFactory;
import com.campus.chatbuy.manager.IGoodsSessionManager;
import com.campus.chatbuy.manager.IMessageManager;
import com.campus.chatbuy.model.Message;
import com.campus.chatbuy.model.UserSession;
import com.campus.chatbuy.service.RedisService;
import com.campus.chatbuy.util.StringUtil;
import com.campus.chatbuy.util.ThreadLocalUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.guzz.dao.GuzzBaseDao;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.guzz.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by jinku on 2017/8/30.
 */
@Service
public class MessageManagerImpl implements IMessageManager {

    private static final Logger log = Logger.getLogger(MessageManagerImpl.class);

    /**
     * 单个会话的最大消息数
     */
    private final int Max_Count = 1000;

    private final int Page_Size = 20;

    @Autowired
    private GuzzBaseDao guzzBaseDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private IGoodsSessionManager goodsSessionManager;

    /**
     * 发送消息:存储到数据库,并添加到redis队列中
     *
     * @param msgInfo
     */
    @Override
    public Message sendMessage(SendMessageBean msgInfo) {
        long userId = ThreadLocalUtil.getUserId();
        Message message = new Message();
        message.setMessageId(IdGeneratorFactory.generateId());
        message.setSessionId(msgInfo.getSessionId());
        message.setMsgType(msgInfo.getMsgType());
        message.setBusinessType(msgInfo.getBusinessType());
        message.setMsgContent(msgInfo.getMsgContent());
        message.setFromUserId(userId);
        message.setToUserId(msgInfo.getToUserId());
        message.setCreateTime(new Date());

        String session2MessageKey = RedisKey.Session2MessageList + message.getSessionId();
        // 判断消息队列的长度,超过1000条,则移除队尾元素
        // 保存redis sessionId --> message list （最近1000条）
        redisService.lpush(session2MessageKey, String.valueOf(message.getMessageId()));

        long length = redisService.llen(session2MessageKey);
        if (length > Max_Count) {// 超过1000条,移除队尾元素
            for (int i = 0; i < 10; i++) {// 预留10条
                redisService.rpop(session2MessageKey);
            }
        }
        guzzBaseDao.insert(message);

        // 更新会话时间
        goodsSessionManager.updateSessionAccessTime(message.getSessionId());

        // 更新对方会话未读消息+1
        String unreadKey = RedisKey.SessionUnreadNum + message.getSessionId() + msgInfo.getToUserId();
        String unreadNum = redisService.get(unreadKey);
        if (StringUtil.isEmpty(unreadNum)) {
            redisService.set(unreadKey, "1");
        } else {
            redisService.set(unreadKey, String.valueOf(Integer.parseInt(unreadNum) + 1));
        }

        return message;
    }

    @Override
    public List<MessageBean> queryByIdList(List<Long> msgIdList) {
        SearchExpression se = SearchExpression.forLoadAll(Message.class);
        se.and(Terms.in("messageId", msgIdList));
        se.setOrderBy("createTime desc");
        List<Message> messageList = guzzBaseDao.list(se);
        return MessageBean.convertMessage(messageList);
    }

    @Override
    public MessageBean queryById(Long msgId) {
        SearchExpression se = SearchExpression.forClass(Message.class);
        se.and(Terms.eq("messageId", msgId));
        Message message = (Message) guzzBaseDao.findObject(se);
        if (message == null) {
            return null;
        }
        List<Message> messageList = new ArrayList<>();
        messageList.add(message);
        return MessageBean.convertMessage(messageList).get(0);
    }

    /**
     * 拉取最新的消息,最多20条(msgId之后消息)
     *
     * @param sessionId
     * @param msgId
     * @return
     */
    @Override
    public MessagePullBean pullNewMsg(Long sessionId, Long msgId) {
        long userId = ThreadLocalUtil.getUserId();
        MessagePullBean messagePullBean =  pullMessage(sessionId, msgId, true);

        // 重置会话未读消息数
        String unreadKey = RedisKey.SessionUnreadNum + sessionId + userId;
        redisService.set(unreadKey, "0");

        return messagePullBean;
    }

    /**
     * 拉取历史消息(msgId之后的消息,最多20条)
     *
     * @param sessionId
     * @param msgId
     * @return
     */
    @Override
    public MessagePullBean pullHistoryMsg(Long sessionId, Long msgId) {

        return pullMessage(sessionId, msgId, false);
    }

    @Override
    public int getUnreadNum() throws Exception {
        long userId = ThreadLocalUtil.getUserId();
        // 查询最近的会话列表
        List<UserSession> sessionList = goodsSessionManager.sessionList();
        if (CollectionUtils.isEmpty(sessionList)) {
            return 0;
        }
        int unreadTotal = 0;
        for(UserSession userSession : sessionList) {
            long sessionId = userSession.getSessionId();
            String unreadStr = redisService.get(RedisKey.SessionUnreadNum + sessionId + userId);
            if (StringUtil.isEmpty(unreadStr)) {
                continue;
            }
            int unreadNum = Integer.parseInt(unreadStr);
            unreadTotal += unreadNum;
        }
        return unreadTotal;
    }

    @Override
    public MessageBean getLatestMsg(Long sessionId) {
        String session2MessageKey = RedisKey.Session2MessageList + sessionId;
        String msgId = redisService.lindex(session2MessageKey, 0);
        if (StringUtil.isEmpty(msgId)) {
            return null;
        }
        MessageBean messageBean = queryById(Long.parseLong(msgId));
        return messageBean;
    }

    /**
     * 拉取消息(向前/向后)
     *
     * @param sessionId
     * @param msgId
     * @param isPullNew
     * @return
     */
    private MessagePullBean pullMessage(Long sessionId, Long msgId, boolean isPullNew) {
        String session2MessageKey = RedisKey.Session2MessageList + sessionId;
        String firstId = redisService.lindex(session2MessageKey, 0);
        MessagePullBean messagePullBean = new MessagePullBean();
        if (StringUtil.isEmpty(firstId)) {//队列里没有消息
            return messagePullBean;
        }

        // 异常情况,不可能出现
        Assert.assertTrue(Long.parseLong(firstId) >= msgId.longValue(), "拉取新消息失败");

        int length = redisService.llen(session2MessageKey).intValue();
        if (length <= 0) {
            return messagePullBean;
        }

        List<String> idList = redisService.lrange(session2MessageKey, 0, length - 1);
        int msgIndex = -1;
        for (int i = 0; i < idList.size(); i++) {
            String id = idList.get(i);
            if (String.valueOf(msgId).equals(id)) {
                msgIndex = i;
                break;
            }
        }

        if (isPullNew && msgIndex == 0) {// 没有新消息
            return messagePullBean;
        }

        if (!isPullNew && (msgIndex == length - 1 || msgIndex == -1)) {// 没有历史消息
            return messagePullBean;
        }

        int startIndex = -1;
        int stopIndex = -1;
        int moreCount = 0;
        List<String> idTempList = null;
        if (msgIndex == -1) {// 不在队列中,拉新消息
            startIndex = 0;
            stopIndex = length > Page_Size ? Page_Size - 1 : length - 1;
            // 还有多少条
            moreCount = length - Page_Size;
        } else {// 在队列中
            if (isPullNew) {
                // 总是拉最新的
                startIndex = 0;
                stopIndex = msgIndex > Page_Size ? Page_Size - 1 : msgIndex - 1;
                // 还有多少条
                moreCount = msgIndex - Page_Size;
            } else {
                startIndex = msgIndex + 1;
                stopIndex = msgIndex + Page_Size >= length ? length - 1 : msgIndex + Page_Size;
            }
        }

        // 拉取队列20条
        idTempList = redisService.lrange(session2MessageKey, startIndex, stopIndex);

        if (CollectionUtils.isEmpty(idTempList)) {
            return messagePullBean;
        }

        List<Long> msgIdList = new ArrayList<>();
        for (String idStr : idTempList) {
            if (StringUtil.isEmpty(idStr)) {
                continue;
            }
            msgIdList.add(Long.parseLong(idStr));
        }

        // 从数据库查询消息内容
        List<MessageBean> messageList = queryByIdList(msgIdList);
        moreCount =  moreCount < 0 ? 0 : moreCount;
        messagePullBean.setMessageList(messageList);
//        messagePullBean.setMoreCount(moreCount);

        return messagePullBean;
    }
}
