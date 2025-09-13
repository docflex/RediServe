package com.cachegateway.service;

import commonlibs.kafka.messages.ProductFetchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Gateway for sending ProductFetchRequest messages to the Kafka topic "db.fetch.requests".
 * <p>
 * This class abstracts the Kafka interaction from the CacheService, providing a clean method
 * to send requests asynchronously and log success or failure.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductFetchRequestGateway {

    /**
     * KafkaTemplate used for sending messages to Kafka.
     * The key is a String, and the value is a ProductFetchRequest.
     */
    private final KafkaTemplate<String, ProductFetchRequest> kafkaTemplate;

    /**
     * Sends a ProductFetchRequest message to the Kafka topic "db.fetch.requests".
     * <p>
     * The send operation is asynchronous. When complete, the method logs success or failure
     * using the correlationId from the request.
     *
     * @param request the ProductFetchRequest containing the correlationId and productId
     */
    public void sendRequest(ProductFetchRequest request) {
        kafkaTemplate.send("db.fetch.requests", request)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send fetch request with correlationId={}",
                                request.getCorrelationId(), ex);
                    } else {
                        log.info("Sent fetch request with correlationId={}",
                                request.getCorrelationId());
                    }
                });
    }
}
