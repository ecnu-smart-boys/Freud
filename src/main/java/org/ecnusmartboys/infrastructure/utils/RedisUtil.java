/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.ecnusmartboys.infrastructure.utils;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类。
 */
@Slf4j
@Component
@SuppressWarnings({"unchecked", "all"})
public class RedisUtil {

    private RedisTemplate<String, Object> redisTemplate;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 向有序集合中添加元素。
     *
     * @param key   键
     * @param value 值
     * @param score 分数
     * @return 是否成功
     */
    public boolean zAdd(String key, Object value, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            log.error("RedisUtil.zAdd error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取有序集合中指定范围的元素。
     *
     * @param key   键
     * @param start 开始位置
     * @param end   结束位置
     * @return 元素列表
     */
    public Set<Object> zRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            log.error("RedisUtil.zRange error: {}", e.getMessage());
            return Sets.newHashSet();
        }
    }

    /**
     * 获取有序集合中指定范围的元素和分数。
     *
     * @param key   键
     * @param start 开始位置
     * @param end   结束位置
     * @return 元素和分数的映射
     */
    public Map<Object, Double> zRangeWithScores(String key, long start, long end) {
        try {
            Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<Object>> tuples =
                    redisTemplate.opsForZSet().rangeWithScores(key, start, end);
            Map<Object, Double> result = new HashMap<>();
            for (org.springframework.data.redis.core.ZSetOperations.TypedTuple<Object> tuple : tuples) {
                result.put(tuple.getValue(), tuple.getScore());
            }
            return result;
        } catch (Exception e) {
            log.error("RedisUtil.zRangeWithScores error: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * 获取有序集合中指定元素的排名。
     *
     * @param key   键
     * @param value 元素
     * @return 排名
     */
    public Long zRank(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().rank(key, value);
        } catch (Exception e) {
            log.error("RedisUtil.zRank error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取有序集合中指定元素的分数。
     *
     * @param key   键
     * @param value 元素
     * @return 分数
     */
    public Double zScore(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().score(key, value);
        } catch (Exception e) {
            log.error("RedisUtil.zScore error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 删除有序集合中指定元素。
     *
     * @param key    键
     * @param values 元素列表
     * @return 删除的元素数量
     */
    public Long zRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForZSet().remove(key, values);
        } catch (Exception e) {
            log.error("RedisUtil.zRemove error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取有序集合中元素的数量。
     *
     * @param key 键
     * @return 元素数量
     */
    public Long zSize(String key) {
        try {
            return redisTemplate.opsForZSet().size(key);
        } catch (Exception e) {
            log.error("RedisUtil.zSize error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取有序集合中指定分数范围内的元素数量。
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 元素数量
     */
    public Long zCount(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().count(key, min, max);
        } catch (Exception e) {
            log.error("RedisUtil.zCount error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从有序集合中删除指定排名范围内的元素。
     *
     * @param key   键
     * @param start 开始排名
     * @param end   结束排名
     * @return 删除的元素数量
     */
    public Long zRemoveRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().removeRange(key, start, end);
        } catch (Exception e) {
            log.error("RedisUtil.zRemoveRange error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从有序集合中删除指定分数范围内的元素。
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 删除的元素数量
     */
    public Long zRemoveRangeByScore(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
        } catch (Exception e) {
            log.error("RedisUtil.zRemoveRangeByScore error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取有序集合中指定元素的分数，如果元素不存在则添加元素并返回初始分数。
     *
     * @param key   键
     * @param value 元素
     * @param score 初始分数
     * @return 分数
     */
    public Double zIncrementScore(String key, Object value, double score) {
        try {
            return redisTemplate.opsForZSet().incrementScore(key, value, score);
        } catch (Exception e) {
            log.error("RedisUtil.zIncrementScore error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取有序集合中指定元素的分数，如果元素不存在则添加元素并返回初始分数。
     *
     * @param key   键
     * @param value 元素
     * @param score 初始分数
     * @param time  过期时间
     * @return 分数
     */
    public Double zIncrementScore(String key, Object value, double score, long time) {
        try {
            Double result = redisTemplate.opsForZSet().incrementScore(key, value, score);
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return result;
        } catch (Exception e) {
            log.error("RedisUtil.zIncrementScore error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取有序集合中指定元素的分数，如果元素不存在则添加元素并返回初始分数。
     *
     * @param key   键
     * @param value 元素
     * @param score 初始分数
     * @param time  过期时间
     * @return 分数
     */
    public Double zIncrementScore(String key, Object value, double score, Date time) {
        try {
            Double result = redisTemplate.opsForZSet().incrementScore(key, value, score);
            if (time != null) {
                redisTemplate.expireAt(key, time);
            }
            return result;
        } catch (Exception e) {
            log.error("RedisUtil.zIncrementScore error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取有序集合中指定元素的排名和分数。
     *
     * @param key   键
     * @param value 元素
     * @return 排名和分数的映射
     */
    public Map<String, Double> zRankWithScore(String key, Object value) {
        try {
            Map<String, Double> map = new HashMap<>();
            Double score = redisTemplate.opsForZSet().score(key, value);
            if (score != null) {
                map.put("score", score);
                Long rank = redisTemplate.opsForZSet().rank(key, value);
                if (rank != null) {
                    map.put("rank", Double.valueOf(rank));
                }
            }
            return map;
        } catch (Exception e) {
            log.error("RedisUtil.zRankWithScore error: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * 获取有序集合中指定分数范围内的元素和分数。
     *
     * @param key   键
     * @param min   最小分数
     * @param max   最大分数
     * @param limit 返回数量限制
     * @return 元素和分数的映射
     */
    public Map<Object, Double> zRangeByScoreWithScores(String key, double min, double max, long limit) {
        try {
            Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<Object>> tuples =
                    redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, 0, limit);
            Map<Object, Double> result = new HashMap<>();
            for (org.springframework.data.redis.core.ZSetOperations.TypedTuple<Object> tuple : tuples) {
                result.put(tuple.getValue(), tuple.getScore());
            }
            return result;
        } catch (Exception e) {
            log.error("RedisUtil.zRangeByScoreWithScores error: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * 获取有序集合中指定分数范围内的元素和分数。
     *
     * @param key   键
     * @param min   最小分数
     * @param max   最大分数
     * @param limit 返回数量限制
     * @param skip  跳过元素数量
     * @return 元素和分数的映射
     */
    public Map<Object, Double> zRangeByScoreWithScores(String key, double min, double max, long limit, long skip) {
        try {
            Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<Object>> tuples =
                    redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, skip, limit);
            Map<Object, Double> result = new HashMap<>();
            for (org.springframework.data.redis.core.ZSetOperations.TypedTuple<Object> tuple : tuples) {
                result.put(tuple.getValue(), tuple.getScore());
            }
            return result;
        } catch (Exception e) {
            log.error("RedisUtil.zRangeByScoreWithScores error: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * 获取有序集合中指定分数范围内的元素数量。
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 元素数量
     */
    public Long zCountByScore(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().count(key, min, max);
        } catch (Exception e) {
            log.error("RedisUtil.zCountByScore error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取有序集合中指定元素的排名和分数。
     *
     * @param key   键
     * @param value 元素
     * @return 排名和分数的映射
     */
    public Map<String, Double> zRevRankWithScore(String key, Object value) {
        try {
            Map<String, Double> map = new HashMap<>();
            Double score = redisTemplate.opsForZSet().score(key, value);
            if (score != null) {
                map.put("score", score);
                Long rank = redisTemplate.opsForZSet().reverseRank(key, value);
                if (rank != null) {
                    map.put("rank", Double.valueOf(rank));
                }
            }
            return map;
        } catch (Exception e) {
            log.error("RedisUtil.zRevRankWithScore error: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * 获取有序集合中指定分数范围内的元素和分数。
     *
     * @param key 键
     * @param min 分数最小值
     * @param max 分数最大值
     * @return 元素和分数的映射
     */
    public Map<Object, Double> zRevRangeByScoreWithScores(String key, double min, double max) {
        try {
            Set<Object> set = redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
            Map<Object, Double> map = new HashMap<>();
            if (set != null && !set.isEmpty()) {
                for (Object obj : set) {
                    Double score = redisTemplate.opsForZSet().score(key, obj);
                    if (score != null) {
                        map.put(obj, score);
                    }
                }
            }
            return map;
        } catch (Exception e) {
            log.error("RedisUtil.zRevRangeByScoreWithScores error: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * 获取有序集合中指定排名范围内的元素。
     *
     * @param key   键
     * @param start 排名起始位置
     * @param end   排名结束位置
     * @return 元素集合
     */
    public Set<Object> zRevRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().reverseRange(key, start, end);
        } catch (Exception e) {
            log.error("RedisUtil.zRevRange error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取有序集合中指定分数范围内的元素。
     *
     * @param key 键
     * @param min 分数最小值
     * @param max 分数最大值
     * @return 元素集合
     */
    public Set<Object> zRevRangeByScore(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
        } catch (Exception e) {
            log.error("RedisUtil.zRevRangeByScore error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取有序集合中指定元素的排名和分数。
     *
     * @param key   键
     * @param value 元素
     * @return 排名和分数的映射
     */
    public Map<String, Double> zRankWithScores(String key, Object value) {
        try {
            Map<String, Double> map = new HashMap<>();
            Double score = redisTemplate.opsForZSet().score(key, value);
            if (score != null) {
                map.put("score", score);
                Long rank = redisTemplate.opsForZSet().rank(key, value);
                if (rank != null) {
                    map.put("rank", Double.valueOf(rank));
                }
            }
            return map;
        } catch (Exception e) {
            log.error("RedisUtil.zRankWithScores error: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * 获取有序集合中指定分数范围内的元素和分数。
     *
     * @param key 键
     * @param min 分数最小值
     * @param max 分数最大值
     * @return 元素和分数的映射
     */
    public Map<Object, Double> zRangeByScoreWithScores(String key, double min, double max) {
        try {
            Set<Object> set = redisTemplate.opsForZSet().rangeByScore(key, min, max);
            Map<Object, Double> map = new HashMap<>();
            if (set != null && !set.isEmpty()) {
                for (Object obj : set) {
                    Double score = redisTemplate.opsForZSet().score(key, obj);
                    if (score != null) {
                        map.put(obj, score);
                    }
                }
            }
            return map;
        } catch (Exception e) {
            log.error("RedisUtil.zRangeByScoreWithScores error: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * 获取有序集合中指定元素的排名。
     *
     * @param key   键
     * @param value 元素
     * @return 排名
     */
    public Long zRevRank(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().reverseRank(key, value);
        } catch (Exception e) {
            log.error("RedisUtil.zRevRank error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取有序集合中指定元素的分数。
     *
     * @param key   键
     * @param value 元素
     * @return 分数
     */
    public Double zRevScore(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().score(key, value);
        } catch (Exception e) {
            log.error("RedisUtil.zRevScore error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取有序集合中元素的数量。
     *
     * @param key 键
     * @return 元素数量
     */
    public Long zRevCard(String key) {
        try {
            return redisTemplate.opsForZSet().zCard(key);
        } catch (Exception e) {
            log.error("RedisUtil.zRevCard error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 删除有序集合中指定元素。
     *
     * @param key    键
     * @param values 元素
     * @return 删除的数量
     */
    public Long zRevRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForZSet().remove(key, values);
        } catch (Exception e) {
            log.error("RedisUtil.zRevRemove error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 删除有序集合中指定排名范围内的元素。
     *
     * @param key   键
     * @param start 排名起始位置
     * @param end   排名结束位置
     * @return 删除的数量
     */
    public Long zRevRemoveRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().removeRange(key, start, end);
        } catch (Exception e) {
            log.error("RedisUtil.zRevRemoveRange error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 删除有序集合中指定分数范围内的元素。
     *
     * @param key 键
     * @param min 分数最小值
     * @param max 分数最大值
     * @return 删除的数量
     */
    public Long zRevRemoveRangeByScore(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
        } catch (Exception e) {
            log.error("RedisUtil.zRevRemoveRangeByScore error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 增加有序集合中指定元素的分数。
     *
     * @param key   键
     * @param value 元素
     * @param delta 分数增量
     * @return 新的分数
     */
    public Double zRevIncrementScore(String key, Object value, double delta) {
        try {
            return redisTemplate.opsForZSet().incrementScore(key, value, delta);
        } catch (Exception e) {
            log.error("RedisUtil.zRevIncrementScore error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取有序集合中指定分数范围内的元素。
     *
     * @param key 键
     * @param min 分数最小值
     * @param max 分数最大值
     * @return 元素集合
     */
    public Set<Object> zRangeByScore(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max);
        } catch (Exception e) {
            log.error("RedisUtil.zRangeByScore error: {}", e.getMessage());
            return null;
        }
    }

    public String getKey(Object table, Object key, Object field) {
        return table.toString() + ":" + key.toString() + ":" + field.toString();
    }

    public String getKey(Object... parts) {
        String key = "";
        for (int i = 0; i < parts.length; ++i) {
            if (i != parts.length - 1) {
                key += (parts[i].toString() + ":");
            } else {
                key += parts[parts.length - 1];
            }
        }
        return key;
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * 指定缓存失效时间
     *
     * @param key      键
     * @param time     时间(秒)
     * @param timeUnit 单位
     */
    public boolean expire(String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, timeUnit);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * 根据 key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 查找匹配key
     *
     * @param pattern key
     * @return /
     */
    public List<String> scan(String pattern) {
        ScanOptions options = ScanOptions.scanOptions().match(pattern).build();
        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
        RedisConnection rc = Objects.requireNonNull(factory).getConnection();
        Cursor<byte[]> cursor = rc.scan(options);
        List<String> result = new ArrayList<>();
        while (cursor.hasNext()) {
            result.add(new String(cursor.next()));
        }
        try {
            RedisConnectionUtils.releaseConnection(rc, factory);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 分页查询 key
     *
     * @param patternKey key
     * @param page       页码
     * @param size       每页数目
     * @return /
     */
    public List<String> findKeysForPage(String patternKey, int page, int size) {
        ScanOptions options = ScanOptions.scanOptions().match(patternKey).build();
        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
        RedisConnection rc = Objects.requireNonNull(factory).getConnection();
        Cursor<byte[]> cursor = rc.scan(options);
        List<String> result = new ArrayList<>(size);
        int tmpIndex = 0;
        int fromIndex = page * size;
        int toIndex = page * size + size;
        while (cursor.hasNext()) {
            if (tmpIndex >= fromIndex && tmpIndex < toIndex) {
                result.add(new String(cursor.next()));
                tmpIndex++;
                continue;
            }
            // 获取到满足条件的数据后,就可以退出了
            if (tmpIndex >= toIndex) {
                break;
            }
            tmpIndex++;
            cursor.next();
        }
        try {
            RedisConnectionUtils.releaseConnection(rc, factory);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public void del(String... keys) {
        if (keys != null && keys.length > 0) {
            if (keys.length == 1) {
                boolean result = redisTemplate.delete(keys[0]);
            } else {
                Set<String> keySet = new HashSet<>();
                for (String key : keys) {
                    keySet.addAll(redisTemplate.keys(key));
                }
                long count = redisTemplate.delete(keySet);
            }
        }
    }

    public long del(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 批量获取
     *
     * @param keys
     * @return
     */
    public List<Object> multiGet(List<String> keys) {
        List list = redisTemplate.opsForValue().multiGet(Sets.newHashSet(keys));
        List resultList = Lists.newArrayList();
        Optional.ofNullable(list).ifPresent(e -> list.forEach(ele -> Optional.ofNullable(ele).ifPresent(resultList::add)));
        return resultList;
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key      键
     * @param value    值
     * @param time     时间
     * @param timeUnit 类型
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);

    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return /
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    /**
     * @param prefix 前缀
     * @param ids    id
     */
    public void delByKeys(String prefix, Iterator<Long> ids) {
        Set<String> keys = new HashSet<>();
        for (Iterator<Long> it = ids; it.hasNext(); ) {
            Long id = it.next();
            keys.addAll(redisTemplate.keys(new StringBuffer(prefix).append(id).toString()));
        }
        long count = redisTemplate.delete(keys);
    }

    public long incr(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public long incr(String key, long increment) {
        return redisTemplate.opsForValue().increment(key, increment);
    }

    public double incr(String key, double increment) {
        return redisTemplate.opsForValue().increment(key, increment);
    }

    public long pfAdd(String key, Object value) {

        return redisTemplate.opsForHyperLogLog().add(key, value);
    }

    public long pfCount(String... key) {
        return redisTemplate.opsForHyperLogLog().size(key);
    }
}
