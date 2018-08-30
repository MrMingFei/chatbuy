package com.campus.chatbuy.action;

import com.campus.chatbuy.bean.RegisterBean;
import com.campus.chatbuy.bean.ResultBean;
import com.campus.chatbuy.bean.UserInfoBean;
import com.campus.chatbuy.config.QIMConfig;
import com.campus.chatbuy.constans.ConfigConstants;
import com.campus.chatbuy.constans.RedisKey;
import com.campus.chatbuy.enums.PlatformEnum;
import com.campus.chatbuy.id.generator.IdGeneratorFactory;
import com.campus.chatbuy.manager.IUserManager;
import com.campus.chatbuy.manager.IUserSecurityManager;
import com.campus.chatbuy.manager.IUserSignManager;
import com.campus.chatbuy.manager.impl.BlackListManager;
import com.campus.chatbuy.model.UserInfo;
import com.campus.chatbuy.model.UserSecurity;
import com.campus.chatbuy.model.UserSign;
import com.campus.chatbuy.qim.account.AccountUtil;
import com.campus.chatbuy.qim.group.GroupUtil;
import com.campus.chatbuy.service.RedisService;
import com.campus.chatbuy.sms.JiGuangSmsUtil;
import com.campus.chatbuy.sms.SmsTemplate;
import com.campus.chatbuy.util.*;
import com.campus.chatbuy.util.encrypt.BCrypt;
import org.apache.log4j.Logger;
import org.guzz.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jinku on 2017/5/6.
 */
@Controller
@RequestMapping(value = "/chatbuy/web/user")
public class UserAction {

    @Autowired
    private IUserManager userManager;

    @Autowired
    private IUserSecurityManager userSecurityManager;

    @Autowired
    private RedisService redisService;

    @Autowired
    private BlackListManager blackListManager;

    private static final Logger log = Logger.getLogger(UserAction.class);

    @RequestMapping(value = "queryUserById", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean queryUserById(Long userId) throws Exception {
        Assert.assertNotNull(userId, "用户id不能为空");
        UserInfoBean userInfo = userManager.queryById(userId);
        return ResultBean.success(userInfo);
    }

    @RequestMapping(value = "queryCurUser", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean queryCurUser() throws Exception {
        UserInfoBean userInfo = userManager.queryCurr();
        return ResultBean.success(userInfo);
    }

    /**
     * 检查用户是否注册
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "isExist", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean isExist(String mobile) throws Exception {
        Assert.assertNotEmpty(mobile, "手机号不能为空");

        UserInfo userInfo = userManager.queryByMobile(mobile);
        if (userInfo != null) {
            return ResultBean.success(1);
        } else {
            return ResultBean.success(0);
        }
    }

    /*
     * 登录
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean login(String platform, String mobile, String password,
                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        Assert.assertNotEmpty(mobile, "手机号不能为空");
        Assert.assertNotEmpty(password, "密码不能为空");

        UserSecurity userSecurity = userSecurityManager.queryByPhone(mobile);
        Assert.assertNotNull(userSecurity, "用户名或密码不正确");

        Assert.assertTrue(BCrypt.checkpw(password, userSecurity.getPassword()), "用户名或密码不正确");

        long userId = userSecurity.getUserId();
        // pc和client过期时间不同
        boolean isClient = PlatformEnum.isClient(platform);
        String token = TokenUtil.genToken(request, userId);
        if (isClient) {
            redisService.set(RedisKey.getToken2UserIdKey(token, PlatformEnum.Client.remark),
                    ConfigConstants.Client_Token_Valid_Time, String.valueOf(userId));
        } else {//PC
            // 生成 token,并写入到cookie
            CookieUtil.addCookie(response, CookieUtil.Access_Token_Name, token);
            redisService.set(RedisKey.getToken2UserIdKey(token, PlatformEnum.PC.remark),
                    ConfigConstants.PC_Token_Valid_Time, String.valueOf(userId));
        }

        // 查询用户信息
        UserInfoBean userInfo = userManager.queryMySelf(userId);
        userInfo.setToken(token);
        return ResultBean.success(userInfo);
    }

    /**
     * 发送注册验证码
     *
     * @param mobile
     * @return
     */
    @RequestMapping(value = "sendRegisterSms", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean sendRegisterSms(String mobile) throws Exception {
        Assert.assertTrue(StringUtil.isPhone(mobile), "电话号码格式错误");
        Assert.assertNull(userSecurityManager.queryByPhone(mobile), "手机号码已被注册");

        String msgId = JiGuangSmsUtil.sendCodeSms(mobile, SmsTemplate.Template_ID_Code);
        Assert.assertNotEmpty(msgId, "发送验证码失败");
        redisService.set(RedisKey.Register_MsgId_Key + msgId, ConfigConstants.Sms_Valid_Time, mobile);
        return ResultBean.success(msgId);
    }

    /**
     * 验证短信
     *
     * @param msgId
     * @param code
     * @return
     */
    @RequestMapping(value = "verifyRegisterSms", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean verifyRegisterSms(String msgId, String code, HttpServletRequest request) {
        Assert.assertNotEmpty(msgId, "短信验证码错误");
        Assert.assertNotEmpty(code, "短信验证码错误");

        String mobile = redisService.get(RedisKey.Register_MsgId_Key + msgId);
        Assert.assertNotEmpty(mobile, "短信验证码已过期");

        boolean result = JiGuangSmsUtil.checkCode(msgId, code);
        Assert.assertTrue(result, "短信验证不通过");

        // 只可验证一次
        redisService.delete(RedisKey.Register_MsgId_Key + msgId);
        String unLoginId = CookieUtil.getUnLoginId(request);

        // 验证通过标志 对应关系写入redis
        redisService.set(RedisKey.UnloginId_Key + unLoginId, ConfigConstants.Sms_Valid_Time, mobile);
        return ResultBean.success(result);
    }

    /*
     * 注册
     */
    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean register(String platform, RegisterBean registerBean,
                               HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String unLoginId = CookieUtil.getUnLoginId(request);
        Assert.assertNotEmpty(unLoginId, "注册短信验证码已过期");
        String mobile = redisService.get(RedisKey.UnloginId_Key + unLoginId);
        Assert.assertNotEmpty(mobile, "注册短信验证码已过期");

        log.info("register user info is [" + registerBean + "]");

        // 判断请求参数合法行
        String error = registerBean.verify();
        Assert.assertNull(error, error);
        Assert.assertEquals(mobile, registerBean.getMobile(), "注册请求非法");

        // 校验手机号码的有效性
        Assert.assertTrue(userSecurityManager.queryByPhone(registerBean.getMobile()) == null, "手机号码已被注册");

        // 保存用户信息
        UserInfo userInfo = userManager.saveUser(registerBean);
        long userId = userInfo.getUserId();

        // 生成 token,并写入到cookie
        boolean isClient = PlatformEnum.isClient(platform);
        String token = TokenUtil.genToken(request, userId);
        if (isClient) {
            redisService.set(RedisKey.getToken2UserIdKey(token, PlatformEnum.Client.remark),
                    ConfigConstants.Client_Token_Valid_Time, String.valueOf(userId));
        } else {//PC
            // 生成 token,并写入到cookie
            CookieUtil.addCookie(response, CookieUtil.Access_Token_Name, token);
            redisService.set(RedisKey.getToken2UserIdKey(token, PlatformEnum.PC.remark),
                    ConfigConstants.PC_Token_Valid_Time, String.valueOf(userId));
        }

        // 删除关联
        redisService.delete(RedisKey.UnloginId_Key + unLoginId);

        UserInfoBean userInfoBean = userManager.queryMySelf(userId);
        return ResultBean.success(userInfoBean);
    }

    /**
     * 更新用户信息
     *
     * @param registerBean
     * @return
     */
    @RequestMapping(value = "updateUser", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean updateUser(RegisterBean registerBean) throws Exception {
        Assert.assertNotEmpty(registerBean.getMobile(), "手机号为空");
        UserInfo userInfo = userManager.queryByMobile(registerBean.getMobile());

        Assert.assertNotNull(userInfo, "用户不存在");
        if (StringUtil.isNotEmpty(registerBean.getUserName())) {
            userInfo.setUserName(registerBean.getUserName());
        }
        if (registerBean.getSex() != 0) {
            userInfo.setSex(registerBean.getSex());
        }

        if (registerBean.getMajorId() != null && registerBean.getMajorId() > 0
                && StringUtil.isNotEmpty(registerBean.getMajorName())) {
            userInfo.setMajorId(registerBean.getMajorId());
            userInfo.setMajorName(registerBean.getMajorName());
        }

        if (registerBean.getUniversityId() != null && registerBean.getUniversityId() > 0
                && StringUtil.isNotEmpty(registerBean.getUniversityName())) {
            userInfo.setUniversityId(registerBean.getUniversityId());
            userInfo.setUniversityName(registerBean.getUniversityName());
        }

        if (StringUtil.isNotEmpty(registerBean.getEntranceYear())) {
            userInfo.setEntranceYear(registerBean.getEntranceYear());
        }
        if (StringUtil.isNotEmpty(registerBean.getPicId())) {
            userInfo.setAvatarPicId(Long.parseLong(registerBean.getPicId()));
        }
        userManager.updateUser(userInfo);
        return ResultBean.success(userManager.queryMySelf(userInfo.getUserId()));
    }

    /*
     * 登出
     */
    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean logout(String platform, HttpServletRequest request, HttpServletResponse response) {
        String token = CookieUtil.getValue(request, CookieUtil.Access_Token_Name);
        if (StringUtil.isEmpty(token)) {
            return ResultBean.success(null);
        }
        boolean isClient = PlatformEnum.isClient(platform);
        if (isClient) {
            redisService.delete(RedisKey.getToken2UserIdKey(token, PlatformEnum.Client.remark));
        } else {
            redisService.delete(RedisKey.getToken2UserIdKey(token, PlatformEnum.PC.remark));
            CookieUtil.deleteCookie(request, response, CookieUtil.Access_Token_Name);
        }

        return ResultBean.success(null);
    }

    /**
     * 验证新的手机号码，并变更 userInfo 和 userSecurity 中的手机号码
     *
     * @param code
     * @param msgId
     * @param newMobile
     * @return
     */
    @RequestMapping(value = "changeNewMobile", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean changeNewMobile(
            String code, // 短信验证码
            String msgId, // 短信 ID
            String newMobile, // 新手机号
            String password//密码
    ) throws Exception {
        Assert.assertNotEmpty(msgId, "短信验证码错误");
        Assert.assertNotEmpty(code, "短信验证码错误");
        Assert.assertNotEmpty(newMobile, "手机号不能为空");
        Assert.assertNotEmpty(password, "密码不能为空");

        // 获取当前用户 id
        long userId = ThreadLocalUtil.getUserId();
        UserInfo userInfo = userManager.getUserInfo(userId);
        Assert.assertTrue(userInfo.getMobile().equals(newMobile) == false, "不能和原有手机号相同");

        boolean result = JiGuangSmsUtil.checkCode(msgId, code);
        Assert.assertTrue(result, "短信验证不通过");

        UserSecurity userSecurity = userSecurityManager.queryByUserId(userId);
        // 验证密码
        Assert.assertTrue(BCrypt.checkpw(password, userSecurity.getPassword()), "密码不正确");
        // 修改用户手机号码
        userManager.changeMobile(userId, newMobile);

        // 构建返回结果
        Map<String, Object> map = new HashMap<>();
        map.put("mobile", newMobile);
        return ResultBean.success(map);
    }

    /**
     * 用于换绑电话号码的发送短信接口
     *
     * @param mobile
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "sendExMobileSms", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean sendExMobileSms(String mobile) throws Exception {
        Assert.assertTrue(StringUtil.isPhone(mobile), "电话号码格式错误");
        String msgId = JiGuangSmsUtil.sendCodeSms(mobile, SmsTemplate.Template_ID_Code);
        Assert.assertNotEmpty(msgId, "发送验证码失败");
        return ResultBean.success(msgId);
    }

    /**
     * 更新密码
     *
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @RequestMapping(value = "changePassword", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean changePassword(String oldPassword, String newPassword) {
        // 参数验证
        Assert.assertNotEmpty(oldPassword, "旧密码不能为空");
        Assert.assertNotEmpty(newPassword, "新密码不能为空");

        Assert.assertFalse(oldPassword.equals(newPassword), "新密码不能和旧密码相同");

        long userId = ThreadLocalUtil.getUserId();
        UserSecurity userSecurity = userSecurityManager.queryByUserId(userId);
        Assert.assertTrue(BCrypt.checkpw(oldPassword, userSecurity.getPassword()), "原密码不正确");

        // 设置新的密码
        String pw_hash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        userSecurity.setPassword(pw_hash);
        userSecurity.setUpdateTime(new Date());
        userSecurityManager.updateUserSecurity(userSecurity);
        return ResultBean.success(null);
    }

    @RequestMapping(value = "checkForbid", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean checkForbid(Long toUserId) throws Exception {
        Assert.assertNotNull(toUserId, "请求参数非法");

        Long userId = ThreadLocalUtil.getUserId();
        boolean result = blackListManager.checkBlack(userId, toUserId);
        if (result) {
            return ResultBean.success(1);
        }
        return ResultBean.success(0);
    }

    @RequestMapping(value = "forbid", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean forbid(Long toUserId) throws Exception {
        Assert.assertNotNull(toUserId, "请求参数非法");

        Long userId = ThreadLocalUtil.getUserId();
        blackListManager.addBlack(userId, toUserId);
        return ResultBean.success(0);
    }

    @RequestMapping(value = "cancelForbid", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean cancelForbid(Long toUserId) throws Exception {
        Assert.assertNotNull(toUserId, "请求参数非法");

        Long userId = ThreadLocalUtil.getUserId();
        blackListManager.deleteBlack(userId, toUserId);

        return ResultBean.success(0);
    }

    /**
     * 发送短信验证码
     *
     * @param mobile
     * @return
     */
    @RequestMapping(value = "sendSmsCode", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean sendSmsCode(String mobile) throws Exception {
        Assert.assertTrue(StringUtil.isPhone(mobile), "电话号码格式错误");
        Assert.assertNotNull(userSecurityManager.queryByPhone(mobile), "手机号码没有注册");

        String msgId = JiGuangSmsUtil.sendCodeSms(mobile, SmsTemplate.Template_ID_Code);
        Assert.assertNotEmpty(msgId, "发送验证码失败");
        redisService.set(RedisKey.Register_MsgId_Key + msgId, ConfigConstants.Sms_Valid_Time, mobile);
        return ResultBean.success(msgId);
    }

    /**
     * 验证短信验证码
     *
     * @param msgId
     * @param code
     * @return
     */
    @RequestMapping(value = "verifySmsCode", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean verifySmsCode(String msgId,  String code) throws Exception {
        Assert.assertNotEmpty(msgId, "短信验证码错误");
        Assert.assertNotEmpty(code, "短信验证码错误");
        boolean isSuccess = JiGuangSmsUtil.checkCode(msgId, code);
        if (!isSuccess) {
            return ResultBean.failure("短信验证码不正确");
        }
        return ResultBean.success(isSuccess);
    }

    /**
     * 忘记密码
     *
     * @param pwd
     * @param confirmPwd
     * @param msgId
     * @param code
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "forgetPwd", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean forgetPwd(String pwd, String confirmPwd, String msgId,  String code) throws Exception {
        Assert.assertNotEmpty(pwd, "密码不能为空");
        Assert.assertEquals(pwd, confirmPwd, "确认密码不一致");
        Assert.assertNotEmpty(msgId, "短信验证码错误");
        Assert.assertNotEmpty(code, "短信验证码错误");

        String mobile = redisService.get(RedisKey.Register_MsgId_Key + msgId);
        Assert.assertNotEmpty(mobile, "短信验证码已过期");

        boolean isSuccess = JiGuangSmsUtil.checkCode(msgId, code);
        if (!isSuccess) {
            return ResultBean.failure("短信验证码不正确");
        }

        UserSecurity userSecurity = userSecurityManager.queryByPhone(mobile);
        Assert.assertNotNull(userSecurity, "用户不存在");

        // 设置新的密码
        String pw_hash = BCrypt.hashpw(pwd, BCrypt.gensalt());
        userSecurity.setPassword(pw_hash);
        userSecurity.setUpdateTime(new Date());
        userSecurityManager.updateUserSecurity(userSecurity);
        return ResultBean.success(null);
    }

}
