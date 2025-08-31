package com.orchestrator.publisher;

import commonlibs.cache.policy.Policy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PolicyEventPublisher {

    private final KafkaTemplate<String, Policy> policyKafkaTemplate;
    private final KafkaTemplate<String, String> stringKafkaTemplate;

    public PolicyEventPublisher(KafkaTemplate<String, Policy> policyKafkaTemplate,
                                KafkaTemplate<String, String> stringKafkaTemplate) {
        this.policyKafkaTemplate = policyKafkaTemplate;
        this.stringKafkaTemplate = stringKafkaTemplate;
    }

    public void publishPolicyUpdate(String namespace, Policy policy) {
        policyKafkaTemplate.send("cache.policy.updates", namespace, policy);
    }

    public void publishCacheInvalidate(String namespace) {
        stringKafkaTemplate.send("cache.namespace.invalidate", namespace);
    }
}
