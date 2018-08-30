package com.campus.chatbuy.action;

import com.campus.chatbuy.bean.GoodsInfoBean;
import com.campus.chatbuy.bean.GoodsQueryBean;
import com.campus.chatbuy.bean.PageBean;
import com.campus.chatbuy.bean.ResultBean;
import com.campus.chatbuy.manager.IGoodsInfoManager;
import com.campus.chatbuy.manager.IGoodsTagManager;
import com.campus.chatbuy.manager.impl.GoodsESManager;
import com.campus.chatbuy.model.ConGoodsTag;
import com.campus.chatbuy.model.DicUniversity;
import com.campus.chatbuy.model.GoodsInfo;
import com.campus.chatbuy.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by jinku on 2017/10/6.
 */
@Controller
@RequestMapping(value = "/chatbuy/web/init")
public class InitDataAction {

    @Autowired
    private IGoodsInfoManager goodsInfoManager;

    @Autowired
    private GoodsESManager goodsESManager;

    @Autowired
    private IGoodsTagManager goodsTagManager;

    @RequestMapping(value = "indexGoods", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean goodsIndex() throws Exception {
        List<GoodsInfo> goodsInfoList = goodsInfoManager.queryAll();
        for (GoodsInfo goodsInfo : goodsInfoList) {
            List<ConGoodsTag> conGoodsTags = goodsTagManager.queryConGoodsTag(goodsInfo.getGoodsId());
            goodsESManager.indexGoods(goodsInfo, conGoodsTags);
        }
        return ResultBean.success(null);
    }

    @RequestMapping(value = "/queryGoods", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean queryGoods(GoodsQueryBean queryBean) throws Exception {
        return ResultBean.success(goodsESManager.queryGoods(queryBean));
    }
}
