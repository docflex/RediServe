package com.dbfetcher.config;

import commonlibs.kafka.config.KafkaCommonConfig;
import commonlibs.kafka.messages.ProductFetchRequest;
import commonlibs.kafka.messages.ProductFetchResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableKafka
public class KafkaConfig extends KafkaCommonConfig {

    @Bean
    public KafkaTemplate<String, ProductFetchResponse> productResponseKafkaTemplate() {
        return jsonKafkaTemplate();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductFetchRequest> requestListenerFactory() {
        return jsonListenerFactory(ProductFetchRequest.class, "db-fetcher-group");
    }
}
