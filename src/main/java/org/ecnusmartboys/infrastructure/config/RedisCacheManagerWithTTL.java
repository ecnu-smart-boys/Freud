package org.ecnusmartboys.infrastructure.config;

import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

public class RedisCacheManagerWithTTL extends RedisCacheManager {

    private static final RedisSerializationContext.SerializationPair<Object> DEFAULT_PAIR =
            RedisSerializationContext.SerializationPair
                    .fromSerializer(new GenericJackson2JsonRedisSerializer());
    private static final CacheKeyPrefix DEFAULT_CACHE_KEY_PREFIX = cacheName -> cacheName + ":";

    public RedisCacheManagerWithTTL(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        final int lastIndexOf = name.lastIndexOf('#');
        if (lastIndexOf > -1) {
            final String ttl = name.substring(lastIndexOf + 1);
            final Duration duration = Duration.ofSeconds(Long.parseLong(ttl));
            cacheConfig = cacheConfig.entryTtl(duration);
            //修改缓存key和value值的序列化方式
            cacheConfig = cacheConfig.computePrefixWith(DEFAULT_CACHE_KEY_PREFIX)
                    .serializeValuesWith(DEFAULT_PAIR);
            final String cacheName = name.substring(0, lastIndexOf);
            return super.createRedisCache(cacheName, cacheConfig);
        } else {
            //修改缓存key和value值的序列化方式
            cacheConfig = cacheConfig.computePrefixWith(DEFAULT_CACHE_KEY_PREFIX)
                    .serializeValuesWith(DEFAULT_PAIR);
            return super.createRedisCache(name, cacheConfig);
        }
    }

}
