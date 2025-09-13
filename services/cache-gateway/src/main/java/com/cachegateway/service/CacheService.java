package com.cachegateway.service;

import commonlibs.cache.policy.Policy;
import commonlibs.cache.policy.PolicyRegistry;
import commonlibs.dto.ProductDTO;
import commonlibs.kafka.messages.ProductFetchRequest;
import commonlibs.kafka.messages.ProductFetchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PolicyRegistry policyRegistry;
    private final ProductFetchRequestGateway requestGateway;

    // Track pending requests by correlationId
    private final Map<String, CompletableFuture<ProductDTO>> pendingRequests = new ConcurrentHashMap<>();

    public CacheService(RedisTemplate<String, Object> redisTemplate,
                        PolicyRegistry policyRegistry,
                        ProductFetchRequestGateway requestGateway) {
        this.redisTemplate = redisTemplate;
        this.policyRegistry = policyRegistry;
        this.requestGateway = requestGateway;
    }

    /**
     * Non-blocking fetch: returns a CompletableFuture immediately.
     * If cache hit occurs, future is completed immediately.
     * If cache miss, sends Kafka request and completes future on response.
     *
     * @param namespace Cache namespace
     * @param entity    Entity type (e.g., "products")
     * @param id        Product ID
     * @return CompletableFuture of ProductDTO
     */
    public CompletableFuture<ProductDTO> getProductAsync(String namespace, String entity, Long id) {
        String key = buildKey(namespace, entity, id);
        Policy policy = policyRegistry.getPolicy(namespace);

        Object cachedValue = redisTemplate.opsForValue().get(key);
        if (cachedValue instanceof ProductDTO product) {
            log.info("[CACHE-HIT] key={}", key);
            return CompletableFuture.completedFuture(product);
        }

        log.info("[CACHE-MISS] key={}, sending Kafka fetch request...", key);

        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<ProductDTO> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        ProductFetchRequest request = new ProductFetchRequest(correlationId, id);
        requestGateway.sendRequest(request);

        // Complete future asynchronously when cache is updated
        future.thenAccept(product -> {
            if (product != null) {
                redisTemplate.opsForValue().set(key, product, Duration.ofSeconds(policy.ttlSeconds()));
                log.info("[CACHE-STORE] key={} stored in Redis with TTL={}s", key, policy.ttlSeconds());
            }
        }).exceptionally(ex -> {
            pendingRequests.remove(correlationId);
            log.error("Failed to fetch product for correlationId={}", correlationId, ex);
            return null;
        });

        return future;
    }

    /**
     * Called by Kafka listener when a response arrives.
     * Completes the corresponding pending future if exists.
     */
    public boolean completePendingRequest(ProductFetchResponse response) {
        var future = pendingRequests.remove(response.getCorrelationId());
        if (future != null) {
            future.complete(response.getProduct());
            return true;
        } else {
            log.warn("Late or unknown response received for correlationId={}", response.getCorrelationId());
            return false;
        }
    }

    private String buildKey(String namespace, String entity, Long id) {
        return String.format("%s:%s:%d", namespace, entity, id);
    }
}
