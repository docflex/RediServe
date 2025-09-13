package com.orchestrator.publisher;

import commonlibs.cache.policy.Policy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service responsible for publishing cache-related events over Kafka.
 * <p>
 * This includes:
 * - Policy updates for namespaces
 * - Cache invalidation signals for namespaces
 */
@Service
public class PolicyEventPublisher {

    private final KafkaTemplate<String, Policy> policyKafkaTemplate;
    private final KafkaTemplate<String, String> stringKafkaTemplate;

    /**
     * Constructor for PolicyEventPublisher.
     *
     * @param policyKafkaTemplate Template for sending Policy objects to Kafka topics
     * @param stringKafkaTemplate Template for sending simple String messages to Kafka topics
     */
    public PolicyEventPublisher(KafkaTemplate<String, Policy> policyKafkaTemplate,
                                KafkaTemplate<String, String> stringKafkaTemplate) {
        this.policyKafkaTemplate = policyKafkaTemplate;
        this.stringKafkaTemplate = stringKafkaTemplate;
    }

    /**
     * Publishes a policy update event for the specified namespace.
     * <p>
     * Other services listening to the "cache.policy.updates" topic
     * will receive this event and can update their local cache policies.
     *
     * @param namespace The namespace for which the policy is being updated
     * @param policy    The new policy object
     */
    public void publishPolicyUpdate(String namespace, Policy policy) {
        policyKafkaTemplate.send("cache.policy.updates", namespace, policy);
    }

    /**
     * Publishes a cache invalidation signal for the specified namespace.
     * <p>
     * Other services listening to the "cache.namespace.invalidate" topic
     * should clear cached entries for this namespace.
     *
     * @param namespace The namespace to invalidate
     */
    public void publishCacheInvalidate(String namespace) {
        stringKafkaTemplate.send("cache.namespace.invalidate", namespace);
    }
}
