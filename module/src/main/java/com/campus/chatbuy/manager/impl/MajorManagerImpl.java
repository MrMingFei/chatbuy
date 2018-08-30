package com.campus.chatbuy.manager.impl;

import com.campus.chatbuy.manager.IMajorManager;
import com.campus.chatbuy.model.DicMajor;
import com.campus.chatbuy.model.DicUniversity;
import com.campus.chatbuy.model.GoodsInfo;
import org.guzz.dao.GuzzBaseDao;
import org.guzz.orm.mapping.FirstColumnDataLoader;
import org.guzz.orm.mapping.FormBeanRowDataLoader;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.guzz.orm.sql.BindedCompiledSQL;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.transaction.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jinku on 2017/7/12.
 */
@Service
public class MajorManagerImpl implements IMajorManager {

    @Autowired
    private GuzzBaseDao guzzBaseDao;

    @Override
    public List<DicMajor> queryAllMajor() {
        SearchExpression se = SearchExpression.forLoadAll(DicMajor.class);
        return guzzBaseDao.list(se);
    }

    @Override
    public List<DicMajor> queryMajor(String fLetter) {
        SearchExpression se = SearchExpression.forLoadAll(DicMajor.class);
        se.and(Terms.eq("fLetter", fLetter));
        // 只查询有效的专业
        se.and(Terms.eq("validity", 1));
        return guzzBaseDao.list(se);
    }

    @Override
    public List<DicUniversity> queryAllUniversity() {
        SearchExpression se = SearchExpression.forLoadAll(DicUniversity.class);
        return guzzBaseDao.list(se);
    }

    @Override
    public List<DicMajor> queryMajorByName(String majorName) {
        SearchExpression se = SearchExpression.forLoadAll(DicMajor.class);
        se.and(Terms.like("majorName", "%" + majorName + "%", true));
        // 只查询有效的专业
        se.and(Terms.eq("validity", 1));
        return guzzBaseDao.list(se);
    }
}
