package com.campus.chatbuy.service.impl;

import com.campus.chatbuy.constans.ConfigConstants;
import com.campus.chatbuy.service.RedisService;
import com.campus.chatbuy.util.StringUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.util.List;

@Service("redisService")
public class NativeJedis200ServiceImpl extends AbstractService implements RedisService {

    private static final Logger log = Logger.getLogger(NativeJedis200ServiceImpl.class);

    private JedisPool pool;
    private int database;

    @Override
    public Long setnx(String key, String value) {
        Jedis redis = this.getJedis();
        key = getKey(key);
        try {
            return redis.setnx(key, value);
        } finally {
            pool.returnResource(redis);
        }
    }

    @Override
    public Long lpush(String key, String value) {
        Jedis redis = this.getJedis();
        key = getKey(key);
        try {
            return redis.lpush(key, value);
        } finally {
            pool.returnResource(redis);
        }
    }

    @Override
    public String lpop(String key) {
        Jedis redis = this.getJedis();
        key = getKey(key);
        try {
            return redis.lpop(key);
        } finally {
            pool.returnResource(redis);
        }
    }

    @Override
    public String rpop(String key) {
        Jedis redis = this.getJedis();
        key = getKey(key);
        try {
            return redis.rpop(key);
        } finally {
            pool.returnResource(redis);
        }
    }

    @Override
    public Long llen(String key) {
        Jedis redis = this.getJedis();
        key = getKey(key);
        try {
            return redis.llen(key);
        } finally {
            pool.returnResource(redis);
        }
    }

    @Override
    public List<String> lrange(String key, int start, int stop) {
        Jedis redis = this.getJedis();
        key = getKey(key);
        try {
            return redis.lrange(key, start, stop);
        } finally {
            pool.returnResource(redis);
        }
    }

    @Override
    public String lindex(String key, int index) {
        Jedis redis = this.getJedis();
        key = getKey(key);
        try {
            return redis.lindex(key, index);
        } finally {
            pool.returnResource(redis);
        }
    }

    @Override
    public String set(String key, String value) {
        Jedis redis = this.getJedis();
        key = getKey(key);
        try {
            return redis.set(key, value);
        } finally {
            pool.returnResource(redis);
        }
    }

    /*
     * 添加超时参数
     */
    @Override
    public String set(String key, int seconds, String value) {
        Jedis redis = this.getJedis();
        key = getKey(key);
        try {
            return redis.setex(key, seconds, value);
        } finally {
            pool.returnResource(redis);
        }
    }

    @Override
    public String get(String key) {
        Jedis redis = this.getJedis();
        key = getKey(key);
        try {
            return redis.get(key);
        } finally {
            pool.returnResource(redis);
        }
    }

    @Override
    public Long delete(String key) {
        Jedis redis = this.getJedis();
        key = getKey(key);
        try {
            return redis.del(key);
        } finally {
            pool.returnResource(redis);
        }
    }

    public String hset(String key, String columns, String value) {
        Jedis redis = this.getJedis();
        key = getKey(key);
        try {
            return "" + redis.hset(key, columns, value);
        } finally {
            pool.returnResource(redis);
        }
    }

    public String hget(String key, String columns) {
        Jedis redis = this.getJedis();
        key = getKey(key);
        try {
            return redis.hget(key, columns);
        } finally {
            pool.returnResource(redis);
        }
    }

    @Override
    public boolean configure(ServiceConfig[] scs) {
        if (scs.length == 1) {
            String ip = scs[0].getProps().getProperty("IP");
            int port = NumberUtils.toInt(scs[0].getProps().getProperty("port"), Protocol.DEFAULT_PORT);
            String password = scs[0].getProps().getProperty("redisPass");
            int maxConnections = NumberUtils.toInt(scs[0].getProps().getProperty("maxConnections"), 200);
            int redisConnectionTimedOut = NumberUtils
                    .toInt(scs[0].getProps().getProperty("redisConnectionTimedOut"), Protocol.DEFAULT_TIMEOUT);

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(maxConnections);
            log.info("redis Config , IP = " + ip + " ," + port);

            JedisPool jPool = null;
            if (StringUtil.isNotEmpty(password)) {
                jPool = new JedisPool(config, ip, port, redisConnectionTimedOut, password);
            } else {
                jPool = new JedisPool(config, ip, port, redisConnectionTimedOut);
            }

            this.database = Integer.parseInt(scs[0].getProps().getProperty("database"));
            this.pool = jPool;

            return true;
        } else {
            log.fatal("redis service start failed. too many configurations.");
        }

        return false;
    }

    @Override
    public boolean isAvailable() {
        return pool != null;
    }

    @Override
    public void startup() {
        log.info("Redis Service StartUp");
    }

    @Override
    public void shutdown() {
        if (pool != null) {
            pool.destroy();
            pool = null;
        }
    }

    @Override
    public String getSet(String key, String value) {
        Jedis redis = this.getJedis();
        key = getKey(key);
        try {
            return redis.getSet(key, value);
        } finally {
            pool.returnResource(redis);
        }
    }

    @Override
    public boolean exists(String key) {
        Jedis redis = this.getJedis();
        key = getKey(key);
        try {
            return redis.exists(key);
        } finally {
            pool.returnResource(redis);
        }
    }

    @Override
    public long incr(String key) {
        Jedis redis = this.getJedis();
        key = getKey(key);
        try {
            return redis.incr(key);
        } finally {
            pool.returnResource(redis);
        }
    }

    @Override
    public long expire(String key, int second) {
        Jedis redis = this.getJedis();
        key = getKey(key);
        try {
            return redis.expire(key, second);
        } finally {
            pool.returnResource(redis);
        }
    }

    private Jedis getJedis() {
        Jedis j = pool.getResource();
        if (database > 0) {
            j.select(database);
        }
        return j;
    }

    private String getKey(String key) {
        return ConfigConstants.Redis_Key_Prefix + key;
    }

}
