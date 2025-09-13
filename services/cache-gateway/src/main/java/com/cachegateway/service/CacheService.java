package com.cachegateway.service;

import com.cachegateway.models.Product;
import commonlibs.cache.policy.Policy;
import commonlibs.cache.policy.PolicyRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * Service to handle caching of Products with Redis, based on
 * policies defined per namespace.
 *
 * <p>Implements a simple cache-aside pattern: attempts to read from
 * Redis first, then falls back to DB-Fetcher if cache miss occurs.
 * Stores the fetched value in Redis according to the TTL defined
 * in the policy.
 */
@Slf4j
@Service
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final WebClient webClient;
    private final PolicyRegistry policyRegistry;

    public CacheService(
            RedisTemplate<String, Object> redisTemplate,
            @Value("${services.dbfetcher.url}") String dbFetcherUrl,
            PolicyRegistry policyRegistry) {

        this.redisTemplate = redisTemplate;
        this.webClient = WebClient.builder()
                .baseUrl(dbFetcherUrl)
                .build();
        this.policyRegistry = policyRegistry;
    }

    /**
     * Fetch a Product either from Redis cache or from DB-Fetcher service.
     * Caches the value according to the namespace policy.
     *
     * @param namespace the cache namespace
     * @param entity    the entity type (e.g., "product")
     * @param id        the entity ID
     * @return the Product object, or null if not found
     */
    public Product getProduct(String namespace, String entity, Long id) {
        String key = namespace + ":" + entity + ":" + id;
        Policy policy = policyRegistry.getPolicy(namespace);

        // Attempt to fetch from Redis cache
        Object cachedValue = redisTemplate.opsForValue().get(key);
        if (cachedValue instanceof Product product) {
            log.info("[CACHE-HIT] key={}", key);
            return product;
        }

        // Cache miss → fetch from DB-Fetcher service
        log.info("[CACHE-MISS] key={}, fetching from DB-Fetcher...", key);
        Product product = webClient.get()
                .uri("/products/{id}", id)
                .retrieve()
                .bodyToMono(Product.class)
                .block();

        // Store in Redis if found
        if (product != null) {
            redisTemplate.opsForValue().set(key, product, Duration.ofSeconds(policy.ttlSeconds()));
            log.info("[CACHE-STORE] key={} stored in Redis with TTL={}s", key, policy.ttlSeconds());
        }

        return product;
    }
}
