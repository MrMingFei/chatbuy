package com.campus.chatbuy.action;

import com.campus.chatbuy.bean.ResultBean;
import com.campus.chatbuy.bean.TradeInfoBean;
import com.campus.chatbuy.bean.TradeInfoResultBean;
import com.campus.chatbuy.bean.UserInfoBean;
import com.campus.chatbuy.manager.IGoodsInfoManager;
import com.campus.chatbuy.manager.IUserManager;
import com.campus.chatbuy.manager.impl.TradeInfoManager;
import com.campus.chatbuy.model.GoodsInfo;
import com.campus.chatbuy.model.TradeInfo;
import com.campus.chatbuy.model.UserInfo;
import com.campus.chatbuy.sms.JiGuangSmsUtil;
import com.campus.chatbuy.sms.SmsTemplate;
import com.campus.chatbuy.util.ThreadLocalUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.guzz.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Created by jinku on 2017/9/8.
 */
@Controller
@RequestMapping(value = "/chatbuy/web/trade")
public class TradeInfoAction {

    private static final Logger log = Logger.getLogger(TradeInfoAction.class);

    @Autowired
    private TradeInfoManager tradeInfoManager;

    @Autowired
    private IUserManager userManager;

    @Autowired
    private IGoodsInfoManager goodsInfoManager;

    /**
     * 买家发起交易
     *
     * @param tradeInfoBean
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "insert", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean insert(TradeInfoBean tradeInfoBean) throws Exception {
        log.info("trade insert tradeInfoBean [" + tradeInfoBean + "]");
        Assert.assertNotNull(tradeInfoBean.getGoodsId(), "商品id不能为空");
        Assert.assertNotEmpty(tradeInfoBean.getTradeAddress(), "交易地址不能为空");
        Assert.assertNotNull(tradeInfoBean.getTradePrice(), "交易价格不能为空");
        Assert.assertNotEmpty(tradeInfoBean.getTradeTime(), "交易时间不能为空");
        Long tradeId = tradeInfoManager.insert(tradeInfoBean);
        return ResultBean.success(String.valueOf(tradeId));
    }

    @RequestMapping(value = "confirm", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean confirm(Long tradeId) throws Exception {
        Assert.assertNotNull(tradeId, "交易ID为空");
        TradeInfo tradeInfo = tradeInfoManager.queryById(tradeId);
        Assert.assertNotNull(tradeInfo, "交易信息不存在");
        Assert.assertTrue(tradeInfo.getTradeState() != TradeInfo.Trade_Status_Confirmed, "该商品已经达成交易");

        // 更新交易状态,并保存用户交易记录
        tradeInfoManager.confirmTrade(tradeInfo);
        return ResultBean.success(tradeId.toString());
    }

    @RequestMapping(value = "tradeInfo", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean tradeInfo(Long tradeId) throws Exception {
        Assert.assertNotNull(tradeId, "交易ID为空");
        TradeInfo tradeInfo = tradeInfoManager.queryById(tradeId);
        Assert.assertNotNull(tradeInfo, "交易信息不存在");

        TradeInfoResultBean tradeInfoResultBean = new TradeInfoResultBean();
        BeanUtils.copyProperties(tradeInfoResultBean, tradeInfo);
        return ResultBean.success(tradeInfoResultBean);
    }

    @RequestMapping(value = "tradeInfoList", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean tradeInfoList(String tradeIdList) throws Exception {
        Assert.assertNotEmpty(tradeIdList, "交易ID为空");
        String[] idArray = tradeIdList.split(",");
        Assert.assertTrue(idArray != null && idArray.length > 0, "交易ID为空");
        List<Long> idList = new ArrayList<>();
        for(int i = 0; i < idArray.length; i++) {
            idList.add(Long.parseLong(idArray[i]));
        }
        // 批量查询
        List<TradeInfo> tradeInfoList = tradeInfoManager.queryByIdList(idList);
        List<TradeInfoResultBean> tradeInfoBeanList = new ArrayList<>();
        for(TradeInfo tradeInfo : tradeInfoList) {
            TradeInfoResultBean tradeInfoResultBean = new TradeInfoResultBean();
            BeanUtils.copyProperties(tradeInfoResultBean, tradeInfo);
            tradeInfoBeanList.add(tradeInfoResultBean);
        }
        return ResultBean.success(tradeInfoBeanList);
    }

    @RequestMapping(value = "boughtList", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean boughtList(Long userId, Integer pageNo, Integer pageSize) throws Exception {
        Assert.assertNotNull(userId, "用户id不能为空");

        if (pageNo == null) {
            pageNo = 1;
        }
        if (pageSize == null) {
            pageSize = 20;
        }

        return ResultBean.success(tradeInfoManager.boughtList(userId, pageNo, pageSize));
    }

    /**
     * 提醒卖家上线的短信通知
     *
     * @param sellerUserId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "onlineNotice", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean onlineNotice(Long sellerUserId) throws Exception {
        Assert.assertNotNull(sellerUserId, "卖家用户Id不能为空");
        UserInfo userInfo = userManager.getUserInfo(sellerUserId);
        Assert.assertNotNull(userInfo, "该用户不存在");

        Map<String, String> paramsMap = new HashMap<>();
        String msgId = JiGuangSmsUtil.sendParamSms(userInfo.getMobile(),
                SmsTemplate.Template_ID_Online_Notice, paramsMap);
        Assert.assertNotEmpty(msgId, "短信发送失败");
        return ResultBean.success(null);
    }
}
