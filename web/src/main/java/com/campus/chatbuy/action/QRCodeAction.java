package com.campus.chatbuy.action;

import com.campus.chatbuy.bean.ResultBean;
import com.campus.chatbuy.bean.UserInfoBean;
import com.campus.chatbuy.constans.ConfigConstants;
import com.campus.chatbuy.constans.RedisKey;
import com.campus.chatbuy.constans.RouteKey;
import com.campus.chatbuy.enums.PlatformEnum;
import com.campus.chatbuy.exception.BusinessException;
import com.campus.chatbuy.manager.IGoodsInfoManager;
import com.campus.chatbuy.manager.IUserManager;
import com.campus.chatbuy.model.GoodsInfo;
import com.campus.chatbuy.service.RedisService;
import com.campus.chatbuy.util.*;
import org.apache.log4j.Logger;
import org.guzz.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinku on 2018/1/6.
 */
@Controller
@RequestMapping(value = "/chatbuy/web/qrcode")
public class QRCodeAction {

    private static final Logger log = Logger.getLogger(QRCodeAction.class);

    @Autowired
    private IGoodsInfoManager goodsManager;

    @Autowired
    private RedisService redisService;

    @Autowired
    private IUserManager userManager;

    /**
     * 生成商品信息二维码
     *
     * @param goodsId
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/goodsInfo", method = RequestMethod.GET)
    public void picInfo(String goodsId, HttpServletResponse response) throws Exception {
        Assert.assertNotEmpty(goodsId, "商品Id不能为空");
        long goodsIdL = 0L;
        try {
            goodsIdL = Long.parseLong(goodsId);
        } catch (Exception e) {
            throw new BusinessException(1, "商品Id不合法", e);
        }
        GoodsInfo goodsInfo = goodsManager.queryById(goodsIdL);
        Assert.assertNotNull(goodsInfo, "该商品不存在");
        String content = String.format(RouteKey.Goods_Detail, goodsId);
        try {
            byte[] codeData = QRCodeUtil.genQRCode(content);

            DownloadUtil.printData(response, "image/png", codeData);
        } catch (Exception e) {
            throw new BusinessException(1, "生成二维码失败", e);
        }
    }

    /**
     * 生成登录二维码
     *
     * @param platform
     * @param key
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/genLoginCode", method = RequestMethod.GET)
    public void genLoginCode(String platform, String key, HttpServletResponse response) throws Exception {
        Assert.assertNotEmpty(key, "请求参数非法");
        Assert.assertNotEmpty(platform, "请求参数非法");

        String codeLoginRedisKey = RedisKey.Code_Login_Key + platform + key;
        redisService.set(codeLoginRedisKey, 5 * 60, "0");
        String content = String.format(RouteKey.Code_Login, platform, key);
        try {
            byte[] codeData = QRCodeUtil.genQRCode(content);
            DownloadUtil.printData(response, "image/png", codeData);
        } catch (Exception e) {
            throw new BusinessException(1, "生成二维码失败", e);
        }
    }

    /**
     * app 扫一扫
     *
     * @param loginPlatform
     * @param key
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/sao1sao", method = RequestMethod.POST)
    public ResultBean sao1sao(String loginPlatform, String key) throws Exception {

        Assert.assertNotEmpty(key, "请求参数非法");
        Assert.assertNotEmpty(loginPlatform, "请求参数非法");

        long userId = ThreadLocalUtil.getUserId();
        String codeLoginRedisKey = RedisKey.Code_Login_Key + loginPlatform + key;

        // 检查二维码是否过期
        String value = redisService.get(codeLoginRedisKey);
        if (StringUtil.isEmpty(value)) {
            return ResultBean.failure("二维码已过期");
        }
        // 设置用户信息
        redisService.set(codeLoginRedisKey, 5 * 60, String.valueOf(userId));
        return ResultBean.success(null);
    }

    /**
     * 查询扫码用户信息
     *
     * @param platform
     * @param key
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/codeUserInfo", method = RequestMethod.GET)
    public ResultBean isSao1Sao(String platform, String key) throws Exception {

        Assert.assertNotEmpty(key, "请求参数非法");
        Assert.assertNotEmpty(platform, "请求参数非法");

        String codeLoginRedisKey = RedisKey.Code_Login_Key + platform + key;
        String userId = redisService.get(codeLoginRedisKey);
        if (StringUtil.isEmpty(userId) || "0".equals(userId)) {
            return ResultBean.success(null);
        }
        log.info("codeUserInfo userId " + userId);
        UserInfoBean userInfoBean = userManager.queryById(Long.parseLong(userId));
        Assert.assertNotNull(userInfoBean, "用户不存在");
        Map<String, String> userInfoMap = new HashMap<>();
        userInfoMap.put("userId", userInfoBean.getUserId());
        userInfoMap.put("userName", userInfoBean.getUserName());
        String userAvatar =  null;
        if (userInfoBean.getAvatarPicInfo() != null) {
            userAvatar = userInfoBean.getAvatarPicInfo().getBigUrl();
        }
        userInfoMap.put("userAvatar", userAvatar);
        return ResultBean.success(userInfoMap);
    }

    /**
     * 二维码登录:app调用
     *
     * @param loginPlatform
     * @param key
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/codeLogin", method = RequestMethod.POST)
    public ResultBean codeLogin(String loginPlatform, String key) throws Exception {
        Assert.assertNotEmpty(key, "请求参数非法");
        Assert.assertNotEmpty(loginPlatform, "请求参数非法");

        long currUserId = ThreadLocalUtil.getUserId();

        String codeLoginRedisKey = RedisKey.Code_Login_Key + loginPlatform + key;
        String userId = redisService.get(codeLoginRedisKey);
        if (StringUtil.isEmpty(userId)) {
            return ResultBean.failure("登录确认已过期");
        }
        Assert.assertEquals(String.valueOf(currUserId), userId, "登录确认用户不一致");

        String loginResultKey = RedisKey.Code_Login_Result_Key + key + userId;
        // 设置已验证登录
        redisService.set(loginResultKey, 5 * 60, String.valueOf(userId));
        return ResultBean.success(null);
    }


    /**
     * 检查是否登录验证通过
     *
     * @param key
     * @param userId
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/isCodeLogin", method = RequestMethod.GET)
    public ResultBean isCodeLogin(String key, String userId,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception {

        Assert.assertNotEmpty(userId, "请求参数非法");
        Assert.assertNotEmpty(key, "请求参数非法");

        String codeLoginResultKey = RedisKey.Code_Login_Result_Key + key + userId;
        String value = redisService.get(codeLoginResultKey);
        if (StringUtil.isNotEmpty(value)) {
            long userIdL = Long.parseLong(userId);
            String token = TokenUtil.genToken(request, userIdL);
            CookieUtil.addCookie(response, CookieUtil.Access_Token_Name, token);
            redisService.set(RedisKey.getToken2UserIdKey(token, PlatformEnum.PC.remark),
                    ConfigConstants.PC_Token_Valid_Time, String.valueOf(userId));

            // 查询用户信息
            UserInfoBean userInfo = userManager.queryMySelf(userIdL);
            return ResultBean.success(userInfo);
        }
        return ResultBean.success(null);
    }
}
