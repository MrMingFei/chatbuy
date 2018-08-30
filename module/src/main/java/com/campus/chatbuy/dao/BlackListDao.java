package com.campus.chatbuy.dao;

import com.campus.chatbuy.model.BlackList;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by jinku on 2017/11/21.
 */
@Component
public interface BlackListDao {

    int insert(BlackList blackList);

    BlackList checkBlack(@Param("userId") Long userId, @Param("blackUserId") Long blackUserId);

    List<BlackList> checkBlackList(@Param("userId") Long userId,
                                   @Param("blackUserIdList") Set<Long> blackUserIdList);

    int addBlack(@Param("userId") Long userId, @Param("blackUserId") Long blackUserId,
                 @Param("updateTime") Date updateTime);

    int deleteBlack(@Param("userId") Long userId, @Param("blackUserId") Long blackUserId,
                 @Param("updateTime") Date updateTime);
}
