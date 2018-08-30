package com.campus.chatbuy.manager;

import com.campus.chatbuy.bean.*;
import com.campus.chatbuy.model.GoodsInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jinku on 2017/7/7.
 */
public interface IGoodsInfoManager {

    GoodsInfo queryById(long goodsId);

    void update(GoodsInfo goodsInfo);

    List<GoodsInfo> queryList(Set<Long> goodsIdList);

    Map<Long, GoodsInfo> queryMapList(Set<Long> goodsIdList);

    PageBean<GoodsInfo> queryGoods(GoodsQueryBean queryBean);

    List<GoodsInfoBean> convertToBean(List<GoodsInfo> goodsInfoList) throws Exception;

    GoodsInfoBean convertToBean(GoodsInfo goodsInfo) throws Exception;

    PageBean<GoodsInfo> queryUserGoods(long userId, List<Integer> goodsStatus, int pageNo, int pageSize);

    UserGoodsStat queryUserGoodsStat(long userId);

    long publishGoods(GoodsPublishBean publishBean) throws Exception;

    void updateGoods(GoodsPublishBean publishBean) throws Exception;

    void changeGoodsPrice(long goodsId, long userId, String price) throws Exception;

    List<GoodsInfo> queryAll();
}
