package com.campus.chatbuy.service;

import java.util.List;

public interface RedisService {

    String set(String key, String value);

    String get(String key);

    Long delete(String key);

    String set(String key, int seconds, String value);

    String getSet(String key, String value);

    boolean exists(String key);

    long incr(String key);

    long expire(String key, int second);

    Long setnx(String key, String value);

    Long lpush(String key, String value);

    String lpop(String key);

    String rpop(String key);

    Long llen(String key);

    List<String> lrange(String key, int start, int stop);

    String lindex(String key, int index);

}
