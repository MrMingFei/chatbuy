package com.campus.chatbuy.action;

import com.campus.chatbuy.bean.ComplainBean;
import com.campus.chatbuy.bean.ResultBean;
import com.campus.chatbuy.manager.IComplainManager;
import com.campus.chatbuy.model.GoodsComplain;
import org.apache.log4j.Logger;
import org.guzz.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Author: baoxuebin
 * Date: 2017/8/1
 */
@Controller
@RequestMapping(value = "/chatbuy/web/complain")
public class ComplainAction {

    private static final Logger log = Logger.getLogger(GoodsAction.class);

    @Autowired
    private IComplainManager complainManager;

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean saveComplain(ComplainBean complainBean) {
        log.info("saveComplain complainBean [" + complainBean + "]");

        // 验证参数
        Assert.assertNotNull(complainBean.getInitiatorId(), "投诉人不能为空");
        Assert.assertNotNull(complainBean.getTargetId(), "被投诉人不能为空");
        Assert.assertNotNull(complainBean.getGoodsId(), "相关商品不能为空");
        Assert.assertNotNull(complainBean.getReasonType(), "投诉类型为空");

        complainManager.saveComplain(complainBean);
        return ResultBean.success(0);
    }

}
