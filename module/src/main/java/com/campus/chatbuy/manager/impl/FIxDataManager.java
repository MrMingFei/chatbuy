package com.campus.chatbuy.manager.impl;

import com.campus.chatbuy.constans.RedisKey;
import com.campus.chatbuy.manager.IGoodsInfoManager;
import com.campus.chatbuy.model.GoodsInfo;
import com.campus.chatbuy.model.UserSession;
import com.campus.chatbuy.service.RedisService;
import com.campus.chatbuy.util.DateUtils;
import org.guzz.dao.GuzzBaseDao;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by jinku on 2018/1/3.
 */
@Service
public class FIxDataManager {

    @Autowired
    private GuzzBaseDao guzzBaseDao;

    @Autowired
    private IGoodsInfoManager goodsInfoManager;

    @Autowired
    private RedisService redisService;

    public void fixUserType() {
        SearchExpression se = SearchExpression.forLoadAll(UserSession.class);
        List<UserSession> sessionList = guzzBaseDao.list(se);

        for (UserSession userSession : sessionList) {
            Long goodsId = userSession.getGoodsId();
            Long userId = userSession.getUserId();
            GoodsInfo goodsInfo = goodsInfoManager.queryById(goodsId);
            if (goodsInfo == null) {
                continue;
            }
            Long sellerId = goodsInfo.getUserId();
            if (userId.equals(sellerId)) {
                userSession.setUserType(UserSession.User_Type_Sell);
            } else {
                userSession.setUserType(UserSession.User_Type_Buy);
            }
            guzzBaseDao.update(userSession);
        }
    }

    /**
     * 删除过期的redis 中消息信息
     */
    public void clearRedisMessage() {
        SearchExpression se = SearchExpression.forLoadAll(UserSession.class);
        Date overdueDate = DateUtils.Add(new Date(), -30, 0, 0, 0);
        se.and(Terms.smallerOrEq("updateTime", overdueDate));
        List<UserSession> sessionList = guzzBaseDao.list(se);

        for (UserSession userSession : sessionList) {
            clearRedisMessage(userSession);
        }
    }

    public void clearRedisMessage(String sessionId) {
        SearchExpression se = SearchExpression.forClass(UserSession.class);
        se.and(Terms.eq("sessionId", sessionId));
        List<UserSession> sessionList = guzzBaseDao.list(se);
        for(UserSession userSession : sessionList) {
            clearRedisMessage(userSession);
        }
    }

    public void clearRedisMessage(UserSession userSession) {
        String session2MessageKey = RedisKey.Session2MessageList + userSession.getSessionId();
        String unReadNumKey1 = RedisKey.SessionUnreadNum + userSession.getSessionId() + userSession.getUserId();
        String unReadNumKey2 = RedisKey.SessionUnreadNum + userSession.getSessionId() + userSession.getOtherUserId();

        redisService.delete(session2MessageKey);
        redisService.delete(unReadNumKey1);
        redisService.delete(unReadNumKey2);
    }
}
