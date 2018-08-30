package com.campus.chatbuy.storage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.campus.chatbuy.bean.PicInfoBean;
import com.campus.chatbuy.config.QStorageConfig;
import com.campus.chatbuy.id.generator.IdGeneratorFactory;
import com.campus.chatbuy.util.ConfigUtil;
import com.campus.chatbuy.util.HttpRequestUtil;
import com.campus.chatbuy.util.StringUtil;
import org.apache.log4j.Logger;
import org.guzz.util.Assert;

import java.util.HashMap;

/**
 * Created by jinku on 2017/7/17.
 * <p/>
 * 腾讯云优图处理工具类
 */
public class QPictureUtil {

    private static final Logger log = Logger.getLogger(QPictureUtil.class);

    private static final String Pic_Suffix = "?imageView2/0/w/%s/h/%s";

    //	/**
    //	 * 上传图片 暂不可用
    //	 *
    //	 * @param picId
    //	 * @param picData
    //	 * @return
    //	 */
    //	public static UploadResult uploadPic(String picId, byte[] picData) {
    //		log.info("QPictureUtil uploadPic picId is [" + picId + "]");
    //		PicCloud pc = new PicCloud(QStorageConfig.appId, QStorageConfig.secretId, QStorageConfig.secretKey,
    //				QStorageConfig.bucketName);
    //		long expired = System.currentTimeMillis() / 1000 + 3600;
    //		String sign = pc.getSign(expired);
    //
    //		ByteArrayInputStream inputStream = new ByteArrayInputStream(picData);
    //		UploadResult result = pc.upload(inputStream, picId);
    //		if (pc.getErrno() == 0) {
    //			log.info("QPictureUtil uploadPic result; url is [" + result.url + "] downloadUrl is [" + result
    // .downloadUrl
    //					+ "] width is [" + result.width + "] height is [" + result.height + "]");
    //		} else {
    //			log.error("QPictureUtil uploadPic error; errorNo is [" + pc.getErrno() + "] errorMsg is [" + pc
    // .getErrMsg()
    //					+ "]");
    //		}
    //
    //		return result;
    //	}

    /**
     * 上传图片
     *
     * @param fileName
     * @param pathPrefix
     * @param data
     * @return
     * @throws Exception
     */
    public static PicInfoBean uploadPic(String fileName, String pathPrefix, byte[] data) throws Exception {
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());
        long picId = IdGeneratorFactory.generateId();
        String filePath = pathPrefix + picId + fileSuffix;

        String fileUrl = QStorageUtil.uploadFile(filePath, data);
        PicInfoBean picInfo = queryPicInfo(fileUrl);
        picInfo.setPicId(String.valueOf(picId));
        return picInfo;
    }

    /**
     * 查询图片信息
     *
     * @param picUrl
     * @return
     * @throws Exception
     */
    public static PicInfoBean queryPicInfo(String picUrl) throws Exception {
        if("prod".equals(ConfigUtil.getValue("run.env.mode"))) {
            picUrl = picUrl.replace("http:", "https:");//替换成https
        }
        log.info("QPictureUtil queryPicInfo picUrl is [" + picUrl + "]");
        picUrl = picUrl.replace("cos", "pic");//万象有图处理
        String url = picUrl + "?imageInfo";
        log.info("QPictureUtil queryPicInfo request url [" + url + "]");
        String result = HttpRequestUtil.getUrl(url, new HashMap<String, String>());
        log.info("QPictureUtil queryPicInfo result is [" + result + "]");
        JSONObject obj = JSON.parseObject(result);

        int code = obj.getIntValue("code");
        Assert.assertEquals(code, 0, obj.getString("error"));

        int width = obj.getIntValue("width");
        int height = obj.getIntValue("height");
        PicInfoBean picInfo = new PicInfoBean();
        picInfo.setWidth(width);
        picInfo.setHeight(height);
        picInfo.setOriginUrl(picUrl);
        picInfo.setBigUrl(picUrl + String.format(Pic_Suffix, QStorageConfig.Big_Pic_Width, QStorageConfig.Big_Pic_Height));
        picInfo.setSmallUrl(picUrl + String.format(Pic_Suffix, QStorageConfig.Small_Pic_Width, QStorageConfig.Small_Pic_Height));

        log.info("QPictureUtil queryPicInfo picInfo is [" + picInfo + "]");
        return picInfo;
    }

}
