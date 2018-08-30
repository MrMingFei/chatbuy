package com.campus.chatbuy.manager.impl;

import com.campus.chatbuy.bean.GoodsInfoBean;
import com.campus.chatbuy.bean.PageBean;
import com.campus.chatbuy.bean.TradeInfoBean;
import com.campus.chatbuy.id.generator.IdGeneratorFactory;
import com.campus.chatbuy.manager.IGoodsInfoManager;
import com.campus.chatbuy.model.GoodsInfo;
import com.campus.chatbuy.model.TradeInfo;
import com.campus.chatbuy.model.TradeRecord;
import com.campus.chatbuy.util.StringUtil;
import com.campus.chatbuy.util.ThreadLocalUtil;
import org.guzz.dao.GuzzBaseDao;
import org.guzz.exception.IllegalParameterException;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.guzz.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jinku on 2017/9/8.
 */
@Service
public class TradeInfoManager {

    @Autowired
    private GuzzBaseDao guzzBaseDao;

    @Autowired
    private IGoodsInfoManager goodsInfoManager;

    @Autowired
    private GoodsESManager goodsESManager;

    /**
     * 买家发起交易
     *
     * @param tradeInfoBean
     * @return
     * @throws Exception
     */
    public Long insert(TradeInfoBean tradeInfoBean) throws Exception {
        GoodsInfo goodsInfo = goodsInfoManager.queryById(tradeInfoBean.getGoodsId());
        Assert.assertNotNull(goodsInfo, "该商品不存在");
        Assert.assertTrue(goodsInfo.getStatus() == GoodsInfo.Status_Published, "该商品已经出售或下架");
        long currUserId = ThreadLocalUtil.getUserId();
        Assert.assertTrue(goodsInfo.getUserId().longValue() != currUserId, "不能对自己的商品发起交易");
        // 判断该用户有没有卖过该商品
        TradeInfo tradeInfo = queryByUserAndGoods(currUserId, tradeInfoBean.getGoodsId());
        if (tradeInfo != null && tradeInfo.getTradeState() == TradeInfo.Trade_Status_Confirmed) {
            throw new IllegalParameterException("您已经购买过该商品了,请不要重复交易");
        }
        tradeInfo = new TradeInfo();
        tradeInfo.setBuyerId(currUserId);
        tradeInfo.setSellerId(goodsInfo.getUserId());
        tradeInfo.setTradePrice(StringUtil.convertYuan2Cent(tradeInfoBean.getTradePrice()));
        tradeInfo.setCreateTime(new Date());
        tradeInfo.setGoodsId(tradeInfoBean.getGoodsId());
        tradeInfo.setTradeAddress(tradeInfoBean.getTradeAddress());
        tradeInfo.setTradeState(TradeInfo.Trade_Status_Init);
        tradeInfo.setTradeId(IdGeneratorFactory.generateId());
        tradeInfo.setTradeTime(tradeInfoBean.getTradeTime());
        guzzBaseDao.insert(tradeInfo);
        return tradeInfo.getTradeId();
    }

    public TradeInfo queryByUserAndGoods(Long buyerId, Long goodsId) {
        SearchExpression se = SearchExpression.forLoadAll(TradeInfo.class);
        se.and(Terms.eq("buyerId", buyerId));
        se.and(Terms.eq("goodsId", goodsId));
        se.and(Terms.eq("tradeState", TradeInfo.Trade_Status_Confirmed));
        return (TradeInfo) guzzBaseDao.findObject(se);
    }

    public TradeInfo queryById(Long tradeId) {
        SearchExpression se = SearchExpression.forClass(TradeInfo.class);
        se.and(Terms.eq("tradeId", tradeId));
        return (TradeInfo) guzzBaseDao.findObject(se);
    }

    public List<TradeInfo> queryByIdList(List<Long> tradeIdList) {
        SearchExpression se = SearchExpression.forLoadAll(TradeInfo.class);
        se.and(Terms.in("tradeId", tradeIdList));
        return guzzBaseDao.list(se);
    }

    public TradeInfo queryByGoodsId(Long goodsId) {
        SearchExpression se = SearchExpression.forClass(TradeInfo.class);
        se.and(Terms.eq("goodsId", goodsId));
        se.and(Terms.eq("tradeState", TradeInfo.Trade_Status_Confirmed));
        return (TradeInfo) guzzBaseDao.findObject(se);
    }

    public void confirmTrade(TradeInfo tradeInfo) throws Exception {
        GoodsInfo goodsInfo = goodsInfoManager.queryById(tradeInfo.getGoodsId());
        Assert.assertTrue(goodsInfo != null, "该商品不存在");
        Assert.assertTrue(goodsInfo.getStatus() == GoodsInfo.Status_Published, "该商品已经出售或下架");

        tradeInfo.setTradeState(TradeInfo.Trade_Status_Confirmed);
        tradeInfo.setUpdateTime(new Date());
        guzzBaseDao.update(tradeInfo);

        // 更新商品状态为已售

        goodsInfo.setStatus(GoodsInfo.Status_Sold);
        goodsInfo.setUpdateTime(new Date());
        goodsInfoManager.update(goodsInfo);

        // 保存用户交易记录 (买方)
        TradeRecord buyTradeRecord = new TradeRecord();
        buyTradeRecord.setGoodsId(tradeInfo.getGoodsId());
        buyTradeRecord.setUserId(tradeInfo.getBuyerId());
        buyTradeRecord.setOtherUserId(tradeInfo.getSellerId());
        buyTradeRecord.setCreateTime(new Date());
        buyTradeRecord.setTradeId(tradeInfo.getTradeId());
        buyTradeRecord.setTradeType(TradeRecord.Trade_Type_Buy);
        guzzBaseDao.insert(buyTradeRecord);

        // 保存用户交易记录 (卖方)
        TradeRecord sellTradeRecord = new TradeRecord();
        sellTradeRecord.setGoodsId(tradeInfo.getGoodsId());
        sellTradeRecord.setUserId(tradeInfo.getSellerId());
        sellTradeRecord.setOtherUserId(tradeInfo.getBuyerId());
        sellTradeRecord.setCreateTime(new Date());
        sellTradeRecord.setTradeId(tradeInfo.getTradeId());
        sellTradeRecord.setTradeType(TradeRecord.Trade_Type_Sell);
        guzzBaseDao.insert(sellTradeRecord);

        // 更新ES
        goodsESManager.indexGoods(goodsInfo);
    }

    public PageBean<GoodsInfoBean> boughtList(Long userId, int pageNo, int pageSize) throws Exception {
        SearchExpression se = SearchExpression.forClass(TradeRecord.class, pageNo, pageSize);
        se.and(Terms.eq("userId", userId));
        se.and(Terms.eq("tradeType", 1));

        long count = guzzBaseDao.count(se);
        if (count == 0) {
            return PageBean.getPageBean(0, null);
        }

        Set<Long> goodsIdSet = new HashSet();
        List<TradeRecord> tradeRecordList = guzzBaseDao.list(se);
        for (TradeRecord tradeRecord : tradeRecordList) {
            goodsIdSet.add(tradeRecord.getGoodsId());
        }

        List<GoodsInfo> goodsInfoList = goodsInfoManager.queryList(goodsIdSet);
        List<GoodsInfoBean> goodsInfoBeanList = goodsInfoManager.convertToBean(goodsInfoList);

        return PageBean.getPageBean(count, goodsInfoBeanList);
    }
}
