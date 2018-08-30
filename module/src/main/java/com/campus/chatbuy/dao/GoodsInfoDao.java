package com.campus.chatbuy.dao;

import com.campus.chatbuy.model.GoodsInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jinku on 2017/9/12.
 */
@Component
public interface GoodsInfoDao {

    List<GoodsInfo> selectByCondition(@Param("tagIdList") List<Integer> tagIdList,
                                      @Param("universityId") Integer universityId,
                                      @Param("majorId") Integer majorId,
                                      @Param("categoryIdList") List<Integer> categoryIdList,
                                      @Param("goodsStatus") Integer goodsStatus,
                                      @Param("startNum") Integer startNum,
                                      @Param("pageSize") Integer pageSize);

    long countByCondition(@Param("tagIdList") List<Integer> tagIdList,
                          @Param("universityId") Integer universityId,
                          @Param("majorId") Integer majorId,
                          @Param("categoryIdList") List<Integer> categoryIdList,
                          @Param("goodsStatus") Integer goodsStatus);
}
