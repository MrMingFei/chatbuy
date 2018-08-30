package com.campus.chatbuy.config;

import com.campus.chatbuy.util.ConfigUtil;

/**
 * Created by jinku on 2017/7/7.
 *
 * 腾讯云对象存储配置类
 */
public class QStorageConfig {

	public final static int  appId = ConfigUtil.getIntValue("QStorage_AppID");
	public final static  String secretId = ConfigUtil.getValue("QStorage_SecretId");
	public final static  String secretKey = ConfigUtil.getValue("QStorage_SecretKey");
	public final static  String bucketName = ConfigUtil.getValue("QStorage_BucketName");

	public final static int Sign_Expire_Seconds = 10 * 60;

	public final static String avatarPath = "/picture/avatar/";
	public final static String goodsPicPath = "/picture/goodsPic/";

	public final static int Big_Pic_Width = 1200;
	public final static int Big_Pic_Height = 800;

	public final static int Small_Pic_Width = 400;
	public final static int Small_Pic_Height = 400;
}
