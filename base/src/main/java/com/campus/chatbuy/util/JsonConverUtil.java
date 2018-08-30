package com.campus.chatbuy.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;

public class JsonConverUtil {

    public static String bean2Json(Object bean) {
        return JSON.toJSONString(bean, SerializerFeature.WriteMapNullValue);
    }

    public static <T> T json2Bean(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public static <T> List<T> json2List(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }

}
