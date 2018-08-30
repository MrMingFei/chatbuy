package com.campus.chatbuy.manager.impl;

import com.campus.chatbuy.manager.IUserSecurityManager;
import com.campus.chatbuy.model.UserSecurity;
import org.guzz.dao.GuzzBaseDao;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: baoxuebin
 * Date: 2017/7/11
 */
@Service
public class UserSecurityManagerImpl implements IUserSecurityManager {

    @Autowired
    private GuzzBaseDao guzzBaseDao;

    @Override
    public UserSecurity queryByPhone(String phone) {
        SearchExpression se = SearchExpression.forClass(UserSecurity.class);
        se.and(Terms.eq("mobile", phone));
        UserSecurity user = (UserSecurity) guzzBaseDao.findObject(se);
        return user;
    }

    @Override
    public long saveUserSecurity(UserSecurity userSecurity) {
        guzzBaseDao.insert(userSecurity);
        return userSecurity.getUserId();
    }

    @Override
    public UserSecurity queryByUserId(long userId) {
        SearchExpression se = SearchExpression.forClass(UserSecurity.class);
        se.and(Terms.eq("userId", userId));
        UserSecurity user = (UserSecurity) guzzBaseDao.findObject(se);

        return user;
    }

    @Override
    public void updateUserSecurity(UserSecurity userSecurity) {
        guzzBaseDao.update(userSecurity);
    }
}
