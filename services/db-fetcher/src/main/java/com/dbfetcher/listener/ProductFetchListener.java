package com.dbfetcher.listener;

import com.dbfetcher.mapper.ProductMapper;
import com.dbfetcher.models.ProductEntity;
import com.dbfetcher.repository.ProductRepository;
import commonlibs.kafka.messages.ProductFetchRequest;
import commonlibs.kafka.messages.ProductFetchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductFetchListener {

    private final ProductRepository repository;
    private final KafkaTemplate<String, ProductFetchResponse> kafkaTemplate;

    @KafkaListener(topics = "db.fetch.requests", groupId = "db-fetcher-group")
    public void handleFetchRequest(ProductFetchRequest request) {
        log.info("Received fetch request for productId={}", request.getProductId());

        ProductEntity product = repository.findById(request.getProductId()).orElse(null);
        ProductFetchResponse response = new ProductFetchResponse(request.getCorrelationId(), ProductMapper.toDTO(product));

        kafkaTemplate.send("db.fetch.responses", response.getCorrelationId(), response);
        log.info("Sent fetch response for correlationId={}", request.getCorrelationId());
    }
}
