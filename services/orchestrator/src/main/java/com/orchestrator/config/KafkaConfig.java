package com.orchestrator.config;

import commonlibs.cache.policy.Policy;
import commonlibs.kafka.KafkaCommonConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaConfig extends KafkaCommonConfig {
    @Bean
    public KafkaTemplate<String, Policy> policyKafkaTemplate() {
        return jsonKafkaTemplate();
    }

    @Bean
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        return super.stringKafkaTemplate();
    }
}
