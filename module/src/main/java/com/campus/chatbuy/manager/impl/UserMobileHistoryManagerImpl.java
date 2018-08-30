package com.campus.chatbuy.manager.impl;

import com.campus.chatbuy.manager.IUserMobileHistoryManager;
import com.campus.chatbuy.model.UserMobileHistory;
import com.campus.chatbuy.util.StringUtil;
import org.guzz.dao.GuzzBaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Author: baoxuebin
 * Date: 2017/7/31
 */
@Service
public class UserMobileHistoryManagerImpl implements IUserMobileHistoryManager {

    @Autowired
    private GuzzBaseDao guzzBaseDao;

    @Override
    public void saveUserMobileHistory(long userId, String oldPhone) {
        UserMobileHistory userMobileHistory = new UserMobileHistory();
        userMobileHistory.setUserId(userId);
        userMobileHistory.setMobile(oldPhone);
        userMobileHistory.setCreateTime(new Date());
        guzzBaseDao.insert(userMobileHistory);
    }
}
