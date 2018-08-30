package com.campus.chatbuy.manager;

import com.campus.chatbuy.model.UserSecurity;

/**
 * Author: baoxuebin
 * Date: 2017/7/11
 */
public interface IUserSecurityManager {

    UserSecurity queryByPhone(String phone);

    long saveUserSecurity(UserSecurity userSecurity);

    UserSecurity queryByUserId(long userId);

    void updateUserSecurity(UserSecurity userSecurity);
}
