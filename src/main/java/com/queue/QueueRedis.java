package com.queue;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class QueueRedis {

    private JedisPool jedisPool;

    public void init() {
        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPool = new JedisPool(jedisPoolConfig, "localhost", 6379);
    }

    public String rightPopAndLeftPush(String srcKey, String dstKey) {
        final Jedis jedis = jedisPool.getResource();
        final String res;
        try {
            res = jedis.rpoplpush(srcKey, dstKey);
        } finally {
            jedis.close();
        }
        return res;
    }

    public void lRemove(String key, long count, String value) {
        final Jedis jedis = jedisPool.getResource();
        try {
            jedis.lrem(key, count, value);
        } finally {
            jedis.close();
        }
    }
}
