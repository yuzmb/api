package com.mtdhb.api.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import com.mtdhb.api.cache.SignatureKeyGenerator;

/**
 * @author i@huangdenghe.com
 * @date 2018/05/30
 */
@Configuration
public class CachingConfiguration extends CachingConfigurerSupport {

    @Override
    public KeyGenerator keyGenerator() {
        return new SignatureKeyGenerator();
    }

    @SuppressWarnings("rawtypes")
    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
        // 设置缓存过期时间，单位：秒
        Map<String, Long> expires = new HashMap<>();
        expires.put("USER_SESSION", 30 * 60L);
        expires.put("RECEIVING_TREND", 30 * 60L);
        expires.put("RECEIVING_PIE", 30 * 60L);
        expires.put("COOKIE_RANK", 15 * 60L);
        expires.put("RECEIVING_CAROUSEL", 60L);
        redisCacheManager.setExpires(expires);
        return redisCacheManager;
    }

}
