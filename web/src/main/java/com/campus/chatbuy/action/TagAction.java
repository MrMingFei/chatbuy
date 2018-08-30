package com.campus.chatbuy.action;

import com.campus.chatbuy.bean.ResultBean;
import com.campus.chatbuy.manager.IGoodsTagManager;
import com.campus.chatbuy.model.GoodsTag;
import org.guzz.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by jinku on 17-7-11.
 */
@Controller
@RequestMapping(value = "/chatbuy/web/tag")
public class TagAction {

	@Autowired
	private IGoodsTagManager goodsTagManager;

	@RequestMapping(value = "queryTagByGoodsId", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean queryTagByGoodsId(Long goodsId) {
		Assert.assertNotNull(goodsId, "商品Id为空");
		List<GoodsTag> categoryList = goodsTagManager.queryTagByGoodsId(goodsId);
		return ResultBean.success(categoryList);
	}

	@RequestMapping(value = "queryAllTag", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean queryAllTag() {
		List<GoodsTag> categoryList = goodsTagManager.queryAllTag();
		return ResultBean.success(categoryList);
	}

}
