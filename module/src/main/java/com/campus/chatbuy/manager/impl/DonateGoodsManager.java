package com.campus.chatbuy.manager.impl;

import com.campus.chatbuy.bean.*;
import com.campus.chatbuy.dao.DonateGoodsDao;
import com.campus.chatbuy.enums.DonateGoodsStatus;
import com.campus.chatbuy.id.generator.IdGeneratorFactory;
import com.campus.chatbuy.manager.IGoodsCategoryManager;
import com.campus.chatbuy.manager.IGoodsInfoManager;
import com.campus.chatbuy.manager.IGoodsPicManager;
import com.campus.chatbuy.manager.IUserManager;
import com.campus.chatbuy.model.*;
import com.campus.chatbuy.util.DateUtils;
import com.campus.chatbuy.util.ThreadLocalUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.guzz.dao.GuzzBaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by jinku on 2017/12/10.
 * 捐赠商品相关的业务逻辑
 */
@Service
public class DonateGoodsManager {

    private static final Logger log = Logger.getLogger(DonateGoodsManager.class);

    @Autowired
    private DonateGoodsDao donateGoodsDao;

    @Autowired
    private GuzzBaseDao guzzBaseDao;

    @Autowired
    private IGoodsInfoManager goodsInfoManager;

    @Autowired
    private IUserManager userManager;

    @Autowired
    private IGoodsCategoryManager goodsCategoryManager;

    @Autowired
    private IGoodsPicManager goodsPicManager;

    /**
     * 导出excel 标题
     */
    private static final String[] HEADERS = {"序号", "商品名称", "商品类别", "捐赠者", "捐赠时间", "取货时间",
                                             "取货地址", "联系方式", "是否取件"};

    /**
     * 捐赠商品总数
     *
     * @return
     */
    public long donateGoodsCount() {
        return donateGoodsDao.countAll();
    }

    /**
     * 保存捐赠商品
     *
     * @param publishBean
     * @throws Exception
     */
    public void donateGoods(DonatePublishBean publishBean) throws Exception {
        //保存GoodsInfo
        GoodsInfo goodsInfo = new GoodsInfo();
        goodsInfo.setGoodsId(IdGeneratorFactory.generateId());
        goodsInfo.setGoodsName(publishBean.getGoodsName());
        goodsInfo.setGoodsDesc(publishBean.getGoodsDesc());
        goodsInfo.setCategoryId(Integer.parseInt(publishBean.getCategoryId()));
        goodsInfo.setCreateTime(new Date());
        goodsInfo.setPrice(0L);
        goodsInfo.setOriginPrice(0L);
        goodsInfo.setUserId(ThreadLocalUtil.getUserId());
        goodsInfo.setReadNum(0L);
        goodsInfo.setStatus(GoodsInfo.Status_Deleted);
        goodsInfo.setSource(GoodsInfo.Source_HuoDong);
        goodsInfo.setPayWay(GoodsInfo.Pay_Way_offline);
        guzzBaseDao.insert(goodsInfo);

        //保存GoodsPic
        if (publishBean.getPicList() != null && publishBean.getPicList().size() > 0) {
            for (Long picId : publishBean.getPicList()) {
                GoodsPic goodsPic = new GoodsPic();
                goodsPic.setPicId(picId);
                goodsPic.setValidity(1);
                goodsPic.setCreateTime(new Date());
                goodsPic.setGoodsId(goodsInfo.getGoodsId());
                guzzBaseDao.insert(goodsPic);
            }
        }
        DonateGoods donateGoods = new DonateGoods();
        donateGoods.setGoodsId(goodsInfo.getGoodsId());
        donateGoods.setCreateTime(new Date());
        donateGoods.setDeliveryType(publishBean.getDeliveryType());
        donateGoods.setDonateStatus(DonateGoodsStatus.ToDoAudit.key);
        donateGoods.setPickPlace(publishBean.getPickPlace());
        donateGoods.setPickTimeWeek(publishBean.getPickTimeWeek());
        donateGoods.setPickTimeDay(publishBean.getPickTimeDay());
        donateGoods.setUserId(goodsInfo.getUserId());
        donateGoodsDao.insert(donateGoods);
    }

    /**
     * 商品捐赠列表
     *
     * @param pageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    public PageBean<DonatorInfoBean> goodsDonatorList(int pageNo, int pageSize) throws Exception {
        List<DonatorInfoBean> donatorInfoBeanList = new ArrayList<>();
        long count = donateGoodsDao.countAll();
        if (count == 0) {
            return PageBean.getPageBean(count, donatorInfoBeanList);
        }
        int start = (pageNo - 1) * pageSize;
        List<DonateGoods> donateGoodsList = donateGoodsDao.queryGoods(start, pageSize);

        if (CollectionUtils.isEmpty(donateGoodsList)) {
            return PageBean.getPageBean(count, donatorInfoBeanList);
        }
        Set<Long> goodsIdSet = new HashSet<>();
        Set<Long> userIdSet = new HashSet<>();
        Set<Integer> categoryIdSet = new HashSet<>();
        for (DonateGoods donateGoods : donateGoodsList) {
            goodsIdSet.add(donateGoods.getGoodsId());
            userIdSet.add(donateGoods.getUserId());
        }

        Map<Long, GoodsInfo> goodsInfoMap = goodsInfoManager.queryMapList(goodsIdSet);
        Map<Long, UserInfo> userInfoMap = userManager.queryMapList(userIdSet);

        // 填充用户名和商品名称
        for (DonateGoods donateGoods : donateGoodsList) {
            DonatorInfoBean donatorInfoBean = new DonatorInfoBean();
            GoodsInfo goodsInfo = goodsInfoMap.get(donateGoods.getGoodsId());
            donatorInfoBean.setUserName(userInfoMap.get(goodsInfo.getUserId()).getUserName());
            donatorInfoBean.setGoodsName(goodsInfo.getGoodsName());
            donatorInfoBean.setCategoryId(goodsInfo.getCategoryId());
            categoryIdSet.add(goodsInfo.getCategoryId());
            donatorInfoBeanList.add(donatorInfoBean);
        }
        // 填充分类名称
        Map<Integer, GoodsCategory> categoryMap = goodsCategoryManager.queryCategoryMap(categoryIdSet);
        for (DonatorInfoBean donatorInfoBean : donatorInfoBeanList) {
            donatorInfoBean.setCategoryName(categoryMap.get(donatorInfoBean.getCategoryId()).getCategoryName());
        }
        return PageBean.getPageBean(count, donatorInfoBeanList);
    }

    /**
     * 获取当前用户的捐赠列表
     *
     * @param pageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    public PageBean<DonateGoodsBean> myDonateGoods(int pageNo, int pageSize) throws Exception {
        long userId = ThreadLocalUtil.getUserId();
        List<DonateGoodsBean> donateGoodsBeanList = new ArrayList<>();
        long count = donateGoodsDao.countByUser(userId);
        if (count == 0) {
            return PageBean.getPageBean(count, donateGoodsBeanList);
        }
        int start = (pageNo - 1) * pageSize;
        List<DonateGoods> donateGoodsList = donateGoodsDao.queryGoodsByUser(userId, start, pageSize);
        if (CollectionUtils.isEmpty(donateGoodsList)) {
            return PageBean.getPageBean(count, donateGoodsBeanList);
        }

        Set<Long> goodsIdSet = new HashSet<>();
        Set<Long> userIdSet = new HashSet<>();
        Set<Integer> categoryIdSet = new HashSet<>();
        for (DonateGoods donateGoods : donateGoodsList) {
            goodsIdSet.add(donateGoods.getGoodsId());
            userIdSet.add(donateGoods.getUserId());
        }

        Map<Long, GoodsInfo> goodsInfoMap = goodsInfoManager.queryMapList(goodsIdSet);
        Map<Long, UserInfo> userInfoMap = userManager.queryMapList(userIdSet);
        Map<Long, List<PicInfoBean>> goodsPicMap = goodsPicManager.queryByGoodsId(goodsIdSet);

        for (DonateGoods donateGoods : donateGoodsList) {
            GoodsInfo goodsInfo = goodsInfoMap.get(donateGoods.getGoodsId());
            categoryIdSet.add(goodsInfo.getCategoryId());
            UserInfo userInfo = userInfoMap.get(goodsInfo.getUserId());
            DonateGoodsBean donateGoodsBean = new DonateGoodsBean();
            donateGoodsBean.setGoodsId(String.valueOf(donateGoods.getGoodsId().longValue()));
            if (donateGoods.getDeliveryTime() != null) {
                donateGoodsBean.setDeliveryTime(donateGoods.getDeliveryTime().getTime());
            }
            if (donateGoods.getRefuseTime() != null) {
                donateGoodsBean.setRefuseTime(donateGoods.getRefuseTime().getTime());
            }
            donateGoodsBean.setRefuseReason(donateGoods.getRefuseReason());
            donateGoodsBean.setDeliveryType(donateGoods.getDeliveryType());
            donateGoodsBean.setDonateStatus(donateGoods.getDonateStatus());
            donateGoodsBean.setGoodsDesc(goodsInfo.getGoodsDesc());
            donateGoodsBean.setGoodsName(goodsInfo.getGoodsName());
            donateGoodsBean.setPickPlace(donateGoods.getPickPlace());
            donateGoodsBean.setPickTimeWeek(donateGoods.getPickTimeWeek());
            donateGoodsBean.setPickTimeDay(donateGoods.getPickTimeDay());
            donateGoodsBean.setCategoryId(goodsInfo.getCategoryId());
            donateGoodsBean.setUserId(String.valueOf(userInfo.getUserId()));
            donateGoodsBean.setUserName(userInfo.getUserName());
            donateGoodsBean.setCreateTime(donateGoods.getCreateTime().getTime());
            donateGoodsBean.setPicList(goodsPicMap.get(goodsInfo.getGoodsId()));

            donateGoodsBeanList.add(donateGoodsBean);
        }

        Map<Integer, GoodsCategory> categoryMap = goodsCategoryManager.queryCategoryMap(categoryIdSet);

        for (DonateGoodsBean donateGoodsBean : donateGoodsBeanList) {
            donateGoodsBean.setCategoryName(categoryMap.get(donateGoodsBean.getCategoryId()).getCategoryName());
        }
        return PageBean.getPageBean(count, donateGoodsBeanList);
    }

    /**
     * 查询捐赠商品列表
     *
     * @param donateQueryBean
     * @return
     * @throws Exception
     */
    public PageBean<DonateGoodsBean> queryDonateGoods(DonateQueryBean donateQueryBean) throws Exception {
        Integer pageNo = donateQueryBean.getPageNo();
        Integer pageSize = donateQueryBean.getPageSize();
        List<Integer> donateStatus = donateQueryBean.getDonateStatus();
        List<DonateGoodsBean> donateGoodsBeanList = new ArrayList<>();

        if (pageNo == null) {
            pageNo = 1;
        }
        if (pageSize == null) {
            pageSize = 20;
        }
        donateQueryBean.setPageNo(pageNo);
        donateQueryBean.setPageSize(pageSize);

        long count = donateGoodsDao.countByStatusList(donateStatus);
        if (count == 0) {
            return PageBean.getPageBean(count, donateGoodsBeanList);
        }
        int startNum = (pageNo - 1) * pageSize;
        List<DonateGoods> donateGoodsList = donateGoodsDao.queryGoodsByStatus(donateStatus, startNum, pageSize);
        if (CollectionUtils.isEmpty(donateGoodsList)) {
            return PageBean.getPageBean(count, donateGoodsBeanList);
        }
        Set<Long> goodsIdSet = new HashSet<>();
        Set<Long> userIdSet = new HashSet<>();
        Set<Integer> categoryIdSet = new HashSet<>();
        for (DonateGoods donateGoods : donateGoodsList) {
            goodsIdSet.add(donateGoods.getGoodsId());
            userIdSet.add(donateGoods.getUserId());
        }

        Map<Long, GoodsInfo> goodsInfoMap = goodsInfoManager.queryMapList(goodsIdSet);
        Map<Long, UserInfo> userInfoMap = userManager.queryMapList(userIdSet);
        Map<Long, List<PicInfoBean>> goodsPicMap = goodsPicManager.queryByGoodsId(goodsIdSet);

        for (DonateGoods donateGoods : donateGoodsList) {
            GoodsInfo goodsInfo = goodsInfoMap.get(donateGoods.getGoodsId());
            categoryIdSet.add(goodsInfo.getCategoryId());
            UserInfo userInfo = userInfoMap.get(goodsInfo.getUserId());
            DonateGoodsBean donateGoodsBean = new DonateGoodsBean();
            if (donateGoods.getDeliveryTime() != null) {
                donateGoodsBean.setDeliveryTime(donateGoods.getDeliveryTime().getTime());
            }
            if (donateGoods.getRefuseTime() != null) {
                donateGoodsBean.setRefuseTime(donateGoods.getRefuseTime().getTime());
            }
            donateGoodsBean.setGoodsId(String.valueOf(donateGoods.getGoodsId().longValue()));
            donateGoodsBean.setRefuseReason(donateGoods.getRefuseReason());
            donateGoodsBean.setDeliveryType(donateGoods.getDeliveryType());
            donateGoodsBean.setDonateStatus(donateGoods.getDonateStatus());
            donateGoodsBean.setGoodsDesc(goodsInfo.getGoodsDesc());
            donateGoodsBean.setGoodsName(goodsInfo.getGoodsName());
            donateGoodsBean.setPickPlace(donateGoods.getPickPlace());
            donateGoodsBean.setPickTimeWeek(donateGoods.getPickTimeWeek());
            donateGoodsBean.setPickTimeDay(donateGoods.getPickTimeDay());
            donateGoodsBean.setCategoryId(goodsInfo.getCategoryId());
            donateGoodsBean.setUserId(String.valueOf(userInfo.getUserId()));
            donateGoodsBean.setUserName(userInfo.getUserName());
            donateGoodsBean.setMobile(userInfo.getMobile());
            donateGoodsBean.setCreateTime(donateGoods.getCreateTime().getTime());
            donateGoodsBean.setPicList(goodsPicMap.get(goodsInfo.getGoodsId()));

            donateGoodsBeanList.add(donateGoodsBean);
        }

        Map<Integer, GoodsCategory> categoryMap = goodsCategoryManager.queryCategoryMap(categoryIdSet);

        for (DonateGoodsBean donateGoodsBean : donateGoodsBeanList) {
            GoodsCategory goodsCategory = categoryMap.get(donateGoodsBean.getCategoryId());
            donateGoodsBean.setCategoryName(goodsCategory.getCategoryName());
        }
        return PageBean.getPageBean(count, donateGoodsBeanList);
    }

    /**
     * 捐赠统计信息查询
     *
     * @return
     * @throws Exception
     */
    public DonateGoodsStat donateGoodsStat() {
        DonateGoodsStat donateGoodsStat = new DonateGoodsStat();
        int waitAudit = DonateGoodsStatus.ToDoAudit.key;
        long waitAuditCount = donateGoodsDao.countByStatus(waitAudit);
        donateGoodsStat.setWaitAuditCount(waitAuditCount);

        int auditRefuse = DonateGoodsStatus.AuditRefused.key;
        long auditRefuseCount = donateGoodsDao.countByStatus(auditRefuse);
        donateGoodsStat.setAuditRefuseCount(auditRefuseCount);

        int waitRecived = DonateGoodsStatus.ToDoReceived.key;
        long waitRecivedCount = donateGoodsDao.countByStatus(waitRecived);
        donateGoodsStat.setWaitRecivedCount(waitRecivedCount);

        int recivedRefuse = DonateGoodsStatus.ReceiveRefused.key;
        long recivedRefuseCount = donateGoodsDao.countByStatus(recivedRefuse);
        donateGoodsStat.setRecivedRefuseCount(recivedRefuseCount);

        int waitDelivery = DonateGoodsStatus.ToDoDelivery.key;
        long waitDeliveryCount = donateGoodsDao.countByStatus(waitDelivery);
        donateGoodsStat.setWaitDeliveryCount(waitDeliveryCount);

        int inDelivery = DonateGoodsStatus.Delivering.key;
        long inDeliveryCount = donateGoodsDao.countByStatus(inDelivery);
        donateGoodsStat.setInDeliveryCount(inDeliveryCount);

        int haveArrived = DonateGoodsStatus.Delivered.key;
        long haveArrivedCount = donateGoodsDao.countByStatus(haveArrived);
        donateGoodsStat.setHaveArrivedCount(haveArrivedCount);

        return donateGoodsStat;
    }

    /**
     * 获取当前商品状态
     *
     * @return
     * @throws Exception
     */
    public int currentDonateStatus(long goodsId) throws  Exception{
        return donateGoodsDao.queryCurrentStatus(goodsId);
    }

    public void updateDonateStatus(long goodsId, Integer donateStatus, Date updateTime) {
        donateGoodsDao.updateStatus(goodsId, donateStatus, updateTime);
    }

    public void updateStatusDelivered(long goodsId, Integer donateStatus, Date updateTime, Date deliveryTime) {
        donateGoodsDao.updateStatusDelivered(goodsId, donateStatus, updateTime, deliveryTime);
    }

    public void updateStatusRefuse(long goodsId, Integer donateStatus, Date refuseTime, Date updateTime, String
            refuseReason) {
        donateGoodsDao.updateStatusRefuse(goodsId, donateStatus, refuseTime, updateTime, refuseReason);
    }

    /**
     * 待接收列表导出生成excel
     *
     * @param donateQueryBean
     * @return
     */
    public byte[] buildToBeReceiveExcel(DonateQueryBean donateQueryBean) throws Exception {
        donateQueryBean.setPageNo(1);
        donateQueryBean.setPageSize(1000);
        PageBean<DonateGoodsBean> pageBean = queryDonateGoods(donateQueryBean);
        List<DonateGoodsBean> donateGoodsBeanList = pageBean.getRows();

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("待收件列表");
        sheet.setDefaultColumnWidth(20);
        HSSFRow headerRow = sheet.createRow(0);
        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        for (int i = 0; i < HEADERS.length; i++) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellStyle(style);
            cell.setCellValue(HEADERS[i]);
        }
        int rowNum = 1;
        HSSFCell cell;
        if (CollectionUtils.isNotEmpty(donateGoodsBeanList)) {
            for (DonateGoodsBean donateGoodsBean : donateGoodsBeanList) {
                HSSFRow row = sheet.createRow(rowNum);
                cell = row.createCell(0);//序号
                cell.setCellStyle(style);
                cell.setCellValue(rowNum);
                cell = row.createCell(1);//商品名称
                cell.setCellStyle(style);
                cell.setCellValue(donateGoodsBean.getGoodsName());
                cell = row.createCell(2);//商品分类
                cell.setCellStyle(style);
                cell.setCellValue(donateGoodsBean.getCategoryName());
                cell = row.createCell(3);//捐赠者
                cell.setCellStyle(style);
                cell.setCellValue(donateGoodsBean.getUserName());
                cell = row.createCell(4);//捐赠时间
                cell.setCellStyle(style);
                cell.setCellValue(DateUtils.format(donateGoodsBean.getCreateTime()));
                cell = row.createCell(5);//取货时间
                cell.setCellStyle(style);
                cell.setCellValue(donateGoodsBean.getPickTimeStr());
                cell = row.createCell(6);//取货地点
                cell.setCellStyle(style);
                cell.setCellValue(donateGoodsBean.getPickPlace());
                cell = row.createCell(7);//联系方式
                cell.setCellStyle(style);
                cell.setCellValue(donateGoodsBean.getMobile());
                cell = row.createCell(8);//是否取件
                cell.setCellStyle(style);
                cell.setCellValue("否");
                rowNum++;
            }
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            workbook.write(os);
        } catch (IOException e) {
            log.error("buildToBeReceiveExcel exception", e);
        }
        return os.toByteArray();
    }
}
