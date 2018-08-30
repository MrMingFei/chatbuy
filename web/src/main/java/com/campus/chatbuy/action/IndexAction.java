package com.campus.chatbuy.action;

import com.campus.chatbuy.bean.ResultBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by jinku on 2017/8/3.
 */
@Controller
public class IndexAction {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home() {
		return index();
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView("error_json");
		modelAndView.addObject("exception", "request url is not found");
		return modelAndView;
	}

}
