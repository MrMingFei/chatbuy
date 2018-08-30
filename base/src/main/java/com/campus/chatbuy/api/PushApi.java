package com.campus.chatbuy.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.campus.chatbuy.util.ConfigUtil;
import com.campus.chatbuy.util.HttpRequestUtil;
import com.campus.chatbuy.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinku on 2017/11/22.
 */
public class PushApi {

    private static final Logger log = Logger.getLogger(PushApi.class);

    private static final String Pusher_Host = ConfigUtil.getValue("pusher_host");

    public static boolean pushMessage(String userId, String msgId, String content) {
        String url = Pusher_Host + "/inner/push/message";
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("udid", userId);
        paramsMap.put("mid", msgId);
        paramsMap.put("content", content);
        try {
            String result = HttpRequestUtil.postUrl(url, paramsMap);
            log.info("PushApi pushMessage result [" + result + "]");
            if(StringUtil.isEmpty(result)) {
                return false;
            }
            JSONObject jsonObject = JSON.parseObject(result);
            int returnCode = jsonObject.getIntValue("returnCode");
            if (returnCode == 0) {
                return true;
            }
        } catch (Exception e) {
            log.error("PushApi pushMessage exception", e);
            return false;
        }
        return false;
    }
}
