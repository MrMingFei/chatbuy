package com.campus.chatbuy.manager.impl;

import com.campus.chatbuy.bean.GoodsSessionBean;
import com.campus.chatbuy.bean.MessageBean;
import com.campus.chatbuy.bean.PicInfoBean;
import com.campus.chatbuy.bean.UserInfoBean;
import com.campus.chatbuy.constans.RedisKey;
import com.campus.chatbuy.id.generator.IdGeneratorFactory;
import com.campus.chatbuy.manager.*;
import com.campus.chatbuy.model.GoodsInfo;
import com.campus.chatbuy.model.UserSession;
import com.campus.chatbuy.service.RedisService;
import com.campus.chatbuy.util.DateUtils;
import com.campus.chatbuy.util.StringUtil;
import com.campus.chatbuy.util.ThreadLocalUtil;
import org.apache.commons.collections.CollectionUtils;
import org.guzz.dao.GuzzBaseDao;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by jinku on 2017/8/10.
 */
@Service
public class GoodsSessionManagerImpl implements IGoodsSessionManager {

    @Autowired
    private GuzzBaseDao guzzBaseDao;

    @Autowired
    private IUserManager userManager;

    @Autowired
    private IGoodsPicManager goodsPicManager;

    @Autowired
    private RedisService redisService;

    @Autowired
    private IMessageManager messageManager;

    @Autowired
    private IGoodsInfoManager goodsInfoManager;

    @Autowired
    private BlackListManager blackListManager;

    @Override
    public List<GoodsSessionBean> goodsSessionList(Integer userType) throws Exception {
        List<UserSession> sessionList = sessionList(userType);
        return convert(sessionList);
    }

    @Override
    public List<UserSession> sessionList() {
        return sessionList(null);
    }

    @Override
    public List<UserSession> sessionList(Integer userType) {
        long userId = ThreadLocalUtil.getUserId();
        // 只取前20条会话数据
        SearchExpression se = SearchExpression.forClass(UserSession.class, 1, 20);
        Date overdueDate = DateUtils.Add(new Date(), -30, 0, 0, 0);
        se.and(Terms.biggerOrEq("updateTime", overdueDate));
        se.and(Terms.eq("validity", 1));
        se.and(Terms.eq("userId", userId));
        if (userType != null) {
            se.and(Terms.eq("userType", userType));
        }
        se.setOrderBy("updateTime desc");

        List<UserSession> sessionList = guzzBaseDao.list(se);
        return sessionList;
    }

    @Override
    public GoodsSessionBean goodsSession(Long userId, Long otherUserId, Long goodsId)
            throws Exception {
        SearchExpression se = SearchExpression.forClass(UserSession.class);
        se.and(Terms.eq("userId", userId));
        se.and(Terms.eq("otherUserId", otherUserId));
        se.and(Terms.eq("goodsId", goodsId));

        GoodsInfo goodsInfo = goodsInfoManager.queryById(goodsId);
        Long sellerId = goodsInfo.getUserId();

        UserSession goodsSession = (UserSession) guzzBaseDao.findObject(se);
        if (goodsSession == null) {
            long sessionId = IdGeneratorFactory.generateId();
            Date createDate = new Date();
            // 创建会话
            UserSession goodsSession1 = new UserSession();
            goodsSession1.setSessionId(sessionId);
            goodsSession1.setUserId(userId);
            goodsSession1.setOtherUserId(otherUserId);
            goodsSession1.setGoodsId(goodsId);
            goodsSession1.setValidity(1);
            if (sellerId.equals(userId)) {
                goodsSession1.setUserType(UserSession.User_Type_Sell);
            } else {
                goodsSession1.setUserType(UserSession.User_Type_Buy);
            }
            goodsSession1.setCreateTime(createDate);
            goodsSession1.setUpdateTime(createDate);
            guzzBaseDao.insert(goodsSession1);
            goodsSession = goodsSession1;

            UserSession goodsSession2 = new UserSession();
            goodsSession2.setSessionId(sessionId);
            goodsSession2.setUserId(otherUserId);
            goodsSession2.setOtherUserId(userId);
            goodsSession2.setGoodsId(goodsId);
            goodsSession2.setValidity(1);
            if (sellerId.equals(otherUserId)) {
                goodsSession2.setUserType(UserSession.User_Type_Sell);
            } else {
                goodsSession2.setUserType(UserSession.User_Type_Buy);
            }
            goodsSession2.setCreateTime(createDate);
            goodsSession2.setUpdateTime(createDate);
            guzzBaseDao.insert(goodsSession2);

        } else if (goodsSession.getValidity() == 0) {
            goodsSession.setValidity(1);// 更新成有效
            guzzBaseDao.update(goodsSession);
        }

        List<UserSession> sessionList = new ArrayList<>();
        sessionList.add(goodsSession);

        return convert(sessionList).get(0);
    }

    @Override
    public void updateSessionAccessTime(Long sessionId) {
        SearchExpression se = SearchExpression.forClass(UserSession.class);
        se.and(Terms.eq("sessionId", sessionId));
        List<UserSession> sessionList = guzzBaseDao.list(se);
        for(UserSession userSession : sessionList) {
            userSession.setUpdateTime(new Date());
            guzzBaseDao.update(userSession);
        }
    }

    /**
     * 删除用户的指定会话
     *
     * @param otherUserId
     * @param goodsId
     */
    @Override
    public void delete(Long otherUserId, Long goodsId) {
        long userId = ThreadLocalUtil.getUserId();
        SearchExpression se = SearchExpression.forClass(UserSession.class);
        se.and(Terms.eq("userId", userId));
        se.and(Terms.eq("otherUserId", otherUserId));
        se.and(Terms.eq("goodsId", goodsId));
        UserSession userSession = (UserSession) guzzBaseDao.findObject(se);
        userSession.setValidity(0);
        guzzBaseDao.update(userSession);
    }

    private List<GoodsSessionBean> convert(List<UserSession> sessionList) throws Exception {
        List<GoodsSessionBean> sessionBeanList = new ArrayList<>();
        if (CollectionUtils.isEmpty(sessionList)) {
            return sessionBeanList;
        }
        Set<Long> userIdSet = new HashSet<>();
        Set<Long> goodsIdSet = new HashSet<>();
        Set<Long> otherUserIdSet = new HashSet<>();
        for (UserSession goodsSession : sessionList) {
            userIdSet.add(goodsSession.getUserId());
            userIdSet.add(goodsSession.getOtherUserId());
            otherUserIdSet.add(goodsSession.getOtherUserId());
            goodsIdSet.add(goodsSession.getGoodsId());
        }

        long currUserId = ThreadLocalUtil.getUserId();
        Map<Long, Boolean> blackMap = blackListManager.checkBlackList(currUserId, otherUserIdSet);

        Map<Long, UserInfoBean> usersMap = userManager.queryList(userIdSet);
        Map<Long, List<PicInfoBean>> picInfoMap = goodsPicManager.queryByGoodsId(goodsIdSet);
        Map<Long, GoodsInfo> goodsInfoMap = goodsInfoManager.queryMapList(goodsIdSet);


        for (UserSession goodsSession : sessionList) {
            GoodsSessionBean sessionBean = new GoodsSessionBean();
            Boolean isInBlack = blackMap.get(goodsSession.getOtherUserId());
            if (isInBlack != null && isInBlack) {
                sessionBean.isInBlack = 1;
            } else {
                sessionBean.isInBlack = 0;
            }
            GoodsInfo goodsInfo = goodsInfoMap.get(goodsSession.getGoodsId());
            sessionBean.goodsId = String.valueOf(goodsSession.getGoodsId());
            sessionBean.goodsName = goodsInfo.getGoodsName();
            sessionBean.goodsStatus = goodsInfo.getStatus();
            sessionBean.goodsPrice = StringUtil.convertCent2Yuan(goodsInfo.getPrice());
            sessionBean.goodsOriginPrice = StringUtil.convertCent2Yuan(goodsInfo.getOriginPrice());
            sessionBean.sessionId = String.valueOf(goodsSession.getSessionId());
            List<PicInfoBean> picInfoList = picInfoMap.get(goodsSession.getGoodsId());
            if (picInfoList != null && picInfoList.size() > 0) {
                sessionBean.goodsPic = picInfoList.get(0).getSmallUrl();
            }
            sessionBean.createTime = String.valueOf(goodsSession.getCreateTime().getTime());
            if (goodsSession.getUpdateTime() != null) {
                sessionBean.updateTime = String.valueOf(goodsSession.getUpdateTime().getTime());
            }
            sessionBean.userType = goodsSession.getUserType();
            UserInfoBean userInfoBean =  usersMap.get(goodsSession.getUserId());
            UserInfoBean otherUserInfoBean =  usersMap.get(goodsSession.getOtherUserId());

            if (sessionBean.userType == UserSession.User_Type_Buy) {

                sessionBean.buyUserId = String.valueOf(goodsSession.getUserId());
                sessionBean.buyUserName = userInfoBean.getUserName();
                if (userInfoBean.getAvatarPicInfo() != null) {
                    sessionBean.buyUserAvatar = userInfoBean.getAvatarPicInfo().getSmallUrl();
                }
                sessionBean.sellUserId = String.valueOf(goodsSession.getOtherUserId());
                sessionBean.sellUserName = otherUserInfoBean.getUserName();
                if (otherUserInfoBean.getAvatarPicInfo() != null) {
                    sessionBean.sellUserAvatar = otherUserInfoBean.getAvatarPicInfo().getSmallUrl();
                }
            } else {
                sessionBean.buyUserId = String.valueOf(goodsSession.getOtherUserId());
                sessionBean.buyUserName = otherUserInfoBean.getUserName();
                if (otherUserInfoBean.getAvatarPicInfo() != null) {
                    sessionBean.buyUserAvatar = otherUserInfoBean.getAvatarPicInfo().getSmallUrl();
                }

                sessionBean.sellUserId = String.valueOf(goodsSession.getUserId());
                sessionBean.sellUserName = userInfoBean.getUserName();
                if (userInfoBean.getAvatarPicInfo() != null) {
                    sessionBean.sellUserAvatar = userInfoBean.getAvatarPicInfo().getSmallUrl();
                }
            }

            String unreadKey = RedisKey.SessionUnreadNum + goodsSession.getSessionId() + goodsSession.getUserId();
            String unreadNumStr = redisService.get(unreadKey);
            int unreadNum = 0;
            if (StringUtil.isNotEmpty(unreadNumStr)) {
                unreadNum = Integer.parseInt(unreadNumStr);
            }
            sessionBean.unreadNum = unreadNum;
            MessageBean messageBean = messageManager.getLatestMsg(goodsSession.getSessionId());
            sessionBean.latestMessage = messageBean;
            sessionBeanList.add(sessionBean);
        }

        return sessionBeanList;
    }
}
