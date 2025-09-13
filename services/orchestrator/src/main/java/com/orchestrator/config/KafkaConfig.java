package com.orchestrator.config;

import commonlibs.cache.policy.Policy;
import commonlibs.kafka.config.KafkaCommonConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Kafka configuration for the orchestrator service.
 * <p>
 * Provides KafkaTemplate beans for sending messages of different types:
 * - {@link Policy} objects
 * - plain String messages
 * <p>
 * Extends {@link KafkaCommonConfig} to reuse common Kafka configuration (e.g., serializers, producer factory).
 */
@Configuration
public class KafkaConfig extends KafkaCommonConfig {

    /**
     * KafkaTemplate for sending {@link Policy} objects as JSON messages.
     * <p>
     * Uses a generic JSON serializer configured in KafkaCommonConfig.
     *
     * @return KafkaTemplate capable of sending Policy messages
     */
    @Bean
    public KafkaTemplate<String, Policy> policyKafkaTemplate() {
        return jsonKafkaTemplate();
    }

    /**
     * KafkaTemplate for sending plain String messages.
     *
     * @return KafkaTemplate capable of sending String messages
     */
    @Bean
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        return super.stringKafkaTemplate();
    }
}
