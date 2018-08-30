package com.campus.chatbuy.manager;

/**
 * Author: baoxuebin
 * Date: 2017/7/11
 */
public interface IUserMobileHistoryManager {

    void saveUserMobileHistory(long userId, String oldPhone);
}
