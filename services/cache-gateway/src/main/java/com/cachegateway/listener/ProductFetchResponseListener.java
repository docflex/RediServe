package com.cachegateway.listener;

import com.cachegateway.service.CacheService;
import commonlibs.kafka.messages.ProductFetchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener for handling ProductFetchResponse messages.
 * <p>
 * Delegates the completion of pending cache requests to {@link CacheService}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductFetchResponseListener {

    private final CacheService cacheService;

    /**
     * Handles incoming ProductFetchResponse messages from Kafka.
     *
     * @param response The response message containing correlationId and ProductDTO
     */
    @KafkaListener(topics = "db.fetch.responses", groupId = "cache-gateway-group")
    public void handle(ProductFetchResponse response) {
        boolean handled = cacheService.completePendingRequest(response);
        if (!handled) {
            log.warn("Received response with unknown correlationId={}", response.getCorrelationId());
        }
    }
}
