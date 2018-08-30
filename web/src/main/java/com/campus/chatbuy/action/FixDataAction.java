package com.campus.chatbuy.action;

import com.campus.chatbuy.bean.ResultBean;
import com.campus.chatbuy.constans.RedisKey;
import com.campus.chatbuy.manager.impl.FIxDataManager;
import com.campus.chatbuy.model.GoodsCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by jinku on 2018/1/3.
 */
@Controller
@RequestMapping(value = "/chatbuy/web/fix")
public class FixDataAction {

    @Autowired
    private FIxDataManager fIxDataManager;

    @RequestMapping(value = "fixUserType", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean fixBuyType() {
        fIxDataManager.fixUserType();
        return ResultBean.success(null);
    }

    /**
     * 删除过期的session消息(redis)
     *
     * @return
     */
    @RequestMapping(value = "clearRedisMessage", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean clearRedisMessage() {
        fIxDataManager.clearRedisMessage();
        return ResultBean.success(null);
    }

    /**
     * 删除指定会话过期的session消息(redis)
     *
     * @return
     */
    @RequestMapping(value = "clearSessionRedisMessage", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean clearSessionRedisMessage(String sessionId) {
        fIxDataManager.clearRedisMessage(sessionId);
        return ResultBean.success(null);
    }
}
