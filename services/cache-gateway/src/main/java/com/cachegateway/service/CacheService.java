package com.cachegateway.service;

import com.cachegateway.models.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;

@Slf4j
@Service
public class CacheService {

    private final StringRedisTemplate redisTemplate;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public CacheService(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            @Value("${services.dbfetcher.url}") String dbFetcherUrl) {

        this.redisTemplate = redisTemplate;
        this.webClient = WebClient.builder().baseUrl(dbFetcherUrl).build();
        this.objectMapper = objectMapper;
    }

    public Product getProduct(String namespace, String entity, Long id) throws Exception {
        String key = namespace + ":" + entity + ":" + id;

        // 1. Try cache
        String cachedValue = redisTemplate.opsForValue().get(key);
        if (cachedValue != null) {
            log.info("[CACHE-HIT] key={}", key);
            return objectMapper.readValue(cachedValue, Product.class);
        }

        // 2. Cache miss → fetch from db-fetcher
        log.info("[CACHE-MISS] key={}, fetching from DB-Fetcher...", key);

        Product product = webClient.get()
                .uri("/products/{id}", id)
                .retrieve()
                .bodyToMono(Product.class)
                .block();

        if (product != null) {
            // 3. Store in Redis with TTL (60s for now)
            redisTemplate.opsForValue()
                    .set(key, objectMapper.writeValueAsString(product), Duration.ofSeconds(60));
            log.info("[CACHE-STORE] key={} stored in Redis with TTL=60s", key);
        }

        return product;
    }
}
