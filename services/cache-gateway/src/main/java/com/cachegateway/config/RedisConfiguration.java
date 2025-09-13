package com.cachegateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis configuration for Cache Gateway.
 * <p>
 * Provides a RedisTemplate configured for:
 * - String keys
 * - JSON-serialized values with type information
 * <p>
 * This setup ensures type-safe storage and retrieval of cache objects.
 */
@Configuration
public class RedisConfiguration {

    /**
     * Configures a RedisTemplate<String, Object> for general-purpose caching.
     *
     * @param connectionFactory Redis connection factory
     * @return RedisTemplate configured with JSON serialization for values
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Serialize keys as plain strings
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Serialize values as JSON with type information (GenericJackson2JsonRedisSerializer)
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}
