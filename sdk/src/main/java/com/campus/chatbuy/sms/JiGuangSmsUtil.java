package com.campus.chatbuy.sms;

import com.alibaba.fastjson.JSONObject;
import com.campus.chatbuy.config.JiGuangConfig;
import com.campus.chatbuy.util.Base64Util;
import com.campus.chatbuy.util.ConfigUtil;
import com.campus.chatbuy.util.HttpRequestUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinku on 2017/7/6.
 * <p/>
 * 极光短信发送工具类
 * https://docs.jiguang.cn/jsms/server/rest_api_jsms
 */
public class JiGuangSmsUtil {

	private static final Logger log = Logger.getLogger(JiGuangSmsUtil.class);

	/**
	 * 发送短信
	 *
	 * @param mobile
	 * @param templateId
	 * @return
	 */
	public static String sendCodeSms(String mobile, String templateId) throws Exception {
		//只有online环境才发短信
		if(!"prod".equals(ConfigUtil.getValue("run.env.mode"))) {
			return "1234567890";
		}
		String url = JiGuangConfig.Send_Code_Sms_Url;
		JSONObject object = new JSONObject();
		object.put("mobile", mobile);
		object.put("temp_id", templateId);
		String requestJson = object.toJSONString();
		Map<String, String> headers = new HashMap();

		log.info("JiGuangSmsUtil sendSms url is [" + url + "] requestJson is [" + requestJson + "]");
		headers.put("Authorization", "Basic " + getAuthStr());

		String result = HttpRequestUtil.httpPostJson(JiGuangConfig.Send_Code_Sms_Url, requestJson, headers);
		log.info("JiGuangSmsUtil sendSms result is [" + result + "]");
		JSONObject jsonObj = JSONObject.parseObject(result);
		String msgId = jsonObj.getString("msg_id");
		return msgId;
	}

	/**
	 * 检查验证码是否通过
	 *
	 * @param msgId
	 * @param code
	 * @return
	 */
	public static boolean checkCode(String msgId, String code) {
		if (!"prod".equals(ConfigUtil.getValue("run.env.mode")) && "123456".equals(code)) {
			return true;
		}
		String url = String.format(JiGuangConfig.Check_Code_Url, msgId);
		JSONObject object = new JSONObject();
		object.put("code", code);
		String requestJson = object.toJSONString();
		Map<String, String> headers = new HashMap();

		log.info("JiGuangSmsUtil checkCode url is [" + url + "] requestJson is [" + requestJson + "]");
		try {
			headers.put("Authorization", "Basic " + getAuthStr());

			String result = HttpRequestUtil.httpPostJson(url, requestJson, headers);
			log.info("JiGuangSmsUtil checkCode result is [" + result + "]");
			JSONObject jsonObj = JSONObject.parseObject(result);
			boolean isValid = jsonObj.getBooleanValue("is_valid");
			return isValid;
		} catch (Exception e) {
			log.error("JiGuangSmsUtil checkCode exception", e);
		}
		return false;
	}

	/**
	 * 发送参数短信
	 *
	 * @param mobile
	 * @param templateId
	 * @param smsParams
	 * @return
	 */
	public static String sendParamSms(String mobile, String templateId, Map<String, String> smsParams) throws Exception {
		//只有online环境才发短信
		if(!"prod".equals(ConfigUtil.getValue("run.env.mode"))) {
			return "1234567890";
		}
		String url = JiGuangConfig.Send_Param_Sms_Url;
		JSONObject object = new JSONObject();
		object.put("mobile", mobile);
		object.put("temp_id", templateId);
		if (smsParams != null && smsParams.size() > 0) {
			object.put("temp_para", smsParams.toString());
		}
		String requestJson = object.toJSONString();
		Map<String, String> headers = new HashMap();
		log.info("JiGuangSmsUtil sendParamSms url is [" + url + "] requestJson is [" + requestJson + "]");

		headers.put("Authorization", "Basic " + getAuthStr());
		String result = HttpRequestUtil.httpPostJson(url, requestJson, headers);
		log.info("JiGuangSmsUtil sendParamSms result is [" + result + "]");
		JSONObject jsonObj = JSONObject.parseObject(result);
		String msgId = jsonObj.getString("msg_id");
		return msgId;
	}

	private static String getAuthStr() throws Exception {
		String originStr = JiGuangConfig.App_Key + ":" + JiGuangConfig.Master_Secret;
		String authStr = Base64Util.encode(originStr.getBytes("UTF-8"));
		return authStr;
	}

	public static void main(String[] args) throws Exception {
//		String mobile = "18713506432";
//		String msgId = sendCodeSms(mobile, SmsTemplate.Template_ID_Code);
//		System.out.println(msgId);
//		checkCode("290348263035", "487705");
		Map<String, String> paramsMap = new HashMap<>();
		String msgId = sendParamSms("18713506432", SmsTemplate.Template_ID_Online_Notice, paramsMap);
		System.out.println(msgId);
	}
}
