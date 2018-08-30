package com.campus.chatbuy.manager.impl;

import com.campus.chatbuy.bean.*;
import com.campus.chatbuy.dao.GoodsInfoDao;
import com.campus.chatbuy.id.generator.IdGeneratorFactory;
import com.campus.chatbuy.manager.*;
import com.campus.chatbuy.model.*;
import com.campus.chatbuy.util.StringUtil;
import com.campus.chatbuy.util.ThreadLocalUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.guzz.GuzzContext;
import org.guzz.dao.GuzzBaseDao;
import org.guzz.orm.mapping.FormBeanRowDataLoader;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.guzz.orm.sql.BindedCompiledSQL;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.transaction.TransactionManager;
import org.guzz.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by jinku on 2017/7/7.
 */
@Service
public class GoodsInfoManagerImpl implements IGoodsInfoManager {

    @Autowired
    private GuzzBaseDao guzzBaseDao;

    @Autowired
    private IGoodsPicManager goodsPicManager;

    @Autowired
    private IUserManager userManager;

    @Autowired
    private IGoodsCategoryManager goodsCategoryManager;

    @Autowired
    private IGoodsTagManager goodsTagManager;

    @Autowired
    private GoodsInfoDao goodsInfoDao;

    @Autowired
    private GoodsESManager goodsESManager;

    @Override
    public GoodsInfo queryById(long goodsId) {
        SearchExpression se = SearchExpression.forClass(GoodsInfo.class);
        se.and(Terms.eq("goodsId", goodsId));
        GoodsInfo goodsInfo = (GoodsInfo) guzzBaseDao.findObject(se);
        return goodsInfo;
    }

    @Override
    public void update(GoodsInfo goodsInfo) {
        guzzBaseDao.update(goodsInfo);
    }

    @Override
    public List<GoodsInfo> queryList(Set<Long> goodsIdList) {
        SearchExpression se = SearchExpression.forLoadAll(GoodsInfo.class);
        se.and(Terms.in("goodsId", goodsIdList));
        return guzzBaseDao.list(se);
    }

    @Override
    public Map<Long, GoodsInfo> queryMapList(Set<Long> goodsIdList) {
        SearchExpression se = SearchExpression.forLoadAll(GoodsInfo.class);
        se.and(Terms.in("goodsId", goodsIdList));
        List<GoodsInfo> goodsInfoList =  guzzBaseDao.list(se);
        if (CollectionUtils.isEmpty(goodsInfoList)) {
            return null;
        }
        Map<Long, GoodsInfo> goodsInfoMap = new HashMap<>();
        for(GoodsInfo goodsInfo : goodsInfoList) {
            goodsInfoMap.put(goodsInfo.getGoodsId(), goodsInfo);
        }
        return goodsInfoMap;
    }

    @Override
    public PageBean<GoodsInfo> queryGoods(GoodsQueryBean queryBean) {
        Integer pageNo = queryBean.getPageNo();
        Integer pageSize = queryBean.getPageSize();
        if (pageNo == null) {
            pageNo = 1;
        }
        if (pageSize == null) {
            pageSize = 20;
        }
        queryBean.setPageNo(pageNo);
        queryBean.setPageSize(pageSize);
        Integer universityId = queryBean.getUniversityId();
        Integer majorId = queryBean.getMajorId();
        List<Integer> tagIdList = queryBean.getTagIdList();
        List<Integer> categoryIdList = goodsCategoryManager.queryChildCategory(queryBean.getCategoryId());

        Integer goodsStatus = queryBean.getGoodsStatus();
        if (goodsStatus == null) {
            goodsStatus = GoodsInfo.Status_Published;
            queryBean.setGoodsStatus(goodsStatus);
        }

        if (CollectionUtils.isEmpty(tagIdList)) {
            tagIdList = null;
        }

        if (CollectionUtils.isEmpty(categoryIdList)) {
            categoryIdList = null;
        }

        // 查询场景
        // 大学 专业 标签 分类 商品状态
        // 涉及表：商品表goods_info/用户表user_info/商品标签关联表con_goods_tag

        // 大学 专业 标签 均未空
        if (universityId == null && majorId == null && CollectionUtils.isEmpty(tagIdList)) {
            // 只需查询商品表
            SearchExpression se = SearchExpression.forClass(GoodsInfo.class, pageNo, pageSize);
            if (CollectionUtils.isNotEmpty(categoryIdList)) {
                se.and(Terms.in("categoryId", categoryIdList));
            }
            if (goodsStatus != null) {
                se.and(Terms.eq("status", goodsStatus));
            }
            se.setOrderBy("goodsId desc");
            long count = guzzBaseDao.count(se);
            List<GoodsInfo> infoList = guzzBaseDao.list(se);
            return PageBean.getPageBean(count, infoList);
        }

        // 连接商品/用户/标签 表
        long totalCount = goodsInfoDao.countByCondition(tagIdList, universityId, majorId, categoryIdList, goodsStatus);
        if (totalCount == 0) {
            return PageBean.getPageBean(totalCount, null);
        }

        int startNum = (pageNo - 1) * pageSize;

        List<GoodsInfo> goodsInfoList = goodsInfoDao.selectByCondition(tagIdList, universityId, majorId,
                categoryIdList, goodsStatus, startNum, pageSize);

        return PageBean.getPageBean(totalCount, goodsInfoList);
    }

    @Override
    public List<GoodsInfoBean> convertToBean(List<GoodsInfo> goodsInfoList) throws Exception {
        if (goodsInfoList == null) {
            return null;
        }
        Set<Long> goodsIdSet = new HashSet<>();
        Set<Long> userIdSet = new HashSet<>();
        Set<Integer> categoryIdSet = new HashSet<>();
        List<GoodsInfoBean> infoBeanList = new ArrayList<>();
        for (GoodsInfo goodsInfo : goodsInfoList) {
            goodsIdSet.add(goodsInfo.getGoodsId());
            userIdSet.add(goodsInfo.getUserId());
            categoryIdSet.add(goodsInfo.getCategoryId());
            GoodsInfoBean goodsInfoBean = new GoodsInfoBean();
            BeanUtils.copyProperties(goodsInfoBean, goodsInfo);
            goodsInfoBean.setCreateTime(String.valueOf(goodsInfo.getCreateTime().getTime()));
            if (goodsInfo.getUpdateTime() != null) {
                goodsInfoBean.setUpdateTime(String.valueOf(goodsInfo.getUpdateTime().getTime()));
            }
            goodsInfoBean.setOriginPrice(StringUtil.convertCent2Yuan(goodsInfo.getOriginPrice()));
            goodsInfoBean.setPrice(StringUtil.convertCent2Yuan(goodsInfo.getPrice()));
            infoBeanList.add(goodsInfoBean);
        }
        // 批量查询
        Map<Long, List<GoodsTag>> goodsTagMap = goodsTagManager.queryTagByGoodsId(goodsIdSet);
        Map<Long, UserInfoBean> userInfoBeanMap = userManager.queryList(userIdSet);
        List<GoodsCategory> categoryList = goodsCategoryManager.queryCategory(categoryIdSet);
        Map<Long, List<PicInfoBean>> goodsPicMap = goodsPicManager.queryByGoodsId(goodsIdSet);
        for (GoodsInfoBean infoBean : infoBeanList) {
            UserInfoBean originUserInfo = userInfoBeanMap.get(Long.parseLong(infoBean.getUserId()));
            UserInfoBean userInfoBean = new UserInfoBean();
            BeanUtils.copyProperties(userInfoBean, originUserInfo);

            if (originUserInfo.getAvatarPicInfo() != null) {
                PicInfoBean picInfoBean = new PicInfoBean();
                BeanUtils.copyProperties(picInfoBean, originUserInfo.getAvatarPicInfo());
                userInfoBean.setAvatarPicInfo(picInfoBean);
            }
            // 填充用户信息
            infoBean.setUserInfo(userInfoBean);

            // 填充标签信息
            List<GoodsTag> goodsTagList = goodsTagMap.get(Long.parseLong(infoBean.getGoodsId()));
            List<TagBean> tagBeanList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(goodsTagList)) {
                for (GoodsTag goodsTag : goodsTagList) {
                    tagBeanList.add(TagBean.convert2Bean(goodsTag));
                }
            }
            infoBean.setTagList(tagBeanList);
            // 填充分类信息
            for (GoodsCategory goodsCategory : categoryList) {
                if (infoBean.getCategoryId().equals(String.valueOf(goodsCategory.getId()))) {
                    infoBean.setCategoryName(goodsCategory.getCategoryName());
                    break;
                }
            }
            // 填充图片信息
            List<PicInfoBean> picList = goodsPicMap.get(Long.parseLong(infoBean.getGoodsId()));
            infoBean.setPicList(picList);
        }
        return infoBeanList;
    }

    @Override
    public GoodsInfoBean convertToBean(GoodsInfo goodsInfo) throws Exception {
        List<GoodsInfo> goodsInfoList = new ArrayList<>();
        goodsInfoList.add(goodsInfo);
        return convertToBean(goodsInfoList).get(0);
    }

    /**
     * 查询指定用户的商品
     *
     * @param userId
     * @param goodsStatus
     * @return
     */
    @Override
    public PageBean<GoodsInfo> queryUserGoods(long userId, List<Integer> goodsStatus, int pageNo, int pageSize) {
        SearchExpression se = SearchExpression.forClass(GoodsInfo.class, pageNo, pageSize);
        se.and(Terms.eq("userId", userId));
        se.and(Terms.in("status", goodsStatus));
        se.setOrderBy("goodsId desc");

        long count = guzzBaseDao.count(se);
        if (count == 0) {
            return PageBean.getPageBean(0, null);
        }

        List<GoodsInfo> goodsInfos = guzzBaseDao.list(se);
        return PageBean.getPageBean(count, goodsInfos);
    }

    @Override
    public UserGoodsStat queryUserGoodsStat(long userId) {
        UserGoodsStat userGoodsStat = new UserGoodsStat();
        SearchExpression se = SearchExpression.forLoadAll(GoodsInfo.class);
        se.and(Terms.eq("userId", userId));
        se.and(Terms.eq("status", GoodsInfo.Status_Sold));
        long selloutCount = guzzBaseDao.count(se);
        userGoodsStat.setSelloutCount(selloutCount);

        se = SearchExpression.forLoadAll(GoodsInfo.class);
        se.and(Terms.eq("userId", userId));
        se.and(Terms.eq("status", GoodsInfo.Status_Published));
        long sellingCount = guzzBaseDao.count(se);
        userGoodsStat.setSellingCount(sellingCount);

        return userGoodsStat;
    }

    @Override
    public long publishGoods(GoodsPublishBean publishBean) throws Exception {
        //保存GoodsInfo
        GoodsInfo goodsInfo = new GoodsInfo();
        goodsInfo.setGoodsId(IdGeneratorFactory.generateId());
        goodsInfo.setGoodsName(publishBean.getGoodsName());
        goodsInfo.setGoodsDesc(publishBean.getGoodsDesc());
        goodsInfo.setCategoryId(Integer.parseInt(publishBean.getCategoryId()));
        goodsInfo.setCreateTime(new Date());
        goodsInfo.setPrice(StringUtil.convertYuan2Cent(publishBean.getPrice()));
        goodsInfo.setOriginPrice(StringUtil.convertYuan2Cent(publishBean.getOriginPrice()));
        goodsInfo.setUserId(ThreadLocalUtil.getUserId());
        goodsInfo.setReadNum(0L);
        goodsInfo.setStatus(GoodsInfo.Status_Published);
        goodsInfo.setSource(GoodsInfo.Source_ChatBuy);
        goodsInfo.setPayWay(GoodsInfo.Pay_Way_offline);
        guzzBaseDao.insert(goodsInfo);

        //保存GoodsPic
        if (publishBean.getPicList() != null && publishBean.getPicList().size() > 0) {
            for (Long picId : publishBean.getPicList()) {
                GoodsPic goodsPic = new GoodsPic();
                goodsPic.setPicId(picId);
                goodsPic.setValidity(1);
                goodsPic.setCreateTime(new Date());
                goodsPic.setGoodsId(goodsInfo.getGoodsId());
                guzzBaseDao.insert(goodsPic);
            }
        }

        List<ConGoodsTag> goodsTagList = new ArrayList<>();
        //保存ConGoodsTag
        if (publishBean.getTagList() != null && publishBean.getTagList().size() > 0) {
            for (Long tagId : publishBean.getTagList()) {
                ConGoodsTag conGoodsTag = new ConGoodsTag();
                conGoodsTag.setTagId(tagId);
                conGoodsTag.setCreateTime(new Date());
                conGoodsTag.setValidity(1);
                conGoodsTag.setGoodsId(goodsInfo.getGoodsId());
                goodsTagList.add(conGoodsTag);
                guzzBaseDao.insert(conGoodsTag);
            }
        }
        // 添加索引
        goodsESManager.indexGoods(goodsInfo, goodsTagList);
        return goodsInfo.getGoodsId();
    }

    @Override
    public void updateGoods(GoodsPublishBean publishBean) throws Exception {
        GoodsInfo goodsInfo = queryById(publishBean.getGoodsId());
        if (StringUtil.isNotEmpty(publishBean.getGoodsName())) {
            goodsInfo.setGoodsName(publishBean.getGoodsName());
        }
        if (StringUtil.isNotEmpty(publishBean.getGoodsDesc())) {
            goodsInfo.setGoodsDesc(publishBean.getGoodsDesc());
        }
        if (StringUtil.isNotEmpty(publishBean.getCategoryId())) {
            goodsInfo.setCategoryId(Integer.parseInt(publishBean.getCategoryId()));
        }
        if (publishBean.getPrice() != null) {
            goodsInfo.setPrice(StringUtil.convertYuan2Cent(publishBean.getPrice()));
        }
        if (publishBean.getOriginPrice() != null) {
            goodsInfo.setOriginPrice(StringUtil.convertYuan2Cent(publishBean.getOriginPrice()));
        }
        goodsInfo.setUpdateTime(new Date());
        guzzBaseDao.update(goodsInfo);

        //保存GoodsPic
        List<GoodsPic> goodsPics = goodsPicManager.queryByGoodsId(goodsInfo.getGoodsId());
        for (GoodsPic goodsPic : goodsPics) {
            boolean isExist = false;
            if (CollectionUtils.isNotEmpty(publishBean.getPicList())) {
                for (Long picId : publishBean.getPicList()) {
                    if (picId.equals(goodsPic.getPicId())) {
                        isExist = true;
                        break;
                    }
                }
            }
            if (isExist) {
                continue;
            }
            goodsPic.setValidity(0);
            goodsPic.setUpdateTime(new Date());
            guzzBaseDao.update(goodsPic);
        }

        if (CollectionUtils.isNotEmpty(publishBean.getPicList())) {
            for (Long picId : publishBean.getPicList()) {
                boolean isExist = false;
                for (GoodsPic goodsPic : goodsPics) {
                    if (picId.equals(goodsPic.getPicId())) {
                        isExist = true;
                    }
                }
                if (isExist) {
                    continue;
                }
                GoodsPic goodsPic = new GoodsPic();
                goodsPic.setValidity(1);
                goodsPic.setPicId(picId);
                goodsPic.setCreateTime(new Date());
                goodsPic.setGoodsId(goodsInfo.getGoodsId());
                guzzBaseDao.insert(goodsPic);
            }
        }

        List<ConGoodsTag> goodsTagList = new ArrayList<>();
        //保存ConGoodsTag
        List<ConGoodsTag> conGoodsTags = goodsTagManager.queryConGoodsTag(goodsInfo.getGoodsId());
        for (ConGoodsTag conGoodsTag : conGoodsTags) {
            boolean isExist = false;
            if (CollectionUtils.isNotEmpty(publishBean.getTagList())) {
                for (Long tagId : publishBean.getTagList()) {
                    if (tagId.equals(conGoodsTag.getTagId())) {
                        isExist = true;
                        break;
                    }
                }
            }
            if (isExist) {
                goodsTagList.add(conGoodsTag);
                continue;
            }
            conGoodsTag.setValidity(0);
            conGoodsTag.setUpdateTime(new Date());
            guzzBaseDao.update(conGoodsTag);
        }

        List<ConGoodsTag> conAllGoodsTags = goodsTagManager.queryAllConGoodsTag(goodsInfo.getGoodsId());

        if (CollectionUtils.isNotEmpty(publishBean.getTagList())) {
            for (Long tagId : publishBean.getTagList()) {
                boolean isExist = false;
                for (ConGoodsTag conGoodsTag : conAllGoodsTags) {
                    if (tagId.equals(conGoodsTag.getTagId())) {
                        isExist = true;
                        if (conGoodsTag.getValidity() == 0) {
                            conGoodsTag.setValidity(1);
                            conGoodsTag.setUpdateTime(new Date());
                            guzzBaseDao.update(conGoodsTag);
                            goodsTagList.add(conGoodsTag);
                        }
                    }
                }
                if (isExist) {
                    continue;
                }
                ConGoodsTag conGoodsTag = new ConGoodsTag();
                conGoodsTag.setTagId(tagId);
                conGoodsTag.setValidity(1);
                conGoodsTag.setCreateTime(new Date());
                conGoodsTag.setGoodsId(goodsInfo.getGoodsId());
                guzzBaseDao.insert(conGoodsTag);
                goodsTagList.add(conGoodsTag);
            }
        }

        // 更新索引
        goodsESManager.indexGoods(goodsInfo, goodsTagList);
    }

    /**
     * 修改商品价格
     *
     * @param goodsId 商品id
     * @param price   修改后的价格
     * @return 修改后的价格
     */
    @Override
    public void changeGoodsPrice(long goodsId, long userId, String price) throws Exception{
        GoodsInfo goodsInfo = queryById(goodsId);
        Assert.assertNotNull(goodsInfo, "没有查到该商品");
        Assert.assertTrue(goodsInfo.getUserId() == userId, "必须本人才能修改商品");
        goodsInfo.setPrice(StringUtil.convertYuan2Cent(price));
        guzzBaseDao.update(goodsInfo);
    }

    @Override
    public List<GoodsInfo> queryAll() {
        SearchExpression se = SearchExpression.forLoadAll(GoodsInfo.class);
        return guzzBaseDao.list(se);
    }
}
