package com.campus.chatbuy.action;

import com.campus.chatbuy.bean.ResultBean;
import com.campus.chatbuy.manager.IMajorManager;
import com.campus.chatbuy.model.DicMajor;
import com.campus.chatbuy.model.DicUniversity;
import com.campus.chatbuy.model.GoodsCategory;
import com.campus.chatbuy.util.StringUtil;
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
@RequestMapping(value = "/chatbuy/web/major")
public class MajorAction {

	@Autowired
	private IMajorManager majorManager;

	@RequestMapping(value = "queryAllUniversity", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean queryAllUniversity() {
		List<DicUniversity> universityList = majorManager.queryAllUniversity();
		return ResultBean.success(universityList);
	}

	@RequestMapping(value = "queryAllMajor", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean queryAllMajor() {
		List<DicMajor> categoryList = majorManager.queryAllMajor();
		return ResultBean.success(categoryList);
	}

	@RequestMapping(value = "queryMajorByLetter", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean queryMajorByLetter(String letter) {
		if (StringUtil.isEmpty(letter)) return null;
		List<DicMajor> majorList = majorManager.queryMajor(letter);
		return ResultBean.success(majorList);
	}

	@RequestMapping(value = "queryMajorByName", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean queryMajorByName(String name) {
		if (StringUtil.isEmpty(name)) return null;
		List<DicMajor> majorList = majorManager.queryMajorByName(name);
		return ResultBean.success(majorList);
	}
}
