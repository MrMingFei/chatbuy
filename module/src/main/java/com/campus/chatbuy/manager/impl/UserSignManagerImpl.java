package com.campus.chatbuy.manager.impl;

import com.campus.chatbuy.config.QIMConfig;
import com.campus.chatbuy.manager.IUserSignManager;
import com.campus.chatbuy.model.UserSign;
import com.campus.chatbuy.qim.sign.SigCheckUtil;
import com.campus.chatbuy.util.DateUtils;
import org.guzz.dao.GuzzBaseDao;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by jinku on 2017/7/13.
 */
@Service
public class UserSignManagerImpl implements IUserSignManager {

    private final static int Overdue_Days = 5 * 30;//实际为180天

    @Autowired
    private GuzzBaseDao guzzBaseDao;

    @Override
    public UserSign query(String userId) throws Exception {
        SearchExpression se = SearchExpression.forClass(UserSign.class);
        se.and(Terms.eq("userId", userId));
        UserSign userSign = (UserSign) guzzBaseDao.findObject(se);

        if (userSign != null) {
            Date overdueDate = DateUtils.Add(userSign.getSignTime(), Overdue_Days, 0, 0, 0);
            if (overdueDate.getTime() > System.currentTimeMillis()) {
                return userSign;
            }
        }
        if (userSign == null) {
            userSign = create(userId);
        } else {
            String sign = SigCheckUtil.sign(QIMConfig.Sdk_App_Id, String.valueOf(userId));
            userSign.setUserSign(sign);
            userSign.setSignTime(new Date());
            userSign.setUpdateTime(new Date());
            guzzBaseDao.update(userSign);
        }
        return userSign;
    }

    @Override
    public UserSign create(String userId) throws Exception {
        String sign = SigCheckUtil.sign(QIMConfig.Sdk_App_Id, String.valueOf(userId));
        UserSign userSign = new UserSign();
        userSign.setUserId(userId);
        userSign.setUserSign(sign);
        userSign.setSignTime(new Date());
        userSign.setCreateTime(new Date());
        guzzBaseDao.insert(userSign);

        return userSign;
    }
}
