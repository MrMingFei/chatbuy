package com.campus.chatbuy.manager.impl;

import com.campus.chatbuy.bean.PicInfoBean;
import com.campus.chatbuy.bean.RegisterBean;
import com.campus.chatbuy.bean.UserInfoBean;
import com.campus.chatbuy.config.QIMConfig;
import com.campus.chatbuy.id.generator.IdGeneratorFactory;
import com.campus.chatbuy.manager.*;
import com.campus.chatbuy.model.PicInfo;
import com.campus.chatbuy.model.UserInfo;
import com.campus.chatbuy.model.UserSecurity;
import com.campus.chatbuy.model.UserSign;
import com.campus.chatbuy.qim.account.AccountUtil;
import com.campus.chatbuy.util.DateUtils;
import com.campus.chatbuy.util.Md5Util;
import com.campus.chatbuy.util.StringUtil;
import com.campus.chatbuy.util.ThreadLocalUtil;
import com.campus.chatbuy.util.encrypt.BCrypt;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.guzz.dao.GuzzBaseDao;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by jinku on 2017/5/6.
 */
@Service
public class UserManagerImpl implements IUserManager {

    private static final Logger log = Logger.getLogger(UserManagerImpl.class);

    @Autowired
    private GuzzBaseDao guzzBaseDao;

    @Autowired
    private IUserSecurityManager userSecurityManager;

    @Autowired
    private IPicInfoManager picInfoManager;

    @Autowired
    private IUserSignManager userSignManager;

    @Autowired
    private IUserMobileHistoryManager userMobileHistoryManager;

    @Override
    public UserInfoBean queryById(long userId) throws Exception  {
        UserInfoBean userInfoBean = innerQueryById(userId);
        if (userInfoBean != null) {
            userInfoBean.setMobile(null);
        }
        return userInfoBean;
    }

    @Override
    public UserInfoBean queryMySelf(long userId) throws Exception {
        UserInfoBean userInfoBean = innerQueryById(userId);
        return userInfoBean;
    }

    @Override
    public UserInfoBean queryCurr() throws Exception {
        long userId = ThreadLocalUtil.getUserId();
        UserInfoBean userInfoBean = innerQueryById(userId);
        return userInfoBean;
    }

    @Override
    public UserInfo getUserInfo(long userId) throws Exception {
        SearchExpression se = SearchExpression.forClass(UserInfo.class);
        se.and(Terms.eq("userId", userId));
        UserInfo user = (UserInfo) guzzBaseDao.findObject(se);
        return user;
    }

    @Override
    public UserInfo queryByMobile(String mobile) {
        SearchExpression se = SearchExpression.forClass(UserInfo.class);
        se.and(Terms.eq("mobile", mobile));
        UserInfo user = (UserInfo) guzzBaseDao.findObject(se);
        return user;
    }

    @Override public UserInfo updateUser(UserInfo userInfo) {
        guzzBaseDao.update(userInfo);
        return userInfo;
    }

    @Override
    public Map<Long, UserInfoBean> queryList(Set<Long> userIdSet) throws Exception {
        SearchExpression se = SearchExpression.forLoadAll(UserInfo.class);
        se.and(Terms.in("userId", userIdSet));

        List<UserInfo> userInfos = guzzBaseDao.list(se);
        Map<Long, UserInfoBean> userInfoBeanMap = new HashMap<>();
        Set<Long> picIdSet = new HashSet<>();
        for(UserInfo userInfo : userInfos) {
            UserInfoBean userInfoBean = new UserInfoBean();
            BeanUtils.copyProperties(userInfoBean, userInfo);
            userInfoBean.setMobile(null);
            userInfoBeanMap.put(userInfo.getUserId(), userInfoBean);
            picIdSet.add(userInfo.getAvatarPicId());
        }
        Map<Long, PicInfoBean> picInfoMap = picInfoManager.queryPicInfo(picIdSet);
        Set<Long> userIdKeySet =  userInfoBeanMap.keySet();
        for(Long userId : userIdKeySet) {
            UserInfoBean userInfoBean = userInfoBeanMap.get(userId);
            if (StringUtil.isEmpty(userInfoBean.getAvatarPicId())) {
                continue;
            }
            PicInfoBean picInfoBean = picInfoMap.get(Long.parseLong(userInfoBean.getAvatarPicId()));
            userInfoBean.setAvatarPicInfo(picInfoBean);
        }
        return userInfoBeanMap;
    }

    @Override
    public Map<Long, UserInfo> queryMapList(Set<Long> userIdSet) throws Exception {
        SearchExpression se = SearchExpression.forLoadAll(UserInfo.class);
        se.and(Terms.in("userId", userIdSet));

        List<UserInfo> userInfoList = guzzBaseDao.list(se);
        Map<Long, UserInfo> userInfoMap = new HashMap<>();
        for (UserInfo userInfo : userInfoList) {
            userInfoMap.put(userInfo.getUserId(), userInfo);
        }
        return userInfoMap;
    }

    @Override
    public UserInfo saveUser(RegisterBean registerBean) throws Exception {
        long userId = IdGeneratorFactory.generateId();

        // 当前时间
        Date current = new Date();
        UserSecurity userSecurity = new UserSecurity();
        userSecurity.setUserId(userId);
        userSecurity.setMobile(registerBean.getMobile());
        userSecurity.setCreateTime(current);
        String pw_hash = BCrypt.hashpw(registerBean.getPassword(), BCrypt.gensalt());
        userSecurity.setPassword(pw_hash);
        userSecurity.setStatus(UserSecurity.ACCOUNT_STATE_NORMAL);
        userSecurity.setRoleType(UserSecurity.Role_Type_Common);
        userSecurityManager.saveUserSecurity(userSecurity);

        // 保存用户基本信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setUserName(registerBean.getUserName());
        userInfo.setMobile(registerBean.getMobile());
        userInfo.setUniversityId(registerBean.getUniversityId());
        userInfo.setMajorId(registerBean.getMajorId());
        userInfo.setUniversityName(registerBean.getUniversityName());
        userInfo.setMajorName(registerBean.getMajorName());
        userInfo.setEntranceYear(registerBean.getEntranceYear());
        userInfo.setSex(registerBean.getSex());
        userInfo.setCreateTime(current);
        guzzBaseDao.insert(userInfo);

//        // 生成腾讯云通信签名
//        log.info("register generate user sign and import account start");
//        long start = System.currentTimeMillis();
//        userSignManager.create(String.valueOf(userId));
//        // 并导入到腾讯云通信账号系统
//        UserSign userSign = userSignManager.query(QIMConfig.IM_Admin);
//        AccountUtil.accountImport(userSign.getUserSign(), String.valueOf(userId));
//        long end = System.currentTimeMillis();
//        log.info("register generate user sign and import account end; costTime is [" + (end - start) + "]");
        return userInfo;
    }

    /**
     * 更换手机号码
     * @param userId
     * @param mobile
     */
    @Override
    public void changeMobile(long userId, String mobile) {
        UserSecurity userSecurity = userSecurityManager.queryByUserId(userId);
        userSecurity.setMobile(mobile);
        userSecurity.setUpdateTime(new Date());
        guzzBaseDao.update(userSecurity);

        SearchExpression se = SearchExpression.forClass(UserInfo.class);
        se.and(Terms.eq("userId", userId));
        UserInfo userInfo = (UserInfo) guzzBaseDao.findObject(se);
        userInfo.setMobile(mobile);
        guzzBaseDao.update(userInfo);

        // 保存用户的历史电话号码
        userMobileHistoryManager.saveUserMobileHistory(userId, userInfo.getMobile());
    }

    private UserInfoBean innerQueryById(long userId) throws Exception {
        SearchExpression se = SearchExpression.forClass(UserInfo.class);
        se.and(Terms.eq("userId", userId));
        UserInfo user = (UserInfo) guzzBaseDao.findObject(se);
        if (user == null) {
            return null;
        }
        UserInfoBean userInfoBean = new UserInfoBean();
        BeanUtils.copyProperties(userInfoBean, user);
        if (user.getAvatarPicId() != null) {
            PicInfoBean picInfoBean = picInfoManager.queryPicInfo(user.getAvatarPicId());
            userInfoBean.setAvatarPicInfo(picInfoBean) ;
        }
        return userInfoBean;
    }

}
