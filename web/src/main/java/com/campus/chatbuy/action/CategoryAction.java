package com.campus.chatbuy.action;

import com.campus.chatbuy.bean.ResultBean;
import com.campus.chatbuy.manager.IGoodsCategoryManager;
import com.campus.chatbuy.model.GoodsCategory;
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
@RequestMapping(value = "/chatbuy/web/category")
public class CategoryAction {

	@Autowired
	private IGoodsCategoryManager goodsCategoryManager;

	@RequestMapping(value = "queryTop", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean queryTop() {
		List<GoodsCategory> categoryList = goodsCategoryManager.queryTopCategory();
		return ResultBean.success(categoryList);
	}

	@RequestMapping(value = "queryAll", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean queryAll() {
		List<GoodsCategory> categoryList = goodsCategoryManager.queryAllCategory();
		return ResultBean.success(categoryList);
	}

	@RequestMapping(value = "queryParent", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean queryParent(Integer childId) {
		Assert.assertNotNull(childId, "参数为空");
		GoodsCategory parentCategory = goodsCategoryManager.queryParent(childId);
		return ResultBean.success(parentCategory);
	}

	@RequestMapping(value = "queryChild", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean queryChild(Integer parentId) {
		Assert.assertNotNull(parentId, "参数为空");
		List<GoodsCategory> categoryList = goodsCategoryManager.queryChildCategory(parentId);
		return ResultBean.success(categoryList);
	}
}
