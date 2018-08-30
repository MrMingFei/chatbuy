package com.campus.chatbuy.manager;

import com.campus.chatbuy.bean.RegisterBean;
import com.campus.chatbuy.bean.UserInfoBean;
import com.campus.chatbuy.model.UserInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jinku on 2017/5/6.
 */
public interface IUserManager {

    UserInfoBean queryById(long userId) throws Exception;

    UserInfoBean queryMySelf(long userId) throws Exception;

    UserInfoBean queryCurr() throws Exception;

    UserInfo getUserInfo(long userId) throws Exception;

    UserInfo queryByMobile(String mobile);

    UserInfo updateUser(UserInfo userInfo);

    Map<Long, UserInfoBean> queryList(Set<Long> userIdSet) throws Exception;

    Map<Long, UserInfo> queryMapList(Set<Long> userIdSet) throws Exception;

    UserInfo saveUser(RegisterBean registerBean) throws Exception;

    void changeMobile(long userId, String mobile);
}
