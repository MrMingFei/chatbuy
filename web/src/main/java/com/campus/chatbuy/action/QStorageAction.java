package com.campus.chatbuy.action;

import com.campus.chatbuy.bean.ResultBean;
import com.campus.chatbuy.config.QStorageConfig;
import com.campus.chatbuy.id.generator.IdGeneratorFactory;
import com.campus.chatbuy.model.GoodsTag;
import com.campus.chatbuy.storage.QStorageUtil;
import com.campus.chatbuy.util.StringUtil;
import org.guzz.util.Assert;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.SavepointManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jinku on 2017/7/21.
 */

@Controller
@RequestMapping(value = "/chatbuy/web/qstorage")
public class QStorageAction {

	private final static int Type_Avatar = 1;
	private final static int Type_Goods = 2;

	@RequestMapping(value = "getPeriodSign", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean getPeriodSign(String key, int type) throws Exception {
		Assert.assertTrue(type == Type_Avatar || type == Type_Goods, "请求参数非法");
		String cosPath = QStorageConfig.avatarPath;
		if(type == Type_Goods) {
			cosPath = QStorageConfig.goodsPicPath;
		}
		String sign = QStorageUtil.getPeriodEffectiveSign(cosPath);
		long fileId = IdGeneratorFactory.generateId();
		Map<String, String> resultMap = new HashMap<>();
		resultMap.put("sign", sign);
		resultMap.put("key", key);
		resultMap.put("fileId", String.valueOf(fileId));
		return ResultBean.success(resultMap);
	}
}
