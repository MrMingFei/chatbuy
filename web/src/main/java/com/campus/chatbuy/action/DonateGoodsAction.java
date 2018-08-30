package com.campus.chatbuy.action;

import com.campus.chatbuy.bean.*;
import com.campus.chatbuy.enums.*;
import com.campus.chatbuy.manager.impl.DonateGoodsManager;
import com.campus.chatbuy.model.GoodsInfo;
import com.campus.chatbuy.model.DonateGoods;
import com.campus.chatbuy.util.DownloadUtil;
import com.campus.chatbuy.util.StringUtil;
import org.apache.log4j.Logger;
import org.guzz.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jinku on 2017/12/10.
 * 捐赠商品相关接口
 */
@Controller
@RequestMapping(value = "/chatbuy/web/goods")
public class DonateGoodsAction {

    private static final Logger log = Logger.getLogger(DonateGoodsAction.class);

    @Autowired
    private DonateGoodsManager donateGoodsManager;

    /**
     * 捐赠商品总数
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/donateGoodsCount", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean donateGoodsCount() throws Exception {
        return ResultBean.success(donateGoodsManager.donateGoodsCount());
    }

    /**
     * 捐赠商品发布
     *
     * @param publishBean
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/donateGoodsPublish", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean donateGoodsPublish(DonatePublishBean publishBean) throws Exception {

        Assert.assertNotEmpty(publishBean.getGoodsName(), "goodsName is null");
        Assert.assertNotEmpty(publishBean.getGoodsDesc(), "goodsDesc is null");
        Assert.assertNotEmpty(publishBean.getCategoryId(), "categoryId is null");
        Assert.assertNotNull(publishBean.getDeliveryType(), "deliveryType is null");

        DeliveryType deliveryType = DeliveryType.fromKey(publishBean.getDeliveryType());
        Assert.assertNotNull(deliveryType, "deliveryType is illegal");

        if (deliveryType == DeliveryType.Other_Qu) {//上门取
            Assert.assertNotEmpty(publishBean.getPickPlace(), "pickPlace is null");
            Assert.assertNotNull(publishBean.getPickTimeWeek(), "pickTimeWeek is null");
            Assert.assertNotNull(publishBean.getPickTimeDay(), "pickTimeDay is null");
            WeekEnum weekEnum = WeekEnum.fromKey(publishBean.getPickTimeWeek());
            Assert.assertNotNull(weekEnum, "pickTimeWeek is illegal");
            PickTime pickTime = PickTime.fromKey(publishBean.getPickTimeDay());
            Assert.assertNotNull(pickTime, "pickTimeDay is illegal");
        }

        donateGoodsManager.donateGoods(publishBean);
        return ResultBean.success(null);
    }

    /**
     * 捐赠名单列表
     *
     * @param pageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/goodsDonatorList", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean goodsDonatorList(Integer pageNo, Integer pageSize) throws Exception {
        Assert.assertNotNull(pageNo, "pageNo is null");
        Assert.assertNotNull(pageSize, "pageSize is null");

        PageBean pageBean = donateGoodsManager.goodsDonatorList(pageNo, pageSize);
        return ResultBean.success(pageBean);
    }

    /**
     * 我的捐赠列表
     *
     * @param pageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/myDonateGoods", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean myDonateGoods(Integer pageNo, Integer pageSize) throws Exception {
        Assert.assertNotNull(pageNo, "pageNo is null");
        Assert.assertNotNull(pageSize, "pageSize is null");

        PageBean pageBean = donateGoodsManager.myDonateGoods(pageNo, pageSize);
        return ResultBean.success(pageBean);
    }

    /**
     * 捐赠商品列表
     *
     * @param donateQueryBean
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryDonateGoods", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean queryDonateGoods(DonateQueryBean donateQueryBean) throws Exception {
        Assert.assertNotNull(donateQueryBean.getPageNo(), "pageNo is null");
        Assert.assertNotNull(donateQueryBean.getPageSize(), "pageSize is null");

        PageBean pageBean = donateGoodsManager.queryDonateGoods(donateQueryBean);
        return ResultBean.success(pageBean);
    }

    /**
     * 捐赠统计信息查询
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/donateStatusCount", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean donateStatusCount() throws Exception {
        DonateGoodsStat donateGoodsStat = donateGoodsManager.donateGoodsStat();
        return ResultBean.success(donateGoodsStat);
    }

    /**
     * 接受捐赠
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/confirmDonateGoods", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean confirmDonateGoods(Long goodsId) throws Exception {
        Assert.assertNotNull(goodsId, "goodsIs is null");
        if (donateGoodsManager.currentDonateStatus(goodsId) != DonateGoodsStatus.ToDoAudit.key) {
            return ResultBean.failure("current goods status is wrong");
        }

        donateGoodsManager.updateDonateStatus(goodsId, DonateGoodsStatus.ToDoReceived.key, new Date());
        return ResultBean.success(null);
    }

    /**
     * 审核拒绝捐赠
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/refuseDonateGoods", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean refuseDonateGoods(Long goodsId, String refuseReason) throws Exception {
        Assert.assertNotNull(goodsId, "goodsIs is null");
        Assert.assertNotNull(refuseReason, "refuseReason is null");
        if (donateGoodsManager.currentDonateStatus(goodsId) != DonateGoodsStatus.ToDoAudit.key) {
            return ResultBean.failure("current goods status is wrong");
        }

        donateGoodsManager.updateStatusRefuse(goodsId, DonateGoodsStatus.AuditRefused.key, new Date(),
                new Date(), refuseReason);
        return ResultBean.success(null);
    }

    /**
     * 接收时拒绝捐赠
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/receiveRefusedDoante", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean receiveRefusedDoante(Long goodsId, String refuseReason) throws Exception {
        Assert.assertNotNull(goodsId, "goodsIs is null");
        Assert.assertNotNull(refuseReason, "refuseReason is null");
        if (donateGoodsManager.currentDonateStatus(goodsId) != DonateGoodsStatus.ToDoReceived.key) {
            return ResultBean.failure("current goods status is wrong");
        }

        donateGoodsManager.updateStatusRefuse(goodsId, DonateGoodsStatus.ReceiveRefused.key, new Date(),
                new Date(), refuseReason);
        return ResultBean.success(null);
    }

    /**
     * 已经接收捐赠
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/receivedDonateGoods", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean receivedDonateGoods(Long goodsId) throws Exception {
        Assert.assertNotNull(goodsId, "goodsIs is null");
        if (donateGoodsManager.currentDonateStatus(goodsId) != DonateGoodsStatus.ToDoReceived.key) {
            return ResultBean.failure("current goods status is wrong");
        }

        donateGoodsManager.updateDonateStatus(goodsId, DonateGoodsStatus.ToDoDelivery.key, new Date());
        return ResultBean.success(null);
    }

    /**
     * 投递捐赠商品
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/deliveryDonateGoods", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean deliveryDonateGoods(Long goodsId) throws Exception {
        Assert.assertNotNull(goodsId, "goodsIs is null");
        if (donateGoodsManager.currentDonateStatus(goodsId) != DonateGoodsStatus.ToDoDelivery.key) {
            return ResultBean.failure("current goods status is wrong");
        }

        donateGoodsManager.updateDonateStatus(goodsId, DonateGoodsStatus.Delivering.key, new Date());
        return ResultBean.success(null);
    }

    /**
     * 确认商品送达
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/arrivedDonateGoods", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean arrivedDonateGoods(Long goodsId) throws Exception {
        Assert.assertNotNull(goodsId, "goodsIs is null");
        if (donateGoodsManager.currentDonateStatus(goodsId) != DonateGoodsStatus.Delivering.key) {
            return ResultBean.failure("current goods status is wrong");
        }

        donateGoodsManager.updateStatusDelivered(goodsId, DonateGoodsStatus.Delivered.key, new Date(), new Date());
        return ResultBean.success(null);
    }

    /**
     * 下载待接收的excel
     */
    @RequestMapping(value = "/downloadToBeReceiveExcel", method = RequestMethod.GET)
    public void downloadToBeReceiveExcel(HttpServletResponse response) {
        DonateQueryBean donateQueryBean = new DonateQueryBean();
        List<Integer> donateStatus = new ArrayList<>();
        donateStatus.add(DonateGoodsStatus.ToDoReceived.key);
        donateQueryBean.setDonateStatus(donateStatus);
        try {
            byte[] fileData = donateGoodsManager.buildToBeReceiveExcel(donateQueryBean);
            DownloadUtil.printFileData("待接收列表", FileType.EXCEL, fileData, response, "下载出错了");
        } catch (Exception e) {
            DownloadUtil.printErrorInfo(response, "下载出错了");
            log.error("downloadToBeReceiveExcel exception", e);
        }
    }


}
