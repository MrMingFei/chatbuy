package com.campus.chatbuy.manager.impl;

import com.campus.chatbuy.bean.PicInfoBean;
import com.campus.chatbuy.bean.PicRequestInfo;
import com.campus.chatbuy.manager.IPicInfoManager;
import com.campus.chatbuy.model.PicInfo;
import org.apache.commons.beanutils.BeanUtils;
import org.guzz.dao.GuzzBaseDao;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by jinku on 2017/7/19.
 */
@Service
public class PicInfoManagerImpl implements IPicInfoManager {

    @Autowired
    private GuzzBaseDao guzzBaseDao;

    @Override
    public Map<Long, PicInfoBean> queryPicInfo(Set<Long> picIdSet) throws Exception {
        SearchExpression se = SearchExpression.forLoadAll(PicInfo.class);
        se.and(Terms.in("picId", picIdSet));
        List<PicInfo> picInfoList = guzzBaseDao.list(se);
        Map<Long, PicInfoBean> picInfoMap = new HashMap<>();
        for (PicInfo picInfo : picInfoList) {
            PicInfoBean picInfoBean = new PicInfoBean();
            BeanUtils.copyProperties(picInfoBean, picInfo);
            picInfoMap.put(picInfo.getPicId(), picInfoBean);
        }
        return picInfoMap;
    }

    @Override
    public void savePicInfo(PicInfoBean picInfoBean) {
        PicInfo picInfo = new PicInfo();
        picInfo.setPicId(Long.parseLong(picInfoBean.getPicId()));
        picInfo.setOriginUrl(picInfoBean.getOriginUrl());
        picInfo.setBigUrl(picInfoBean.getBigUrl());
        picInfo.setSmallUrl(picInfoBean.getSmallUrl());
        picInfo.setWidth(picInfoBean.getWidth());
        picInfo.setHeight(picInfoBean.getHeight());
        picInfo.setCreateTime(new Date());
        guzzBaseDao.insert(picInfo);
    }

    @Override
    public PicInfoBean queryPicInfo(long picId) throws Exception {
        SearchExpression se = SearchExpression.forClass(PicInfo.class);
        se.and(Terms.eq("picId", picId));
        PicInfo picInfo =  (PicInfo) guzzBaseDao.findObject(se);
        PicInfoBean picInfoBean = new PicInfoBean();
        BeanUtils.copyProperties(picInfoBean, picInfo);
        return picInfoBean;
    }
}
