package com.cloud.wang.common.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 基于Jedis的Redis工具类
 *
 * @author wang
 * @since 2022-05-03
 */
public class JedisUtil {
    private static Logger logger = LoggerFactory.getLogger(JedisUtil.class);
    /**
     * 默认过期时间,单位/秒, 60*60*2=2H, 两小时
     */
    private static final int DEFAULT_EXPIRE_TIME = 7200;

    //集群下使用
    private static Set<HostAndPort> redisSet;
    public static void init(Set<HostAndPort> redisSet) {
        JedisUtil.redisSet = redisSet;
    }

    //单机
    private static String host;
    private static int port;
    public static void init(String host , int port) {
        JedisUtil.host = host;
        JedisUtil.port = port;
    }

    // ------------------------ ShardedJedisPool ------------------------
    /**
     * 方式01: Redis单节点 + Jedis单例 : Redis单节点压力过重, Jedis单例存在并发瓶颈 》》不可用于线上
     * new Jedis("127.0.0.1", 6379).get("cache_key");
     * 方式02: RedisR单节点 + JedisPool单节点连接池 》》 Redis单节点压力过重，负载和容灾比较差
     * new JedisPool(new JedisPoolConfig(), "127.0.0.1", 6379, 10000).getResource().get("cache_key");
     * 方式03: Redis集群(通过client端集群,一致性哈希方式实现) + Jedis多节点连接池 》》Redis集群,负载和容灾较好, ShardedJedisPool一致性哈希分片,读写均匀，动态扩充
     * new ShardedJedisPool(new JedisPoolConfig(), new LinkedList<JedisShardInfo>());
     */

    private static JedisPool pool;
    private static ReentrantLock INSTANCE_INIT_LOCL = new ReentrantLock(false);

    /**
     * 获取ShardedJedis实例
     *
     * @return
     */
    private static int timeout = 2;

    private static Jedis getInstance() {
        if (pool == null) {
            try {
                if (INSTANCE_INIT_LOCL.tryLock(timeout, TimeUnit.SECONDS)) {
                    try {
                        if (pool == null) {
                            // JedisPoolConfig
                            GenericObjectPoolConfig config = new GenericObjectPoolConfig();
                            // 最大连接数, 默认8个
                            config.setMaxTotal(200);
                            // 最大空闲连接数, 默认8个
                            config.setMaxIdle(50);
                            // 设置最小空闲数
                            config.setMinIdle(8);
                            // 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
                            config.setMaxWaitMillis(10000);
                            // 在获取连接的时候检查有效性, 默认false
                            config.setTestOnBorrow(true);
                            // 调用returnObject方法时，是否进行有效检查
                            config.setTestOnReturn(true);
                            // Idle时进行连接扫描
                            config.setTestWhileIdle(true);
                            //表示idle object evitor两次扫描之间要sleep的毫秒数
                            config.setTimeBetweenEvictionRunsMillis(30000);
                            //表示idle object evitor每次扫描的最多的对象数
                            config.setNumTestsPerEvictionRun(10);
                            //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
                            config.setMinEvictableIdleTimeMillis(60000);

                            //这里也支持指定Redis密码，可以看一下JedisPool的不同构造方法。
                            pool = new JedisPool(config , host , port , false);
                            logger.info(">>>>>>>>>>> JedisUtil.ShardedJedisPool init success.");
                        }

                    } finally {
                        INSTANCE_INIT_LOCL.unlock();
                    }
                }

            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
        Jedis jedis = pool.getResource();
        //单机Redis，可以根据业务需求手动选择Redis库。但是集群环境下用JedisCluster实现，此时只支持db0。
        jedis.select(1);
        return jedis;
    }

    // ------------------------ serialize and unserialize ------------------------

    /**
     * 将对象-->byte[] (由于jedis中不支持直接存储object所以转换成byte[]存入)
     *
     * @param object
     * @return
     */
    private static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            // 序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
            logger.error("{}", e);
        } finally {
            try {
                oos.close();
                baos.close();
            } catch (IOException e) {
                logger.error("{}", e);
            }
        }
        return null;
    }

    /**
     * 将byte[] -->Object
     *
     * @param bytes
     * @return
     */
    private static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            // 反序列化
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            logger.error("{}", e);
        } finally {
            try {
                bais.close();
            } catch (IOException e) {
                logger.error("{}", e);
            }
        }
        return null;
    }

    // ------------------------ jedis util ------------------------
    /**
     * 存储简单的字符串或者是Object 因为jedis没有分装直接存储Object的方法，所以在存储对象需斟酌下
     * 存储对象的字段是不是非常多而且是不是每个字段都用到，如果是的话那建议直接存储对象，
     * 否则建议用集合的方式存储，因为redis可以针对集合进行日常的操作很方便而且还可以节省空间
     */

    /**
     * Set String
     *
     * @param key
     * @param value
     * @param seconds 存活时间,单位/秒
     * @return
     */
    public static String setStringValue(String key, String value, int seconds) {
        String result = null;
        Jedis cluster = getInstance();
        try {
            result = cluster.setex(key, seconds, value);
        } catch (Exception e) {
            logger.info("{}", e);
        } finally {
            cluster.close();
        }
        return result;
    }

    /**
     * Set String (默认存活时间, 2H)
     *
     * @param key
     * @param value
     * @return
     */
    public static String setStringValue(String key, String value) {
        return setStringValue(key, value, DEFAULT_EXPIRE_TIME);
    }

    /**
     * Set Object
     *
     * @param key
     * @param obj
     * @param seconds 存活时间,单位/秒
     */
    public static String setObjectValue(String key, Object obj, int seconds) {
        String result = null;
        Jedis cluster = getInstance();
        try {
            result = cluster.setex(key.getBytes(), seconds, serialize(obj));
        } catch (Exception e) {
            logger.info("{}", e);
        } finally {
            cluster.close();
        }
        return result;
    }

    /**
     * Set Object (默认存活时间, 2H)
     *
     * @param key
     * @param obj
     * @return
     */
    public static String setObjectValue(String key, Object obj) {
        return setObjectValue(key, obj, DEFAULT_EXPIRE_TIME);
    }

    /**
     * Get String
     *
     * @param key
     * @return
     */
    public static String getStringValue(String key) {
        String value = null;
        Jedis cluster = getInstance();
        try {
            value = cluster.get(key);
        } catch (Exception e) {
            logger.info("", e);
        } finally {
            cluster.close();
        }
        return value;
    }

    /**
     * Get Object
     *
     * @param key
     * @return
     */
    public static Object getObjectValue(String key) {
        Object obj = null;
        Jedis cluster = getInstance();
        int index = key.indexOf("forever-");
        try {
            byte[] bytes = cluster.get(key.getBytes());
            if (bytes != null && bytes.length > 0) {
                obj = unserialize(bytes);
            }
        } catch (Exception e) {
            logger.info("", e);
        } finally {
            cluster.close();
        }
        //重置过期时间
        if(index <0){
            expire(key, DEFAULT_EXPIRE_TIME);
        }
        return obj;
    }

    /**
     * Delete
     *
     * @param key
     * @return Integer reply, specifically:
     * an integer greater than 0 if one or more keys were removed
     * 0 if none of the specified key existed
     */
    public static Long del(String key) {
        Long result = null;
        Jedis cluster = getInstance();
        try {
            result = cluster.del(key);
        } catch (Exception e) {
            logger.info("{}", e);
        } finally {
            cluster.close();
        }
        return result;
    }

    /**
     * incrBy	value值加i
     *
     * @param key
     * @param i
     * @return new value after incr
     */
    public static Long incrBy(String key, int i) {
        Long result = null;
        Jedis cluster = getInstance();
        try {
            result = cluster.incrBy(key, i);
        } catch (Exception e) {
            logger.info("{}", e);
        } finally {
            cluster.close();
        }
        return result;
    }

    /**
     * exists
     *
     * @param key
     * @return Boolean reply, true if the key exists, otherwise false
     */
    public static Boolean exists(String key) {
        Boolean result = null;
        Jedis cluster = getInstance();
        try {
            result = cluster.exists(key);
        } catch (Exception e) {
            logger.info("{}", e);
        } finally {
            cluster.close();
        }
        return result;
    }

    /**
     * expire	重置存活时间
     *
     * @param key
     * @param seconds 存活时间,单位/秒
     * @return Integer reply, specifically:
     * 1: the timeout was set.
     * 0: the timeout was not set since the key already has an associated timeout (versions lt 2.1.3), or the key does not exist.
     */
    public static Long expire(String key, int seconds) {
        Long result = null;
        Jedis cluster = getInstance();
        try {
            result = cluster.expire(key, seconds);
        } catch (Exception e) {
            logger.info("{}", e);
        } finally {
            cluster.close();
        }
        return result;
    }
    /**
     * expireAt		设置存活截止时间
     *
     * @param key
     * @param unixTime 存活截止时间戳
     * @return
     */
    public static Long expireAt(String key, long unixTime) {
        Long result = null;
        Jedis cluster = getInstance();
        try {
            result = cluster.expireAt(key, unixTime);
        } catch (Exception e) {
            logger.info("{}", e);
        } finally {
            cluster.close();
        }
        return result;
    }


    /**
     * Set Object(永久有效)
     *
     * @param key
     * @param obj
     */
    public static String setForeverObjectValue(String key, Object obj) {
        String result = null;
        Jedis cluster = getInstance();
        try {
            result = cluster.set(key.getBytes(),serialize(obj));
        } catch (Exception e) {
            logger.info("{}", e);
        } finally {
            cluster.close();
        }
        return result;
    }
}