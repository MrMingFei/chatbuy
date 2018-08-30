package com.campus.chatbuy.util;

/**
 * Created by jinku on 17-7-22.
 */
public class ThreadLocalUtil {

    public static final ThreadLocal userIdLocal = new ThreadLocal();


    public static void setUserId(Long userId) {
        userIdLocal.set(userId);
    }

    public static long getUserId() {
        return (Long) userIdLocal.get();
    }

    public static void clearAll(){
        userIdLocal.remove();
    }
}
