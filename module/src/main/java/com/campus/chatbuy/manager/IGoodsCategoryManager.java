package com.campus.chatbuy.manager;

import com.campus.chatbuy.model.GoodsCategory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jinku on 7/10/17.
 */
public interface IGoodsCategoryManager {

    GoodsCategory queryById(Integer id);

    List<GoodsCategory> queryCategory(Set<Integer> categoryIdSet);

    Map<Integer, GoodsCategory> queryCategoryMap(Set<Integer> categoryIdSet);

    List<GoodsCategory> queryAllCategory();

    List<GoodsCategory> queryTopCategory();

    List<GoodsCategory> queryChildCategory(Integer parentId);

    List<Integer> queryChildCategory(List<Integer> parentIdList);

    GoodsCategory queryParent(Integer childId);

}
