package com.campus.chatbuy.action;

import com.campus.chatbuy.bean.*;
import com.campus.chatbuy.manager.IGoodsInfoManager;
import com.campus.chatbuy.manager.IGoodsTagManager;
import com.campus.chatbuy.manager.impl.GoodsESManager;
import com.campus.chatbuy.manager.impl.TradeInfoManager;
import com.campus.chatbuy.model.ConGoodsTag;
import com.campus.chatbuy.model.GoodsInfo;
import com.campus.chatbuy.model.TradeInfo;
import com.campus.chatbuy.util.CookieUtil;
import com.campus.chatbuy.util.StringUtil;
import com.campus.chatbuy.util.ThreadLocalUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.guzz.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by jinku on 2017/7/7.
 * <p/>
 * 商品发布/详情/筛选相关接口
 */
@Controller
@RequestMapping(value = "/chatbuy/web/goods")
public class GoodsAction {

    private static final Logger log = Logger.getLogger(GoodsAction.class);

    @Autowired
    private IGoodsInfoManager goodsManager;

    @Autowired
    private GoodsESManager goodsESManager;

    @Autowired
    private TradeInfoManager tradeInfoManager;

    @RequestMapping(value = "/queryGoods", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean queryGoods(GoodsQueryBean queryBean) throws Exception {
        log.info("queryGoods queryBean is [" + queryBean + "]");

        if (StringUtil.isNotEmpty(queryBean.getQuery())) {// lucene搜索
            return ResultBean.success(goodsESManager.queryGoods(queryBean));
        }

        // 数据库搜索
        PageBean<GoodsInfo> pageBean = goodsManager.queryGoods(queryBean);
        List<GoodsInfoBean> infoBeanList = goodsManager.convertToBean(pageBean.getRows());
        return ResultBean.success(PageBean.getPageBean(pageBean.getTotal(), infoBeanList));
    }

    @RequestMapping(value = "/goodsInfo", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean goodsInfo(Long goodsId, Integer viewType) throws Exception {
        log.info("goodsInfo goodsId is [" + goodsId + "]");
        Assert.assertNotNull(goodsId, "goodsId is null");
        GoodsInfo goodsInfo = goodsManager.queryById(goodsId);
        if (viewType != null && viewType == 1) {// 阅读
            // 阅读数加1
            if (goodsInfo.getReadNum() == null) {
                goodsInfo.setReadNum(new Long(1));
            } else {
                goodsInfo.setReadNum(goodsInfo.getReadNum() + 1);
            }
            goodsManager.update(goodsInfo);
        }
        return ResultBean.success(goodsManager.convertToBean(goodsInfo));
    }

    @RequestMapping(value = "/userGoods", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean userGoods(Long userId, String status, Integer pageNo, Integer pageSize) throws Exception {
        Assert.assertNotNull(userId, "userId is null");
        Assert.assertNotEmpty(status, "status is null");
        if (pageNo == null) {
            pageNo = 1;
        }
        if (pageSize == null) {
            pageSize = 20;
        }
        String[] statusArray = status.split(",");
        List<Integer> statusList = new ArrayList<>();
        for (int i = 0; i < statusArray.length; i++) {
            if (StringUtil.isEmpty(statusArray[i])) {
                continue;
            }
            statusList.add(Integer.parseInt(statusArray[i]));
        }
        PageBean<GoodsInfo> pageBean = goodsManager.queryUserGoods(userId, statusList, pageNo, pageSize);
        List<GoodsInfoBean> infoBeanList = goodsManager.convertToBean(pageBean.getRows());
        return ResultBean.success(PageBean.getPageBean(pageBean.getTotal(), infoBeanList));
    }

    @RequestMapping(value = "/userGoodsStat", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean userGoodsStat(Long userId) throws Exception {
        Assert.assertNotNull(userId, "userId is null");

        UserGoodsStat userGoodsStat = goodsManager.queryUserGoodsStat(userId);
        return ResultBean.success(userGoodsStat);
    }

    @RequestMapping(value = "/publishGoods", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean publishGoods(GoodsPublishBean publishBean) throws Exception {
        log.info("publishGoods publishBean is [" + publishBean + "]");

        // 验证参数
        Assert.assertNotEmpty(publishBean.getGoodsName(), "goodsName is null");
        Assert.assertNotEmpty(publishBean.getGoodsDesc(), "goodsDesc is null");
        Assert.assertNotNull(publishBean.getOriginPrice(), "originPrice is illegal");
        Assert.assertNotNull(publishBean.getPrice(), "price is illegal");
        Assert.assertNotEmpty(publishBean.getCategoryId(), "categoryId is null");

        long goodsId = goodsManager.publishGoods(publishBean);
        return ResultBean.success(String.valueOf(goodsId));
    }

    @RequestMapping(value = "/updateGoods", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean updateGoods(GoodsPublishBean publishBean) throws Exception {
        log.info("updateGoods publishBean is [" + publishBean + "]");
        Assert.assertNotNull(publishBean.getGoodsId(), "goodsId is null");
        GoodsInfo goodsInfo = goodsManager.queryById(publishBean.getGoodsId());
        Assert.assertNotNull(goodsInfo, "该商品不存在");
        Assert.assertTrue(goodsInfo.getStatus() == GoodsInfo.Status_Published
                || goodsInfo.getStatus() == GoodsInfo.Status_UnPublished, "该商品不允许更新");
        goodsManager.updateGoods(publishBean);
        return ResultBean.success(null);
    }

    @RequestMapping(value = "/changePrice", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean changePrice(Long goodsId, String price) throws Exception {
        Assert.assertNotNull(goodsId, "goodsId is null");
        Assert.assertNotEmpty(price, "price is null");
        GoodsInfo goodsInfo = goodsManager.queryById(goodsId);
        Assert.assertNotNull(goodsInfo, "该商品不存在");
        Assert.assertTrue(goodsInfo.getStatus() == GoodsInfo.Status_Published
                || goodsInfo.getStatus() == GoodsInfo.Status_UnPublished, "该商品不允许更新");
        long userId = ThreadLocalUtil.getUserId();
        goodsManager.changeGoodsPrice(goodsId, userId, price);
        return ResultBean.success(null);
    }

    @RequestMapping(value = "/managerGoods", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean changeStatus(Long goodsId, Integer type) throws Exception {
        // type:1=上架;2=下架;3=删除
        Assert.assertNotNull(goodsId, "goodsId is null");
        Assert.assertNotNull(type, "type is invalid");

        long userId = ThreadLocalUtil.getUserId();
        GoodsInfo goodsInfo = goodsManager.queryById(goodsId);
        Assert.assertTrue(goodsInfo.getUserId().longValue() == userId, "操作非法");

        if (type == 1) {// 必须为下架和已出售状态, 才可上架商品
            Assert.assertTrue(goodsInfo.getStatus() == GoodsInfo.Status_UnPublished ||
                    goodsInfo.getStatus() == GoodsInfo.Status_Sold, "操作非法");
            goodsInfo.setStatus(GoodsInfo.Status_Published);
            log.info("managerGoods publish; goodsId is [" + goodsId + "]");
        } else if (type == 2) {// 必须为在售状态, 才可下架
            Assert.assertTrue(goodsInfo.getStatus() == GoodsInfo.Status_Published, "操作非法");
            goodsInfo.setStatus(GoodsInfo.Status_UnPublished);
            log.info("managerGoods unpublish; goodsId is [" + goodsId + "]");
        } else if (type == 3) {// 必须为下架和已出售状态, 才可删除
            Assert.assertTrue(goodsInfo.getStatus() == GoodsInfo.Status_UnPublished ||
                    goodsInfo.getStatus() == GoodsInfo.Status_Sold, "操作非法");
            goodsInfo.setStatus(GoodsInfo.Status_Deleted);
            log.info("managerGoods delete; goodsId is [" + goodsId + "]");
        } else {
            Assert.assertTrue(false, "操作非法");
        }
        goodsInfo.setUpdateTime(new Date());
        goodsManager.update(goodsInfo);

        // 更新索引
        goodsESManager.indexGoods(goodsInfo);
        return ResultBean.success(null);
    }

    @RequestMapping(value = "/goodsStatus", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean goodsStatus(Long goodsId) throws Exception {
        Assert.assertNotNull(goodsId, "goodsId is null");

        GoodsInfo goodsInfo = goodsManager.queryById(goodsId);
        Assert.assertNotNull(goodsInfo, "该商品不存在");

        String buyerId = null;
        String tradeId = null;
        if (goodsInfo.getStatus() == GoodsInfo.Status_Sold) {
            // 查询交易信息
            TradeInfo tradeInfo = tradeInfoManager.queryByGoodsId(goodsId);
            buyerId = String.valueOf(tradeInfo.getBuyerId());
            tradeId = String.valueOf(tradeInfo.getTradeId());
        }

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("status", goodsInfo.getStatus());
        dataMap.put("buyerId", buyerId);
        dataMap.put("tradeId", tradeId);
        return ResultBean.success(dataMap);
    }

}
