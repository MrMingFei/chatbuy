package com.campus.chatbuy.util;

import com.campus.chatbuy.service.RedisService;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * Created by jinku on 2017/11/9.
 * <p/>
 * redis锁工具类
 */
public class LockUtil {

    private static final Logger logger = Logger.getLogger(LockUtil.class);

    private static RedisService redisService = (RedisService) SpringContextUtil.getBean("redisService");

    /**
     * 独占锁获取,需要设置超时时间
     *
     * @param key     : 要锁的key
     * @param timeout : 超时时间:单位秒
     * @return 获取锁的value值, 释放的时候需要传过来
     * @throws Exception
     */
    public static String tryLock(String key, int timeout) throws Exception {
        if (StringUtil.isEmpty(key)) {
            return null;
        }
        int maxTimes = (timeout * 1000) / 100;
        logger.info("LockUtil tryLock lock maxTimes [" + maxTimes + "]");
        int times = 0;
        while (times <= maxTimes) {
            String timeStr = acquireLock(key, timeout);
            if (StringUtil.isNotEmpty(timeStr)) {
                return timeStr;
            }
            String time = redisService.get(key);
            if (StringUtil.isNotEmpty(time)) {
                long timestamp = Long.parseLong(time);
                if (new Date().getTime() - timestamp > 1000 * (timeout + 1)) {
                    // 已过期,删除
                    logger.warn("LockUtil tryLock lock is over date so delete; key [" + key + "]");
                    redisService.delete(key);
                }
            }
            Thread.sleep(100);
            times++;
        }
        return null;
    }

    /**
     * 独占锁释放
     *
     * @param key       : 锁对应的key
     * @param lockValue :锁对应的value值
     * @return true : 释放成功
     */
    public static boolean releaseLock(String key, String lockValue) {
        if (StringUtil.isEmpty(key)) {
            return false;
        }

        String timeStr = redisService.get(key);

        if (timeStr == null) {
            logger.info("LockUtil releaseLock success; key [" + key + "] not exist");
            return true;
        }

        if (!timeStr.equals(lockValue)) {
            return false;
        }

        boolean flag = redisService.delete(key) > 0 ? true : false;
        if (!flag) {
            logger.warn("LockUtil releaseLock fail; key [" + key + "] lockValue [" + lockValue + "]");
        }
        return flag;
    }

    private static String acquireLock(String key, int expireTime) {
        String timeStr = String.valueOf(new Date().getTime());
        try {
            if (redisService.setnx(key, timeStr) == 1L) {
                /* 设置成功，并且过期时间 */
                redisService.expire(key, expireTime);
                logger.info("LockUtil acquireLock success; key [" + key + "] timestamp [" + timeStr + "]");
                return timeStr;
            }
        } catch (Exception e) {
            logger.error("LockUtil acquireLock success; key [" + key + "] timestamp [" + timeStr + "]", e);
        }
        return null;
    }
}
