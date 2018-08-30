package com.campus.chatbuy.manager;

import com.campus.chatbuy.model.ConGoodsTag;
import com.campus.chatbuy.model.GoodsTag;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jinku on 7/10/17.
 */
public interface IGoodsTagManager {

    List<GoodsTag> queryTagByGoodsId(long goodsId);

    List<ConGoodsTag> queryConGoodsTag(long goodsId);

    List<ConGoodsTag> queryAllConGoodsTag(long goodsId);

    Map<Long, List<GoodsTag>> queryTagByGoodsId(Set<Long> goodsIdSet);

    List<GoodsTag> queryTagList(Set<Long> tagIdSet);

    List<GoodsTag> queryAllTag();

}
