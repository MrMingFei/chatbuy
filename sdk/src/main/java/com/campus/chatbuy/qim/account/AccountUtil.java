package com.campus.chatbuy.qim.account;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.campus.chatbuy.config.QIMConfig;
import com.campus.chatbuy.id.generator.IdGeneratorFactory;
import com.campus.chatbuy.util.HttpRequestUtil;
import com.campus.chatbuy.util.StringUtil;
import org.apache.log4j.Logger;
import org.guzz.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinku on 2017/8/11.
 */
public class AccountUtil {

	private static final Logger log = Logger.getLogger(AccountUtil.class);

	public static void accountImport(String adminSign, String userId) throws Exception {
		String url = "https://console.tim.qq.com/v4/im_open_login_svc/account_import?usersig=" + adminSign
				+ "&identifier=admin&sdkappid=" + QIMConfig.Sdk_App_Id + "&random=" + IdGeneratorFactory.generateId()
				+ "&contenttype=json";
		JSONObject paramJson = new JSONObject();
		paramJson.put("Identifier", userId);

		log.info("accountImport request is [" + paramJson.toJSONString() + "]");
		String result = HttpRequestUtil.httpPostJson(url, paramJson.toJSONString(), null);
		log.info("accountImport result is [" + result + "]");
		JSONObject jsonObject = JSON.parseObject(result);
		String status = jsonObject.getString("ActionStatus");
		Assert.assertEquals(status, "OK", "accountImport failed");
	}

	public static void addBlackList(String adminSign, String userId, String blackUserId) throws Exception {
		String url = "https://console.tim.qq.com/v4/sns/black_list_add?usersig=" + adminSign
				+ "&identifier=admin&sdkappid=" + QIMConfig.Sdk_App_Id + "&random=" + IdGeneratorFactory.generateId()
				+ "&contenttype=json";
		JSONObject paramJson = new JSONObject();
		paramJson.put("From_Account", userId);
		JSONArray blackArray = new JSONArray();
		blackArray.add(blackUserId);

		paramJson.put("To_Account", blackArray);

		log.info("addBlackList request is [" + paramJson.toJSONString() + "]");
		String result = HttpRequestUtil.httpPostJson(url, paramJson.toJSONString(), null);
		log.info("addBlackList result is [" + result + "]");
		JSONObject jsonObject = JSON.parseObject(result);
		String status = jsonObject.getString("ActionStatus");
		Assert.assertEquals(status, "OK", "addBlackList failed");

		JSONArray jsonArray = jsonObject.getJSONArray("ResultItem");
		int returnCode = jsonArray.getJSONObject(0).getIntValue("ResultCode");
		if (returnCode == 31307) {
			// 已添加
			return;
		}
		Assert.assertEquals(returnCode, 0, "addBlackList failed");
	}

	public static void deleteBlackList(String adminSign, String userId, String blackUserId) throws Exception {
		String url = "https://console.tim.qq.com/v4/sns/black_list_delete?usersig=" + adminSign
				+ "&identifier=admin&sdkappid=" + QIMConfig.Sdk_App_Id + "&random=" + IdGeneratorFactory.generateId()
				+ "&contenttype=json";
		JSONObject paramJson = new JSONObject();
		paramJson.put("From_Account", userId);
		JSONArray blackArray = new JSONArray();
		blackArray.add(blackUserId);

		paramJson.put("To_Account", blackArray);

		log.info("deleteBlackList request is [" + paramJson.toJSONString() + "]");
		String result = HttpRequestUtil.httpPostJson(url, paramJson.toJSONString(), null);
		log.info("deleteBlackList result is [" + result + "]");
		JSONObject jsonObject = JSON.parseObject(result);
		String status = jsonObject.getString("ActionStatus");
		Assert.assertEquals(status, "OK", "deleteBlackList failed");

		JSONArray jsonArray = jsonObject.getJSONArray("ResultItem");
		int returnCode = jsonArray.getJSONObject(0).getIntValue("ResultCode");
		Assert.assertEquals(returnCode, 0, "deleteBlackList failed");
	}

	public static boolean checkBlackList(String adminSign, String userId, String toUserId) throws Exception {
		String url = "https://console.tim.qq.com/v4/sns/black_list_check?usersig=" + adminSign
				+ "&identifier=admin&sdkappid=" + QIMConfig.Sdk_App_Id + "&random=" + IdGeneratorFactory.generateId()
				+ "&contenttype=json";

		JSONObject paramJson = new JSONObject();
		paramJson.put("From_Account", userId);
		JSONArray blackArray = new JSONArray();
		blackArray.add(toUserId);
		paramJson.put("To_Account", blackArray);
		paramJson.put("CheckType", "BlackCheckResult_Type_Both");

		log.info("checkBlackList request is [" + paramJson.toJSONString() + "]");
		String result = HttpRequestUtil.httpPostJson(url, paramJson.toJSONString(), null);
		log.info("checkBlackList result is [" + result + "]");
		JSONObject jsonObject = JSON.parseObject(result);
		String status = jsonObject.getString("ActionStatus");
		Assert.assertEquals(status, "OK", "checkBlackList failed");

		JSONArray jsonArray = jsonObject.getJSONArray("BlackListCheckItem");
		String relation = jsonArray.getJSONObject(0).getString("Relation");
		Assert.assertNotEmpty(relation, "checkBlackList failed");
		if ("BlackCheckResult_Type_AWithB".equals(relation)) {
			return true;
		}
		if ("BlackCheckResult_Type_BothWay".equals(relation)) {
			return true;
		}
		return false;
	}



	public static void main(String[] args) throws Exception {
		String sign = "eJxlj11LwzAARd-7K0qfxeWrdBV8KFsqgpO62bH5EmKTjpAtTdusm4j-3VkHBryv51wu9zMIwzB6fVrd"
				+ "8qpqjsYx92FlFN6FEYhu-qC1SjDuGO7EPyjPVnWS8drJboQwjmMEgO8oIY1TtboaXByU8XAvNBs3fvvkUsZTQL"
				+ "CvqN0IF7ScPWZHkG-W7QC36JxQUTx31eRFT3UOdv2yTikZKKjeHtZz22SKZqbtYVLqdF8uVwXi5nTSepLThS3"
				+ "f5-tZPrSw2DqLUiiae2-SqYO8HgKIoIRg4tFBdr1qzCggAGOIMPhJFHwF3-q6XOo_";
//		accountImport(sign, "admin");
//		accountImport(sign, "test001");
//		addBlackList(sign, "admin", "test001");
		checkBlackList(sign, "admin", "test001");
	}
}
