package com.cachegateway.listener;

import commonlibs.cache.policy.Policy;
import commonlibs.cache.policy.PolicyRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Kafka listener for cache policy updates and namespace invalidations.
 *
 * <p>
 * Listens for events from Kafka topics and updates the PolicyRegistry
 * or invalidates Redis keys accordingly.
 */
@Service
@Slf4j
public class PolicyEventListener {

    private final PolicyRegistry policyRegistry;
    private final RedisTemplate<String, Object> redisTemplate;

    public PolicyEventListener(PolicyRegistry policyRegistry, RedisTemplate<String, Object> redisTemplate) {
        this.policyRegistry = policyRegistry;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Handles updates to cache policies for a namespace.
     *
     * @param policy    the updated policy object
     * @param namespace the namespace key from the Kafka message
     */
    @KafkaListener(
            topics = "cache.policy.updates",
            groupId = "cache-gateway-policy-group",
            containerFactory = "policyKafkaListenerContainerFactory"
    )
    public void handlePolicyUpdate(Policy policy,
                                   @Header(KafkaHeaders.RECEIVED_KEY) String namespace) {
        policyRegistry.updatePolicy(namespace, policy);
        log.info("[GATEWAY] Policy updated via Kafka for namespace={}", namespace);
    }

    /**
     * Handles namespace invalidation events.
     * Deletes all Redis keys for the given namespace.
     *
     * @param namespace the namespace to invalidate
     */
    @KafkaListener(
            topics = "cache.namespace.invalidate",
            groupId = "cache-gateway-invalidate-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void handleInvalidate(String namespace) {
        String pattern = namespace + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        log.info("[GATEWAY] Namespace invalidated via Kafka: {} ({} keys removed)",
                namespace, keys != null ? keys.size() : 0);
    }
}
