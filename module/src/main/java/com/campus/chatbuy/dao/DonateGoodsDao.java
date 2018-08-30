package com.campus.chatbuy.dao;

import com.campus.chatbuy.model.BlackList;
import com.campus.chatbuy.model.DonateGoods;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by jinku on 2017/11/21.
 * 捐赠商品的数据访问层
 */
@Component
public interface DonateGoodsDao {

    /**
     * 插入记录
     *
     * @param donateGoods
     * @return
     */
    int insert(DonateGoods donateGoods);

    /**
     * 查询总条数
     *
     * @return
     */
    long countAll();

    /**
     * 分页查询捐赠商品
     *
     * @param start
     * @param offset
     * @return
     */
    List<DonateGoods> queryGoods(@Param("start") int start, @Param("offset") int offset);

    /**
     * 查询用户捐赠条数
     *
     * @param userId
     * @return
     */
    long countByUser(@Param("userId") long userId);

    /**
     * 查询捐赠商品条数
     *
     * @param donateStatus
     * @return
     */
    long countByStatus(@Param("donateStatus") Integer donateStatus);

    /**
     * 查询捐赠商品条数
     *
     * @param donateStatus
     * @return
     */
    long countByStatusList(@Param("donateStatus") List<Integer> donateStatus);

    /**
     * 分页查询用户捐赠商品
     *
     * @param userId
     * @param start
     * @param pageSize
     * @return
     */
    List<DonateGoods> queryGoodsByUser(@Param("userId") long userId, @Param("start") int start,
                                       @Param("pageSize") int pageSize);

    /**
     * 分页查询用户捐赠商品
     *
     * @param donateStatus
     * @param startNum
     * @param pageSize
     * @return
     */
    List<DonateGoods> queryGoodsByStatus(@Param("donateStatus") List<Integer> donateStatus,
                                         @Param("startNum") int startNum,
                                         @Param("pageSize") int pageSize);

    /**
     * 更新捐赠状态
     *
     * @param goodsId
     * @param donateStatus
     * @param updateTime
     * @return
     */
    int updateStatus(@Param("goodsId") long goodsId, @Param("donateStatus") Integer donateStatus,
                     @Param("updateTime") Date updateTime);

    /**
     * 获取当前商品状态
     *
     * @param goodsId
     * @return
     */
    int queryCurrentStatus(@Param("goodsId") long goodsId);

    /**
     * 更新捐赠状态：送达
     *
     * @param goodsId
     * @param donateStatus
     * @param updateTime
     * @param deliveryTime
     * @return
     */
    int updateStatusDelivered(@Param("goodsId") long goodsId, @Param("donateStatus") Integer donateStatus,
                     @Param("updateTime") Date updateTime, @Param("deliveryTime") Date deliveryTime);

    /**
     * 更新捐赠状态：拒绝
     *
     * @param goodsId
     * @param donateStatus
     * @param updateTime
     * @param refuseTime
     * @param refuseReason
     * @return
     */
    int updateStatusRefuse(@Param("goodsId") long goodsId, @Param("donateStatus") Integer donateStatus,
                     @Param("refuseTime") Date refuseTime, @Param("updateTime") Date updateTime,
                     @Param("refuseReason") String refuseReason);
}
