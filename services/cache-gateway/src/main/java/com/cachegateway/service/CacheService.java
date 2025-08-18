package com.cachegateway.service;

import com.cachegateway.models.Product;
import com.cachegateway.policy.Policy;
import com.cachegateway.policy.PolicyRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
        this.webClient = WebClient.builder().baseUrl(dbFetcherUrl).build();
        this.policyRegistry = policyRegistry;
    }

    public Product getProduct(String namespace, String entity, Long id) {
        String key = namespace + ":" + entity + ":" + id;
        Policy policy = policyRegistry.getPolicy(namespace);

        // Try cache
        Object cachedValue = redisTemplate.opsForValue().get(key);
        if (cachedValue instanceof Product product) {
            log.info("[CACHE-HIT] key={}", key);
            return product;
        }

        // Cache miss → fetch from DB-Fetcher
        log.info("[CACHE-MISS] key={}, fetching from DB-Fetcher...", key);

        Product product = webClient.get()
                .uri("/products/{id}", id)
                .retrieve()
                .bodyToMono(Product.class)
                .block();

        if (product != null) {
            redisTemplate.opsForValue().set(key, product,
                    java.time.Duration.ofSeconds(policy.getTtlSeconds()));
            log.info("[CACHE-STORE] key={} stored in Redis with TTL={}s", key, policy.getTtlSeconds());
        }

        return product;
    }

}
