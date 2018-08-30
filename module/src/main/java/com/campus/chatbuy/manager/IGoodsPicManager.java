package com.campus.chatbuy.manager;

import com.campus.chatbuy.bean.PicInfoBean;
import com.campus.chatbuy.model.GoodsPic;
import com.campus.chatbuy.model.PicInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jinku on 2017/7/11.
 */
public interface IGoodsPicManager {

	List<GoodsPic> queryByGoodsId(long goodsId);

	Map<Long, List<PicInfoBean>> queryByGoodsId(Set<Long> goodsIdSet) throws Exception;
}
