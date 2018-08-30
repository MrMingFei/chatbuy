package com.campus.chatbuy.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.campus.chatbuy.api.PushApi;
import com.campus.chatbuy.bean.MessagePullBean;
import com.campus.chatbuy.bean.PicRequestInfo;
import com.campus.chatbuy.bean.ResultBean;
import com.campus.chatbuy.bean.SendMessageBean;
import com.campus.chatbuy.constans.RedisKey;
import com.campus.chatbuy.enums.MsgShowType;
import com.campus.chatbuy.id.generator.IdGeneratorFactory;
import com.campus.chatbuy.manager.IMessageManager;
import com.campus.chatbuy.manager.impl.BlackListManager;
import com.campus.chatbuy.model.Message;
import com.campus.chatbuy.util.Base64Util;
import com.campus.chatbuy.util.LockUtil;
import com.campus.chatbuy.util.StringUtil;
import com.campus.chatbuy.util.ThreadLocalUtil;
import org.apache.log4j.Logger;
import org.guzz.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jinku on 2017/8/30.
 */
@Controller
@RequestMapping(value = "/chatbuy/web/message")
public class MessageAction {

    private static final Logger log = Logger.getLogger(MessageAction.class);

    private final static int Timeout_Seconds = 5;

    @Autowired
    private IMessageManager messageManager;

    @Autowired
    private BlackListManager blackListManager;

    @RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean sendMessage(SendMessageBean msgInfo, Long lastMsgId) throws Exception {
        log.info("sendMessage msgInfo [" + msgInfo + "]");

        Assert.assertNotNull(msgInfo.getBusinessType(), "请求参数有误");
        Assert.assertNotNull(msgInfo.getSessionId(), "请求参数有误");
        Assert.assertNotEmpty(msgInfo.getMsgContent(), "请求参数有误");
        Assert.assertNotNull(msgInfo.getToUserId(), "请求参数有误");
        Assert.assertNotNull(msgInfo.getMsgType(), "请求参数有误");
        Assert.assertNotNull(lastMsgId, "请求参数有误");

        log.info("sendMessage msgContent [" + msgInfo.getMsgContent() + "]");

        // 判断是否被对方加入黑名单
        boolean result = blackListManager.checkBlack(msgInfo.getToUserId(), ThreadLocalUtil.getUserId());
        if (result) {
            // 将消息改成仅发送者可见
            byte[] msgByteArray = Base64Util.decode(msgInfo.getMsgContent());
            String msgStr = new String(msgByteArray, "utf8");
            JSONObject jsonObject = JSON.parseObject(msgStr);
            jsonObject.put("showType", MsgShowType.Only_Sender.key);
            String changedJson = jsonObject.toJSONString();
            String changedMsg = Base64Util.encode(changedJson.getBytes("utf8"));
            msgInfo.setMsgContent(changedMsg);
        }

        String sessionLockKey = RedisKey.SessionLockKey + msgInfo.getSessionId();
        String lockValue = LockUtil.tryLock(sessionLockKey, Timeout_Seconds);
        Assert.assertNotEmpty(lockValue, "服务器繁忙, 请稍后重试");
        MessagePullBean messagePullBean = null;
        Message message = null;
        try {
            // 发消息
            message = messageManager.sendMessage(msgInfo);
            // 拉取消息
            messagePullBean = messageManager.pullNewMsg(msgInfo.getSessionId(), lastMsgId);
        } finally {
            LockUtil.releaseLock(sessionLockKey, lockValue);
        }

        // 如果comet请求,则push给对方
        if (msgInfo.getIsComet() != null && msgInfo.getIsComet() == 1) {
            List<Message> messageList = new ArrayList<>();
            messageList.add(message);
            MessagePullBean oneMessageBean = new MessagePullBean(messageList);
            ResultBean resultBean = ResultBean.success(oneMessageBean);
            boolean isSuccess = PushApi.pushMessage(String.valueOf(msgInfo.getToUserId()),
                    String.valueOf(message.getMessageId()), JSON.toJSONString(resultBean));
            if (isSuccess) {
                return ResultBean.success(resultBean);
            } else {
                return ResultBean.failure("comet push failed");
            }
        }

        return ResultBean.success(messagePullBean);
    }

    @RequestMapping(value = "/pullNewMsg", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean pullNewMsg(Long sessionId, Long msgId) throws Exception {
        Assert.assertNotNull(sessionId, "请求参数有误");
        Assert.assertNotNull(msgId, "请求参数有误");

        log.info("pullNewMsg sessionId [" + sessionId + "] msgId [" + msgId + "]");

        // 对会话加redis锁
        String sessionLockKey = RedisKey.SessionLockKey + sessionId;
        String lockValue = LockUtil.tryLock(sessionLockKey, Timeout_Seconds);
        Assert.assertNotEmpty(lockValue, "服务器繁忙, 请稍后重试");
        MessagePullBean messagePullBean = null;
        try {
            messagePullBean = messageManager.pullNewMsg(sessionId, msgId);
        } finally {
            LockUtil.releaseLock(sessionLockKey, lockValue);
        }

        return ResultBean.success(messagePullBean);
    }

    @RequestMapping(value = "/pullHistoryMsg", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean pullHistoryMsg(Long sessionId, Long msgId) throws Exception {
        Assert.assertNotNull(sessionId, "请求参数有误");
        Assert.assertNotNull(msgId, "请求参数有误");

        log.info("pullHistoryMsg sessionId [" + sessionId + "] msgId [" + msgId + "]");

        // 对会话加redis锁
        String sessionLockKey = RedisKey.SessionLockKey + sessionId;
        String lockValue = LockUtil.tryLock(sessionLockKey, Timeout_Seconds);
        Assert.assertNotEmpty(lockValue, "服务器繁忙, 请稍后重试");
        MessagePullBean messagePullBean = null;
        try {
            messagePullBean = messageManager.pullHistoryMsg(sessionId, msgId);
        } finally {
            LockUtil.releaseLock(sessionLockKey, lockValue);
        }

        return ResultBean.success(messagePullBean);
    }

    @RequestMapping(value = "/getUnreadNum", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getUnreadNum() throws Exception {
        int unread = messageManager.getUnreadNum();
        return ResultBean.success(unread);
    }
}
