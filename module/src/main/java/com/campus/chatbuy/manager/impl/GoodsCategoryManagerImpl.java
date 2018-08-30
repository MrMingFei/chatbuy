package com.campus.chatbuy.manager.impl;

import com.campus.chatbuy.manager.IGoodsCategoryManager;
import com.campus.chatbuy.model.GoodsCategory;
import org.apache.commons.collections.CollectionUtils;
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
public class GoodsCategoryManagerImpl implements IGoodsCategoryManager {
    @Autowired
    private GuzzBaseDao guzzBaseDao;

    @Override
    public GoodsCategory queryById(Integer id) {
        SearchExpression se = SearchExpression.forClass(GoodsCategory.class);
        se.and(Terms.eq("id", id));
        GoodsCategory goodsCategory = (GoodsCategory) guzzBaseDao.findObject(se);
        return goodsCategory;
    }

    @Override
    public List<GoodsCategory> queryCategory(Set<Integer> categoryIdSet) {
        SearchExpression se = SearchExpression.forLoadAll(GoodsCategory.class);
        se.and(Terms.in("id", categoryIdSet));
        se.and(Terms.eq("validity", GoodsCategory.Valid));

        return guzzBaseDao.list(se);
    }

    @Override
    public Map<Integer, GoodsCategory> queryCategoryMap(Set<Integer> categoryIdSet) {
        SearchExpression se = SearchExpression.forLoadAll(GoodsCategory.class);
        se.and(Terms.in("id", categoryIdSet));

        List<GoodsCategory> goodsCategoryList =  guzzBaseDao.list(se);
        Map<Integer, GoodsCategory> goodsCategoryMap = new HashMap<>();
        for(GoodsCategory category : goodsCategoryList) {
            goodsCategoryMap.put(category.getId().intValue(), category);
        }
        return goodsCategoryMap;
    }

    @Override public List<GoodsCategory> queryAllCategory() {
        SearchExpression se = SearchExpression.forLoadAll(GoodsCategory.class);
        se.and(Terms.eq("validity", GoodsCategory.Valid));
        return guzzBaseDao.list(se);
    }

    @Override
    public List<GoodsCategory> queryTopCategory() {
        SearchExpression se = SearchExpression.forLoadAll(GoodsCategory.class);
        se.and(Terms.eq("parentId", 0));
        se.and(Terms.eq("validity", GoodsCategory.Valid));
        return guzzBaseDao.list(se);
    }

    @Override
    public List<GoodsCategory> queryChildCategory(Integer parentId) {
        SearchExpression se = SearchExpression.forLoadAll(GoodsCategory.class);
        se.and(Terms.eq("parentId", parentId));
        se.and(Terms.eq("validity", GoodsCategory.Valid));
        return guzzBaseDao.list(se);
    }

    @Override
    public List<Integer> queryChildCategory(List<Integer> parentIdList) {
        if (CollectionUtils.isEmpty(parentIdList)) {
            return null;
        }
        SearchExpression se = SearchExpression.forLoadAll(GoodsCategory.class);
        se.and(Terms.in("parentId", parentIdList));
        se.and(Terms.eq("validity", GoodsCategory.Valid));
        List<GoodsCategory> categoryList = guzzBaseDao.list(se);
        Set<Integer> childCategoryList = new HashSet<>();
        for(Integer parentId : parentIdList) {
            boolean hasCategory = false;
            for(GoodsCategory goodsCategory : categoryList) {
                if (goodsCategory.getParentId() == parentId) {
                    hasCategory = true;
                    childCategoryList.add(goodsCategory.getId().intValue());
                }
            }
            if (hasCategory == false) {
                // 说明是子分类,也加入
                childCategoryList.add(parentId);
            }
        }
        return new ArrayList<>(childCategoryList);
    }

    @Override
    public GoodsCategory queryParent(Integer childId) {
        GoodsCategory childCategory = queryById(childId);
        return  queryById(childCategory.getParentId());
    }
}
