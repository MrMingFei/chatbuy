package com.campus.chatbuy.action;

import com.campus.chatbuy.bean.ResultBean;
import com.campus.chatbuy.manager.IUserSignManager;
import com.campus.chatbuy.model.UserSign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by jinku on 2017/7/13.
 */
@Controller
@RequestMapping(value = "/chatbuy/web/userSign")
public class UserSignAction {

	@Autowired
	private IUserSignManager userSignManager;

	@RequestMapping(value = "querySign", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean querySign(String userId) throws Exception {
//		UserSign userSign = userSignManager.query(userId);
//		return ResultBean.success(userSign);
		return ResultBean.failure("已废弃");
	}
}
