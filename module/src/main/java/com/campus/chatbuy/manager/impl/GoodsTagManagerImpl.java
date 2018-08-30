package com.campus.chatbuy.manager.impl;

import com.campus.chatbuy.manager.IGoodsTagManager;
import com.campus.chatbuy.model.ConGoodsTag;
import com.campus.chatbuy.model.GoodsTag;
import org.guzz.dao.GuzzBaseDao;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by jinku on 7/10/17.
 */
@Service
public class GoodsTagManagerImpl implements IGoodsTagManager {

    @Autowired
    private GuzzBaseDao guzzBaseDao;

    @Override
    public List<GoodsTag> queryTagByGoodsId(long goodsId) {
        Set<Long> tagIdSet = new HashSet<>();
        List<ConGoodsTag> goodsTagsList = queryConGoodsTag(goodsId);
        for(ConGoodsTag goodsTag : goodsTagsList) {
            tagIdSet.add(goodsTag.getTagId());
        }

        return queryTagList(tagIdSet);
    }

    @Override
    public List<ConGoodsTag> queryConGoodsTag(long goodsId) {
        SearchExpression se = SearchExpression.forClass(ConGoodsTag.class);
        se.and(Terms.eq("goodsId", goodsId));
        se.and(Terms.eq("validity", 1));
        return guzzBaseDao.list(se);
    }

    @Override
    public List<ConGoodsTag> queryAllConGoodsTag(long goodsId) {
        SearchExpression se = SearchExpression.forClass(ConGoodsTag.class);
        se.and(Terms.eq("goodsId", goodsId));
        return guzzBaseDao.list(se);
    }

    @Override public Map<Long, List<GoodsTag>> queryTagByGoodsId(Set<Long> goodsIdSet) {
        SearchExpression se = SearchExpression.forLoadAll(ConGoodsTag.class);
        se.and(Terms.in("goodsId", goodsIdSet));
        se.and(Terms.eq("validity", 1));
        List<ConGoodsTag> conGoodsTagList = guzzBaseDao.list(se);
        Set<Long> tagIdSet = new HashSet<>();
        Map<Long, List<GoodsTag>> goodsPicMap = new HashMap<>();
        for(ConGoodsTag conGoodsTag : conGoodsTagList) {
            tagIdSet.add(conGoodsTag.getTagId());
        }
        List<GoodsTag> goodsTagList = queryTagList(tagIdSet);

        for(ConGoodsTag conGoodsTag : conGoodsTagList) {
            List<GoodsTag> tagList = goodsPicMap.get(conGoodsTag.getGoodsId());
            if (tagList == null) {
                tagList = new ArrayList<>();
                goodsPicMap.put(conGoodsTag.getGoodsId(), tagList);
            }
            for(GoodsTag goodsTag : goodsTagList) {
                if (goodsTag.getId().equals(conGoodsTag.getTagId())) {
                    tagList.add(goodsTag);
                    break;
                }
            }
        }
        return goodsPicMap;
    }

    @Override
    public List<GoodsTag> queryTagList(Set<Long> tagIdSet) {
        SearchExpression se = SearchExpression.forClass(GoodsTag.class);
        se.and(Terms.in("id", tagIdSet));
        return guzzBaseDao.list(se);
    }

    @Override public List<GoodsTag> queryAllTag() {
        SearchExpression se = SearchExpression.forLoadAll(GoodsTag.class);
        return guzzBaseDao.list(se);
    }
}
