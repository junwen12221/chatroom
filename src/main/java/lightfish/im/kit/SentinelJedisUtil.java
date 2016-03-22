package lightfish.im.kit;

import lightfish.im.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;


public class SentinelJedisUtil {
    private static Logger logger = LoggerFactory.getLogger(SentinelJedisUtil.class);
    private static SentinelJedisUtil sentinelJedisUtil;
    @Resource
    private  JedisPool  jedisSentinelPool;

    @PostConstruct
    public  void init() {
        sentinelJedisUtil = this;
        jedisSentinelPool = new JedisPool(new JedisPoolConfig(),"127.0.0.1", 6379);
    }

    public static Jedis getJedis(){
        return sentinelJedisUtil.jedisSentinelPool.getResource();
    }

    /**
     * 设置一个key的过期时间（单位：秒）
     *
     * @param key     key值
     * @param seconds 多少秒后过期
     * @return 1：设置了过期时间 0：没有设置过期时间/不能设置过期时间
     */
    public static long expire(String key, int seconds) {
        if (key == null || key.equals("")) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            return jedis.expire(key, seconds);
        } catch (Exception ex) {
            logger.error("EXPIRE error[key=" + key + " seconds=" + seconds
                    + "]" + ex.getMessage(), ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return 0;
    }

    /**
     * 设置一个key在某个时间点过期
     *
     * @param key           key值
     * @param unixTimestamp unix时间戳，从1970-01-01 00:00:00开始到现在的秒数
     * @return 1：设置了过期时间 0：没有设置过期时间/不能设置过期时间
     */
    public static long expireAt(String key, int unixTimestamp) {
        if (key == null || key.equals("")) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            return jedis.expireAt(key, unixTimestamp);
        } catch (Exception ex) {
            logger.error("EXPIRE error[key=" + key + " unixTimestamp="
                    + unixTimestamp + "]" + ex.getMessage(), ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return 0;
    }

    /**
     * 截断一个List
     *
     * @param key   列表key
     * @param start 开始位置 从0开始
     * @param end   结束位置
     * @return 状态码
     */
    public static String trimList(String key, long start, long end) {
        if (key == null || key.equals("")) {
            return "-";
        }
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            return jedis.ltrim(key, start, end);
        } catch (Exception ex) {
            logger.error("LTRIM 出错[key=" + key + " start=" + start + " end="
                    + end + "]" + ex.getMessage(), ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return "-";
    }

    /**
     * 检查Set长度
     *
     * @param key
     * @return
     */
    public static long countSet(String key) {
        if (key == null) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            return jedis.scard(key);
        } catch (Exception ex) {
            logger.error("countSet error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return 0;
    }

    /**
     * 添加到Set中（同时设置过期时间）
     *
     * @param key     key值
     * @param seconds 过期时间 单位s
     * @param value
     * @return
     */
    public static boolean addSet(String key, int seconds, String... value) {
        boolean result = addSet(key, value);
        if (result) {
            long i = expire(key, seconds);
            return i == 1;
        }
        return false;
    }

    /**
     * 添加到Set中
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean addSet(String key, String... value) {
        if (key == null || value == null) {
            return false;
        }
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            jedis.sadd(key, value);
            return true;
        } catch (Exception ex) {
            logger.error("setList error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return false;
    }

    /**
     * @param key
     * @param value
     * @return 判断值是否包含在set中
     */
    public static boolean containsInSet(String key, String value) {
        if (key == null || value == null) {
            return false;
        }
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception ex) {
            logger.error("setList error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return false;
    }

    /**
     * 获取Set
     *
     * @param key
     * @return
     */
    public static Set<String> getSet(String key) {
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            return jedis.smembers(key);
        } catch (Exception ex) {
            logger.error("getList error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    /**
     * 从set中删除value
     *
     * @param key
     * @return
     */
    public static boolean removeSetValue(String key, String... value) {
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            jedis.srem(key, value);
            return true;
        } catch (Exception ex) {
            logger.error("getList error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return false;
    }

    /**
     * 从list中删除value 默认count 1
     *
     * @param key
     * @param values 值list
     * @return
     */
    public static int removeListValue(String key, List<String> values) {
        return removeListValue(key, 1, values);
    }

    /**
     * 从list中删除value
     *
     * @param key
     * @param count
     * @param values 值list
     * @return
     */
    public static int removeListValue(String key, long count,
                                      List<String> values) {
        int result = 0;
        if (values != null && values.size() > 0) {
            for (String value : values) {
                if (removeListValue(key, count, value)) {
                    result++;
                }
            }
        }
        return result;
    }

    /**
     * 从list中删除value
     *
     * @param key
     * @param count 要删除个数
     * @param value
     * @return
     */
    public static boolean removeListValue(String key, long count, String value) {
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            jedis.lrem(key, count, value);
            return true;
        } catch (Exception ex) {
            logger.error("getList error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return false;
    }

    /**
     * 截取List
     *
     * @param key
     * @param start 起始位置
     * @param end   结束位置
     * @return
     */
    public static List<String> rangeList(String key, long start, long end) {
        if (key == null || key.equals("")) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            return jedis.lrange(key, start, end);
        } catch (Exception ex) {
            logger.error("rangeList 出错[key=" + key + " start=" + start
                    + " end=" + end + "]" + ex.getMessage(), ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    /*
     * 检查List长度
     *
     * @param key
     * @return
    */
    public static long countList(String key) {
        if (key == null) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            return jedis.llen(key);
        } catch (Exception ex) {
            logger.error("countList error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return 0;
    }

    /*
     * 添加到List中（同时设置过期时间）
     *
     * @param key
     *            key值
     * @param seconds
     *            过期时间 单位s
     * @param value
     * @return
     */
    public static boolean addList(String key, int seconds, String... value) {
        boolean result = addList(key, value);
        if (result) {
            long i = expire(key, seconds);
            return i == 1;
        }
        return false;
    }

    /**
     * 添加到List
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean addList(String key, String... value) {
        if (key == null || value == null) {
            return false;
        }
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            jedis.lpush(key, value);
            return true;
        } catch (Exception ex) {
            logger.error("setList error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return false;
    }

    /**
     * 添加到List(只新增)
     *
     * @param key
     * @param list
     * @return
     */
    public static boolean addList(String key, List<String> list) {
        if (key == null || list == null || list.size() == 0) {
            return false;
        }
        for (String value : list) {
            addList(key, value);
        }
        return true;
    }

    /**
     * 获取List
     *
     * @param key
     * @return
     */
    public static List<String> getList(String key) {
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            return jedis.lrange(key, 0, -1);
        } catch (Exception ex) {
            logger.error("getList error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    /**
     * 设置HashSet对象
     *
     * @param domain 域名
     * @param key    键值
     * @param value  Json String or String value
     * @return
     */
    public static boolean setHSet(String domain, String key, String value) {
        if (value == null)
            return false;
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            jedis.hset(domain, key, value);
            return true;
        } catch (Exception ex) {
            logger.error("setHSet error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return false;
    }

    /**
     * 获得HashSet对象
     *
     * @param domain 域名
     * @param key    键值
     * @return Json String or String value
     */
    public static String getHSet(String domain, String key) {
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            return jedis.hget(domain, key);
        } catch (Exception ex) {
            logger.error("getHSet error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    /**
     * 删除HashSet对象
     *
     * @param domain 域名
     * @param key    键值
     * @return 删除的记录数
     */
    public static long delHSet(String domain, String key) {
        Jedis jedis = null;
        long count = 0;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            count = jedis.hdel(domain, key);
        } catch (Exception ex) {
            logger.error("delHSet error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return count;
    }

    /**
     * 删除HashSet对象
     *
     * @param domain 域名
     * @param key    键值
     * @return 删除的记录数
     */
    public static long delHSet(String domain, String... key) {
        Jedis jedis = null;
        long count = 0;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            count = jedis.hdel(domain, key);
        } catch (Exception ex) {
            logger.error("delHSet error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return count;
    }

    /*
     * 判断key是否存在
     *
     * @param domain
     *            域名
     * @param key
     *            键值
     * @return
     */
    public static boolean existsHSet(String domain, String key) {
        Jedis jedis = null;
        boolean isExist = false;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            isExist = jedis.hexists(domain, key);
        } catch (Exception ex) {
            logger.error("existsHSet error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return isExist;
    }

    /**
     * 全局扫描hset
     *
     * @param match field匹配模式
     * @return
     */
    public static List<Map.Entry<String, String>> scanHSet(String domain,
                                                           String match) {
        Jedis jedis = null;
        try {
            int cursor = 0;
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            ScanParams scanParams = new ScanParams();
            scanParams.match(match);
            ScanResult<Map.Entry<String, String>> scanResult;
            List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>();
            do {
                scanResult = jedis.hscan(domain, String.valueOf(cursor),
                        scanParams);
                list.addAll(scanResult.getResult());
                cursor = Integer.parseInt(scanResult.getStringCursor());
            } while (cursor > 0);
            return list;
        } catch (Exception ex) {
            logger.error("scanHSet error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    /**
     * 全局扫描hset
     *
     * @param match field匹配模式
     * @return
     */
    public static Set<String> scan(
            String match) {
        Jedis jedis = null;
        try {
            int cursor = 0;
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            ScanParams scanParams = new ScanParams();
            scanParams.match(match);
            ScanResult<String> scanResult;
            Set<String> retSet = new HashSet<String>();
            do {
                scanResult = jedis.scan(String.valueOf(cursor),
                        scanParams);
                retSet.addAll(scanResult.getResult());
                cursor = Integer.parseInt(scanResult.getStringCursor());
            } while (cursor > 0);
            return retSet;
        } catch (Exception ex) {
            logger.error("scanHSet error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    /**
     * 返回 domain 指定的哈希集中所有字段的value值
     *
     * @param domain
     * @return
     */
    public static List<String> hvals(String domain) {
        Jedis jedis = null;
        List<String> retList = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            retList = jedis.hvals(domain);
        } catch (Exception ex) {
            logger.error("hvals error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return retList;
    }

    /**
     * 返回 domain 指定的哈希集中所有字段的key值
     *
     * @param domain
     * @return
     */
    public static Set<String> hkeys(String domain) {
        Jedis jedis = null;
        Set<String> retList = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            retList = jedis.hkeys(domain);
        } catch (Exception ex) {
            logger.error("hkeys error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return retList;
    }

    /**
     * 返回 domain 指定的哈希key值总数
     *
     * @param domain
     * @return
     */
    public static long lenHset(String domain) {
        Jedis jedis = null;
        long retList = 0;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            retList = jedis.hlen(domain);
        } catch (Exception ex) {
            logger.error("hkeys error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return retList;
    }

    /**
     * 设置排序集合
     *
     * @param key
     * @param score
     * @param value
     * @return
     */
    public static boolean setSortedSet(String key, long score, String value) {
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            jedis.zadd(key, score, value);
            return true;
        } catch (Exception ex) {
            logger.error("setSortedSet error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return false;
    }

    /**
     * 获得排序集合
     *
     * @param key
     * @param startScore
     * @param endScore
     * @param orderByDesc
     * @return
     */
    public static Set<String> getSoredSet(String key, long startScore,
                                          long endScore, boolean orderByDesc) {
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            if (orderByDesc) {
                return jedis.zrevrangeByScore(key, endScore, startScore);
            } else {
                return jedis.zrangeByScore(key, startScore, endScore);
            }
        } catch (Exception ex) {
            logger.error("getSoredSet error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    /**
     * 计算排序长度
     *
     * @param key
     * @param startScore
     * @param endScore
     * @return
     */
    public static long countSoredSet(String key, long startScore, long endScore) {
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            Long count = jedis.zcount(key, startScore, endScore);
            return count == null ? 0L : count;
        } catch (Exception ex) {
            logger.error("countSoredSet error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return 0L;
    }

    /**
     * 删除排序集合
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean delSortedSet(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            long count = jedis.zrem(key, value);
            return count > 0;
        } catch (Exception ex) {
            logger.error("delSortedSet error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return false;
    }

    /**
     * 获得排序集合
     *
     * @param key
     * @param startRange
     * @param endRange
     * @param orderByDesc
     * @return
     */
    public static Set<String> getSoredSetByRange(String key, int startRange,
                                                 int endRange, boolean orderByDesc) {
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            if (orderByDesc) {
                return jedis.zrevrange(key, startRange, endRange);
            } else {
                return jedis.zrange(key, startRange, endRange);
            }
        } catch (Exception ex) {
            logger.error("getSoredSetByRange error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    /**
     * 获得排序打分
     *
     * @param key
     * @return
     */
    public static Double getScore(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            return jedis.zscore(key, member);
        } catch (Exception ex) {
            logger.error("getSoredSet error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public static boolean set(String key, String value, int second) {
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            jedis.setex(key, second, value);
            return true;
        } catch (Exception ex) {
            logger.error("set error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return false;
    }

    public static boolean set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            jedis.set(key, value);
            return true;
        } catch (Exception ex) {
            logger.error("set error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return false;
    }

    public static String get(String key, String defaultValue) {
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            return jedis.get(key) == null ? defaultValue : jedis
                    .get(key);
        } catch (Exception ex) {
            logger.error("get error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return defaultValue;
    }

    public static String get(String key) {
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            return jedis.get(key);
        } catch (Exception ex) {
            logger.error("get error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return null;
    }

    public static boolean del(String key) {
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            jedis.del(key);
            return true;
        } catch (Exception ex) {
            logger.error("del error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return false;
    }

    public static long incr(String key) {
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            return jedis.incr(key);
        } catch (Exception ex) {
            logger.error("incr error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return 0;
    }

    public static long decr(String key) {
        Jedis jedis = null;
        try {
            jedis = sentinelJedisUtil.jedisSentinelPool.getResource();
            return jedis.decr(key);
        } catch (Exception ex) {
            logger.error("incr error.", ex);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return 0;
    }

    private static void returnBrokenResource(Jedis jedis) {
        try {
            sentinelJedisUtil.jedisSentinelPool.returnBrokenResource(jedis);
        } catch (Exception e) {
            logger.error("returnBrokenResource error.", e);
        }
    }

    private static void returnResource(Jedis jedis) {
        try {
            sentinelJedisUtil.jedisSentinelPool.returnResource(jedis);
        } catch (Exception e) {
            logger.error("returnResource error.", e);
        }
    }
}