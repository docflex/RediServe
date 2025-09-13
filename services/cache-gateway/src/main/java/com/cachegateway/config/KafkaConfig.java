package com.cachegateway.config;

import commonlibs.cache.policy.Policy;
import commonlibs.kafka.KafkaCommonConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

/**
 * Kafka configuration for Cache Gateway.
 * <p>
 * Extends KafkaCommonConfig to reuse generic producer/consumer factories and listener container helpers.
 * Defines beans for:
 * - Policy updates (JSON-serialized)
 * - Namespace invalidation (String messages)
 */
@Configuration
@EnableKafka
public class KafkaConfig extends KafkaCommonConfig {

    /** Kafka consumer group for policy updates */
    @Value("${spring.kafka.consumer.policy-group-id}")
    private String policyGroupId;

    /** Kafka consumer group for namespace invalidation */
    @Value("${spring.kafka.consumer.invalidate-group-id}")
    private String invalidateGroupId;

    /**
     * Kafka listener container factory for Policy objects.
     * Used by @KafkaListener methods handling "cache.policy.updates" topic.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Policy> policyKafkaListenerContainerFactory() {
        return jsonListenerFactory(Policy.class, policyGroupId);
    }

    /**
     * Kafka listener container factory for String messages.
     * Used by @KafkaListener methods handling "cache.namespace.invalidate" topic.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> stringKafkaListenerContainerFactory() {
        return stringListenerFactory(invalidateGroupId);
    }
}
