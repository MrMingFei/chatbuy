package com.campus.chatbuy.manager.impl;

import com.campus.chatbuy.bean.ComplainBean;
import com.campus.chatbuy.manager.IComplainManager;
import com.campus.chatbuy.manager.IGoodsInfoManager;
import com.campus.chatbuy.model.GoodsComplain;
import com.campus.chatbuy.model.GoodsInfo;
import org.guzz.dao.GuzzBaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Author: baoxuebin
 * Date: 2017/8/1
 */
@Service
public class ComplainManager implements IComplainManager {

    @Autowired
    private GuzzBaseDao guzzBaseDao;

    @Autowired
    private IGoodsInfoManager goodsInfoManager;

    @Override
    public long saveComplain(ComplainBean complainBean) {
        GoodsComplain goodsComplain = new GoodsComplain();
        goodsComplain.setInitiatorId(complainBean.getInitiatorId());
        goodsComplain.setTargetId(complainBean.getTargetId());
        goodsComplain.setGoodsId(complainBean.getGoodsId());
        goodsComplain.setCreateTime(new Date());

        GoodsInfo goodsInfo = goodsInfoManager.queryById(complainBean.getGoodsId());
        if (goodsInfo.getUserId().equals(complainBean.getInitiatorId())) {
            goodsComplain.setType(GoodsComplain.TYPE_SELLER);
        } else {
            goodsComplain.setType(GoodsComplain.TYPE_BUYER);
        }
        goodsComplain.setReasonType(complainBean.getReasonType());
        goodsComplain.setReasonDesc(complainBean.getReasonDesc());
        goodsComplain.setStatus(GoodsComplain.UNHANDLED);

        return (long) guzzBaseDao.insert(goodsComplain);
    }
}
