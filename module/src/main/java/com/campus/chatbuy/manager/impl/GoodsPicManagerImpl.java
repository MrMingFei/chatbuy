package com.campus.chatbuy.manager.impl;

import com.campus.chatbuy.bean.PicInfoBean;
import com.campus.chatbuy.manager.IGoodsPicManager;
import com.campus.chatbuy.manager.IPicInfoManager;
import com.campus.chatbuy.model.GoodsPic;
import com.campus.chatbuy.model.PicInfo;
import org.guzz.dao.GuzzBaseDao;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by jinku on 2017/7/11.
 */
@Service
public class GoodsPicManagerImpl implements IGoodsPicManager {

	@Autowired
	private GuzzBaseDao guzzBaseDao;

	@Autowired
	private IPicInfoManager picInfoManager;

	@Override
	public List<GoodsPic> queryByGoodsId(long goodsId) {
		SearchExpression se = SearchExpression.forLoadAll(GoodsPic.class);
		se.and(Terms.eq("goodsId", goodsId));
		se.and(Terms.eq("validity", 1));
		return guzzBaseDao.list(se);
	}

	@Override
	public Map<Long, List<PicInfoBean>> queryByGoodsId(Set<Long> goodsIdSet) throws Exception {
		SearchExpression se = SearchExpression.forLoadAll(GoodsPic.class);
		se.and(Terms.in("goodsId", goodsIdSet));
		se.and(Terms.eq("validity", 1));
		List<GoodsPic> goodsPicList = guzzBaseDao.list(se);
		Set<Long> picIdSet = new HashSet<>();
		for(GoodsPic goodsPic : goodsPicList) {
			picIdSet.add(goodsPic.getPicId());
		}

		Map<Long, PicInfoBean> allPicInfoMap = picInfoManager.queryPicInfo(picIdSet);
		Map<Long, List<PicInfoBean>> goodsPicInfoMap = new HashMap<>();

		for(GoodsPic goodsPic : goodsPicList) {
			List<PicInfoBean> picInfoList = goodsPicInfoMap.get(goodsPic.getGoodsId());
			if (picInfoList == null) {
				picInfoList = new ArrayList<>();
				goodsPicInfoMap.put(goodsPic.getGoodsId(), picInfoList);
			}
			picInfoList.add(allPicInfoMap.get(goodsPic.getPicId()));
		}
		return goodsPicInfoMap;
	}
}
