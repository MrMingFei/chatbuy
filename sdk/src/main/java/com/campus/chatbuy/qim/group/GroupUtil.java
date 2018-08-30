package com.campus.chatbuy.qim.group;

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
 * 腾讯云通信群组管理功能
 */
public class GroupUtil {

    private static final Logger log = Logger.getLogger(GroupUtil.class);

    private final static int Forbid_Time_Second = 365 * 24 * 60 * 60;// 1年

    public static String createGroup(String adminSign, String groupName, List<String> memberList) throws Exception {
        String url = "https://console.tim.qq.com/v4/group_open_http_svc/create_group?usersig=" + adminSign
                + "&identifier=admin&sdkappid=" + QIMConfig.Sdk_App_Id + "&random=" + IdGeneratorFactory.generateId()
                + "&contenttype=json";

        JSONObject paramJson = new JSONObject();
        paramJson.put("Name", groupName);
        paramJson.put("Type", "Public");

        JSONArray jsonArray = new JSONArray();
        for (String member : memberList) {
            JSONObject memberJson = new JSONObject();
            memberJson.put("Member_Account", member);
            jsonArray.add(memberJson);
        }
        paramJson.put("MemberList", jsonArray);
        log.info("createGroup request is [" + paramJson.toJSONString() + "]");
        String result = HttpRequestUtil.httpPostJson(url, paramJson.toJSONString(), null);
        log.info("createGroup result is [" + result + "]");
        JSONObject jsonObject = JSON.parseObject(result);
        String status = jsonObject.getString("ActionStatus");
        Assert.assertEquals(status, "OK", "createGroup failed");
        String groupId = jsonObject.getString("GroupId");
        Assert.assertNotEmpty(groupId, "createGroup failed");
        return groupId;
    }

    public static void forbidOrCancelForbid(String adminSign, String groupId, String userId, boolean forbid) throws
            Exception {
        String url = "https://console.tim.qq.com/v4/group_open_http_svc/forbid_send_msg?usersig=" + adminSign
                + "&identifier=admin&sdkappid=" + QIMConfig.Sdk_App_Id + "&random=" + IdGeneratorFactory.generateId()
                + "&contenttype=json";

        JSONObject paramJson = new JSONObject();
        paramJson.put("GroupId", groupId);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(userId);
        paramJson.put("Members_Account", jsonArray);
        if (forbid) {
            paramJson.put("ShutUpTime", Forbid_Time_Second);//禁言时间，单位为秒
        } else {
            paramJson.put("ShutUpTime", 0);//禁言时间，单位为秒
        }

        log.info("forbidOrUnforbid request is [" + paramJson.toJSONString() + "]");
        String result = HttpRequestUtil.httpPostJson(url, paramJson.toJSONString(), null);
        log.info("forbidOrUnforbid result is [" + result + "]");
        JSONObject jsonObject = JSON.parseObject(result);
        String status = jsonObject.getString("ActionStatus");
        Assert.assertEquals(status, "OK", "forbidOrUnforbid failed");
    }

    public static List<String> getForbidList(String adminSign, String groupId) throws Exception {
        String url = "https://console.tim.qq.com/v4/group_open_http_svc/get_group_shutted_uin?usersig=" + adminSign
                + "&identifier=admin&sdkappid=" + QIMConfig.Sdk_App_Id + "&random=" + IdGeneratorFactory.generateId()
                + "&contenttype=json";

        JSONObject paramJson = new JSONObject();
        paramJson.put("GroupId", groupId);

        log.info("getForbidList request is [" + paramJson.toJSONString() + "]");
        String result = HttpRequestUtil.httpPostJson(url, paramJson.toJSONString(), null);
        log.info("getForbidList result is [" + result + "]");
        JSONObject jsonObject = JSON.parseObject(result);
        String status = jsonObject.getString("ActionStatus");
        Assert.assertEquals(status, "OK", "getForbidList failed");

        JSONArray jsonArray = jsonObject.getJSONArray("ShuttedUinList");
        List<String> memberList = new ArrayList<>();
        if (jsonArray.size() == 0) {
            return memberList;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            memberList.add(jsonArray.getJSONObject(i).getString("Member_Account"));
        }
        return memberList;
    }

    public static void main(String[] args) throws Exception {
        String sign = "eJxlj11LwzAARd-7K0qfxeWrdBV8KFsqgpO62bH5EmKTjpAtTdusm4j-3VkHBryv51wu9zMIwzB6fVrd"
                + "8qpqjsYx92FlFN6FEYhu-qC1SjDuGO7EPyjPVnWS8drJboQwjmMEgO8oIY1TtboaXByU8XAvNBs3fvvkUsZTQL"
                + "CvqN0IF7ScPWZHkG-W7QC36JxQUTx31eRFT3UOdv2yTikZKKjeHtZz22SKZqbtYVLqdF8uVwXi5nTSepLThS3"
                + "f5-tZPrSw2DqLUiiae2-SqYO8HgKIoIRg4tFBdr1qzCggAGOIMPhJFHwF3-q6XOo_";
        String groupName = "test001";
        List<String> memberList = new ArrayList<>();
        memberList.add("admin");
        memberList.add("test001");
        memberList.add("test002");
        String groupId = createGroup(sign, groupName, memberList);
        System.out.println(groupId);

        getForbidList(sign, groupId);
//		forbidOrCancelForbid(sign, groupId, "test001", false);
        forbidOrCancelForbid(sign, groupId, "test001", true);
        getForbidList(sign, groupId);
        forbidOrCancelForbid(sign, groupId, "test001", false);
        getForbidList(sign, groupId);
    }
}
