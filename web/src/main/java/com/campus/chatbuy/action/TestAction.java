package com.campus.chatbuy.action;

import com.campus.chatbuy.bean.PicInfoBean;
import com.campus.chatbuy.bean.ResultBean;
import com.campus.chatbuy.config.QStorageConfig;
import com.campus.chatbuy.manager.IGoodsTagManager;
import com.campus.chatbuy.manager.IPicInfoManager;
import com.campus.chatbuy.model.GoodsTag;
import com.campus.chatbuy.service.RedisService;
import com.campus.chatbuy.sms.JiGuangSmsUtil;
import com.campus.chatbuy.sms.SmsTemplate;
import com.campus.chatbuy.storage.QPictureUtil;
import com.campus.chatbuy.util.StringUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.guzz.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by jinku on 2017/8/3.
 */
@Controller
@RequestMapping(value = "/chatbuy/web/test")
public class TestAction {

	@Autowired
	private IGoodsTagManager goodsTagManager;

	@Autowired
	private RedisService redisService;

	@Autowired
	private IPicInfoManager picInfoManager;

	@RequestMapping(value = "testMysql", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean testMysql() {
		List<GoodsTag> categoryList = goodsTagManager.queryAllTag();
		return ResultBean.success(categoryList);
	}

	@RequestMapping(value = "testRedis", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean testRedis() {
		redisService.set("test_redis", "test");
		String test = redisService.get("test_redis");
		Assert.assertEquals(test, "test", "redis is exception");
		redisService.delete("test_redis");
		return ResultBean.success(1);
	}

	@RequestMapping(value = "/testUploadPic", method = RequestMethod.POST) @ResponseBody
	public ResultBean uploadGoodsPic(MultipartFile file) throws Exception {
		PicInfoBean picInfo = QPictureUtil.uploadPic(file.getOriginalFilename(), QStorageConfig.goodsPicPath, file.getBytes());
		picInfoManager.savePicInfo(picInfo);
		PicInfoBean picInfoBean = new PicInfoBean();
		BeanUtils.copyProperties(picInfoBean, picInfo);
		return ResultBean.success(picInfoBean);
	}

	@RequestMapping(value = "testSendSms", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean testRedis(String mobile) throws Exception {
		Assert.assertTrue(StringUtil.isPhone(mobile), "电话号码格式错误");
		String msgId = JiGuangSmsUtil.sendCodeSms(mobile, SmsTemplate.Template_ID_Code);
		Assert.assertNotEmpty(msgId, "发送验证码失败");
		return ResultBean.success(msgId);
	}

	@RequestMapping(value = "testVerifySms", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean testVerifySms(String msgId, String code) throws Exception {
		boolean isSuccess = JiGuangSmsUtil.checkCode(msgId, code);
		Assert.assertTrue(isSuccess, "短信验证失败");
		return ResultBean.success(null);
	}

}
