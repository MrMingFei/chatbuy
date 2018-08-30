package com.campus.chatbuy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.campus.chatbuy.enums.MsgShowType;
import com.campus.chatbuy.util.Base64Util;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest {

    public static void main(String[] args) throws Exception {
        String testStr = "eyJzZXNzaW9uSWQiOiIxMzk4NzM4ODQ0Nzk5MjYyNzIiLCJtZXNzYWdlSWQiOiIxMzk4NzYyMDQ0ODc4ODA3MDQiL" +
                "CJmcm9tVXNlcklkIjoiMTEzNzQwMjA5MTg2NjcyNjQwIiwidG9Vc2VySWQiOiIxMTMyNDE2MjQ0NTIwMDk5ODQiLCJtc2dUeXB" +
                "lIjoxLCJidXNpbmVzc1R5cGUiOjAsIm1zZ0NvbnRlbnQiOnsic3R5bGVUeXBlIjowLCJtc2dDb250ZW50Ijoi4pi6Iiwic2hvd" +
                "1R5cGUiOjAsImV4dCI6e319fQ==";

        byte[] msgByteArray = Base64Util.decode(testStr);
        String msgStr = new String(msgByteArray, "utf8");
        JSONObject jsonObject = JSON.parseObject(msgStr);
        jsonObject.put("msgType", MsgShowType.Only_Receiver.key);
        String changedJson = jsonObject.toJSONString();
        String changedMsg = Base64Util.encode(changedJson.getBytes());
        System.out.println(changedMsg);
    }

}
