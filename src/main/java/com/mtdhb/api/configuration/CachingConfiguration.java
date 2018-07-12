package com.mtdhb.api.configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import com.mtdhb.api.cache.SignatureKeyGenerator;
import com.mtdhb.api.constant.CacheNames;

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

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        cacheConfigurations.put(CacheNames.USER, defaultCacheConfiguration.entryTtl(Duration.ofMinutes(20L)));
        cacheConfigurations.put(CacheNames.COOKIE_RANK, defaultCacheConfiguration.entryTtl(Duration.ofMinutes(30L)));
        cacheConfigurations.put(CacheNames.RECEIVING_CAROUSEL,
                defaultCacheConfiguration.entryTtl(Duration.ofSeconds(60L)));
        cacheConfigurations.put(CacheNames.RECEIVING_TREND,
                defaultCacheConfiguration.entryTtl(Duration.ofMinutes(30L)));
        cacheConfigurations.put(CacheNames.RECEIVING_PIE, defaultCacheConfiguration.entryTtl(Duration.ofMinutes(30L)));
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(defaultCacheConfiguration)
                .withInitialCacheConfigurations(cacheConfigurations).build();
    }

}
