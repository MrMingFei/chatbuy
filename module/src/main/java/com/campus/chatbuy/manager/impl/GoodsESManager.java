package com.campus.chatbuy.manager.impl;

import com.campus.chatbuy.bean.GoodsInfoBean;
import com.campus.chatbuy.bean.GoodsQueryBean;
import com.campus.chatbuy.bean.PageBean;
import com.campus.chatbuy.bean.UserInfoBean;
import com.campus.chatbuy.esearch.ESearchHelper;
import com.campus.chatbuy.manager.IGoodsCategoryManager;
import com.campus.chatbuy.manager.IGoodsInfoManager;
import com.campus.chatbuy.manager.IUserManager;
import com.campus.chatbuy.model.ConGoodsTag;
import com.campus.chatbuy.model.GoodsInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by jinku on 2017/10/6.
 */
@Service
public class GoodsESManager {

    private static final Logger log = Logger.getLogger(GoodsESManager.class);

    private final static String Goods_Index = "goods";

    private final static String Goods_Type = "goods";

    private final static int Max_Count = 1000;

    private final static float MIN_SCORE = 0.5f;

    @Autowired
    private IUserManager userManager;

    @Autowired
    private IGoodsInfoManager goodsInfoManager;

    @Autowired
    private IGoodsCategoryManager goodsCategoryManager;

    /**
     * 添加或更新索引
     *
     * @throws Exception
     */
    public void indexGoods(GoodsInfo goodsInfo, List<ConGoodsTag> goodsTagList) throws Exception {
        Map<String, Object> dataMap = getGoodsDataMap(goodsInfo);
        List<String> tagIdList = new ArrayList<>();
        for (ConGoodsTag goodsTag : goodsTagList) {
            tagIdList.add(String.valueOf(goodsTag.getTagId()));
        }
        dataMap.put("tagIdList", tagIdList);
        String goodsId = String.valueOf(goodsInfo.getGoodsId());

        ESearchHelper.getInstance().indexOrUpdate(Goods_Index, Goods_Type, goodsId, dataMap);
    }

    /**
     * 添加或更新索引
     *
     * @throws Exception
     */
    public void indexGoods(GoodsInfo goodsInfo) throws Exception {
        Map<String, Object> dataMap = getGoodsDataMap(goodsInfo);
        String goodsId = String.valueOf(goodsInfo.getGoodsId());

        ESearchHelper.getInstance().indexOrUpdate(Goods_Index, Goods_Type, goodsId, dataMap);
    }

    /**
     * 全局搜索
     *
     * @param queryBean
     * @return
     * @throws Exception
     */
    public PageBean<GoodsInfoBean> queryGoods(GoodsQueryBean queryBean) throws Exception {
        Integer pageNo = queryBean.getPageNo();
        Integer pageSize = queryBean.getPageSize();
        if (pageNo == null || pageNo <= 0) {
            pageNo = 1;
        }
        if (pageSize == null || pageSize <= 0 || pageSize > 50) {
            pageSize = 20;
        }
        int offset = (pageNo - 1) * pageSize;

        String queryStr = queryBean.getQuery();
        List<Integer> tagIdList = queryBean.getTagIdList();
        Integer majorId = queryBean.getMajorId();
        Integer universityId = queryBean.getUniversityId();
        List<Integer> categoryIdList = goodsCategoryManager.queryChildCategory(queryBean.getCategoryId());
        Integer goodsStatus = queryBean.getGoodsStatus();
        if (goodsStatus == null) {
            goodsStatus = GoodsInfo.Status_Published;
        }

        // 组装查询请求
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder mainBooleanQuery = QueryBuilders.boolQuery();
        BoolQueryBuilder keywordBooleanQuery = QueryBuilders.boolQuery();
        keywordBooleanQuery.should(QueryBuilders.termQuery("goodsName", queryStr));
        keywordBooleanQuery.should(QueryBuilders.termQuery("goodsDesc", queryStr));
        mainBooleanQuery.must(keywordBooleanQuery);

        BoolQueryBuilder filterBooleanQuery = QueryBuilders.boolQuery();
        if (universityId != null) {
            filterBooleanQuery.must(QueryBuilders.termQuery("universityId", String.valueOf(universityId)));
        }
        if (majorId != null) {
            filterBooleanQuery.must(QueryBuilders.termQuery("majorId", String.valueOf(majorId)));
        }
        if (CollectionUtils.isNotEmpty(categoryIdList)) {
            BoolQueryBuilder categoryBooleanQuery = QueryBuilders.boolQuery();
            for (Integer categoryId : categoryIdList) {
                categoryBooleanQuery.should(QueryBuilders.termQuery("categoryId", String.valueOf(categoryId)));
            }
            filterBooleanQuery.must(categoryBooleanQuery);
        }
        if (goodsStatus != null) {
            filterBooleanQuery.must(QueryBuilders.termQuery("goodsStatus", String.valueOf(goodsStatus)));
        }

        if (CollectionUtils.isNotEmpty(tagIdList)) {
            BoolQueryBuilder tagBooleanQuery = QueryBuilders.boolQuery();
            for (Integer tagId : tagIdList) {
                tagBooleanQuery.should(QueryBuilders.termQuery("tagIdList", String.valueOf(tagId)));
            }
            filterBooleanQuery.must(tagBooleanQuery);
        }

        if (filterBooleanQuery.hasClauses()) {
            mainBooleanQuery.must(filterBooleanQuery);
        }

        sourceBuilder.query(mainBooleanQuery);
        sourceBuilder.from(0);
        sourceBuilder.size(Max_Count);
        sourceBuilder.timeout(new TimeValue(10, TimeUnit.SECONDS));

        SearchRequest searchRequest = new SearchRequest(Goods_Index);
        searchRequest.types(Goods_Type);
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = ESearchHelper.getInstance().search(searchRequest);
        SearchHits searchHits = searchResponse.getHits();
        long totalHits = searchHits.getTotalHits();
        log.info("GoodsESManager totalHits [" + totalHits + "]");
        if (totalHits == 0L) {
            return PageBean.getPageBean(0L, null);
        }

        Set<Long> goodsIdList = new HashSet<>();
        List<SearchHit> hitList = new ArrayList<>();
        for (SearchHit searchHit : searchHits) {
            if (searchHit.getScore() < MIN_SCORE) {
                continue;
            }
            log.info("GoodsESManager queryGoods score [" + searchHit.getScore() + "]");
            hitList.add(searchHit);
        }

        for (int i = offset; i < hitList.size() && i < offset + pageSize; ++i) {
            SearchHit searchHit = hitList.get(i);
            String goodsId = searchHit.getId();
            log.info("GoodsESManager queryGoods goodsId [" + goodsId + "]");
            goodsIdList.add(Long.parseLong(goodsId));
        }

        List<GoodsInfo> goodsInfoList = goodsInfoManager.queryList(goodsIdList);
        List<GoodsInfoBean> goodsInfoBeanList = goodsInfoManager.convertToBean(goodsInfoList);

        return PageBean.getPageBean(hitList.size(), goodsInfoBeanList);
    }

    private Map<String, Object> getGoodsDataMap(GoodsInfo goodsInfo) throws Exception {
        UserInfoBean userInfo = userManager.queryById(goodsInfo.getUserId());
        String universityId = String.valueOf(userInfo.getUniversityId());
        String majorId = String.valueOf(userInfo.getMajorId());
        String categoryId = String.valueOf(goodsInfo.getCategoryId());
        String goodsId = String.valueOf(goodsInfo.getGoodsId());
        String userId = String.valueOf(goodsInfo.getUserId());

        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("goodsName", goodsInfo.getGoodsName());
        dataMap.put("goodsDesc", goodsInfo.getGoodsDesc());

        dataMap.put("goodsId", goodsId);
        dataMap.put("goodsStatus", goodsInfo.getStatus());
        dataMap.put("userId", userId);
        dataMap.put("categoryId", categoryId);
        dataMap.put("universityId", universityId);
        dataMap.put("majorId", majorId);
        dataMap.put("createTime", new Date());
        return dataMap;
    }
}
