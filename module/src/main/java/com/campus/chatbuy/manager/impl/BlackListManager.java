package com.campus.chatbuy.manager.impl;

import com.campus.chatbuy.dao.BlackListDao;
import com.campus.chatbuy.model.BlackList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by jinku on 2017/11/21.
 */
@Service
public class BlackListManager {

    @Autowired
    private BlackListDao blackListDao;

    /**
     * 检查用户A是否把用户B加入黑名单
     *
     * @param userId
     * @param blackUserId
     * @return
     */
    public boolean checkBlack(Long userId, Long blackUserId) {
        BlackList blackList = blackListDao.checkBlack(userId, blackUserId);
        if (blackList != null && blackList.getValidity() == BlackList.Valid) {
            return true;
        }
        return false;
    }

    /***
     * 批量查询黑名单
     *
     * @param userId
     * @param blackUserIdList
     * @return
     */
    public Map<Long, Boolean> checkBlackList(Long userId, Set<Long> blackUserIdList) {
        Map<Long, Boolean> resultMap = new HashMap<>();
        List<BlackList> blackListList = blackListDao.checkBlackList(userId, blackUserIdList);
        for (BlackList blackList : blackListList) {
            if (blackListList != null && blackList.getValidity() == BlackList.Valid) {
                resultMap.put(blackList.getBlackUserId(), true);
            }
        }
        return resultMap;
    }

    /**
     * 用户A是把用户B加入黑名单
     *
     * @param userId
     * @param blackUserId
     */
    public void addBlack(Long userId, Long blackUserId) {
        BlackList blackList = blackListDao.checkBlack(userId, blackUserId);
        if (blackList != null && blackList.getValidity() == BlackList.Valid) {
            return;
        }
        if (blackList == null) {
            BlackList blackListNew = new BlackList();
            blackListNew.setCreateTime(new Date());
            blackListNew.setUpdateTime(new Date());
            blackListNew.setUserId(userId);
            blackListNew.setBlackUserId(blackUserId);
            blackListNew.setValidity(BlackList.Valid);

            blackListDao.insert(blackListNew);
        } else {
            blackListDao.addBlack(userId, blackUserId, new Date());
        }
    }

    /**
     * 用户A是把用户B移除黑名单
     *
     * @param userId
     * @param blackUserId
     */
    public void deleteBlack(Long userId, Long blackUserId) {
        BlackList blackList = blackListDao.checkBlack(userId, blackUserId);
        if (blackList == null || blackList.getValidity() != BlackList.Valid) {
            return;
        }
        blackListDao.deleteBlack(userId, blackUserId, new Date());
    }
}
