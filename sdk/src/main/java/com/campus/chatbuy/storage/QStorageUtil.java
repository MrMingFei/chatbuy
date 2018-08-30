package com.campus.chatbuy.storage;

import com.alibaba.fastjson.JSONObject;
import com.campus.chatbuy.config.QStorageConfig;
import com.campus.chatbuy.util.DateUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.request.UploadFileRequest;
import com.qcloud.cos.sign.Credentials;
import com.qcloud.cos.sign.Sign;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * Created by jinku on 2017/7/7.
 * <p/>
 * 腾讯云对象存储工具类
 */
public class QStorageUtil {

	private static final Logger log = Logger.getLogger(QStorageUtil.class);

	/**
	 * 上传文件
	 *
	 * @param cloudPath
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String uploadFile(String cloudPath, byte[] data) throws Exception {
		log.info("QStorageUtil uploadFile cloudPath [" + cloudPath + "]");

		COSClient cosClient = getCOSClient();
		UploadFileRequest uploadFileRequest = new UploadFileRequest(QStorageConfig.bucketName, cloudPath, data);
		String uploadFileRet = cosClient.uploadFile(uploadFileRequest);
		log.info("QStorageUtil uploadFile result [" + uploadFileRet + "]");

		String fileUrl = null;
		JSONObject jsonObj = JSONObject.parseObject(uploadFileRet);
		int code = jsonObj.getIntValue("code");
		if (code == 0) {
			JSONObject dataObj = jsonObj.getJSONObject("data");
			fileUrl = dataObj.getString("source_url");
		}
		return fileUrl;
	}

	/**
	 * 获取指定路径的签名(用于上传)
	 * 过期时间为10分钟
	 *
	 * @param cosPath
	 * @return
	 * @throws Exception
	 */
	public static String getPeriodEffectiveSign(String cosPath) throws Exception {
		log.info("getPeriodEffectiveSign cosPath is [" + cosPath + "]");
		Credentials cred = new Credentials(QStorageConfig.appId, QStorageConfig.secretId, QStorageConfig.secretKey);

		Date expireDate = DateUtils.Add(new Date(), 0, 0, 30, 0);
		long expireTime = expireDate.getTime() / 1000;
		log.info("getPeriodEffectiveSign expireTime is [" + expireTime + "]");
		String sign = Sign.getPeriodEffectiveSign(QStorageConfig.bucketName, cosPath, cred, expireTime);
		log.info("getPeriodEffectiveSign sign is [" + sign + "]");
		return sign;
	}

	private static COSClient getCOSClient() {
		// 初始化秘钥信息
		Credentials cred = new Credentials(QStorageConfig.appId, QStorageConfig.secretId, QStorageConfig.secretKey);

		// 初始化客户端配置
		ClientConfig clientConfig = new ClientConfig();
		// 设置bucket所在的区域，比如华南园区：gz； 华北园区：tj；华东园区：sh ；
		clientConfig.setRegion("tj");

		// 初始化cosClient
		COSClient cosClient = new COSClient(clientConfig, cred);
		return cosClient;
	}

}
