package com.campus.chatbuy.manager;

import com.campus.chatbuy.model.UserSign;

/**
 * Created by jinku on 2017/7/13.
 */
public interface IUserSignManager {

	UserSign query(String userId) throws Exception;

	UserSign create(String userId) throws Exception;
}
