package com.orchestrator.controller;

import com.orchestrator.publisher.PolicyEventPublisher;
import commonlibs.cache.policy.Policy;
import commonlibs.cache.policy.PolicyRegistry;
import org.springframework.web.bind.annotation.*;

/**
 * Administrative REST controller for managing cache namespaces and policies.
 * <p>
 * Exposes endpoints for updating cache policies and sending cache invalidation signals.
 */
@RestController
@RequestMapping("/admin/namespaces")
public class NamespaceAdminController {

    private final PolicyRegistry policyRegistry;
    private final PolicyEventPublisher publisher;

    /**
     * Constructor for NamespaceAdminController.
     *
     * @param policyRegistry The registry that stores policies per namespace
     * @param publisher      Publisher to send policy updates and invalidate events over Kafka
     */
    public NamespaceAdminController(PolicyRegistry policyRegistry,
                                    PolicyEventPublisher publisher) {
        this.policyRegistry = policyRegistry;
        this.publisher = publisher;
    }

    /**
     * Update the cache policy for a given namespace.
     * <p>
     * Updates the local registry and publishes the change via Kafka to notify other services.
     *
     * @param ns        Namespace identifier
     * @param newPolicy The new policy to apply
     * @return Confirmation message
     */
    @PostMapping("/{ns}/policy")
    public String updatePolicy(@PathVariable String ns, @RequestBody Policy newPolicy) {
        policyRegistry.updatePolicy(ns, newPolicy);
        publisher.publishPolicyUpdate(ns, newPolicy);
        return "Updated + published policy for namespace " + ns;
    }

    /**
     * Invalidate the cache for a given namespace.
     * <p>
     * Sends an invalidate event to Kafka so other services can clear relevant cache entries.
     *
     * @param ns Namespace identifier
     * @return Confirmation message
     */
    @PostMapping("/{ns}/invalidate")
    public String invalidateNamespace(@PathVariable String ns) {
        publisher.publishCacheInvalidate(ns);
        return "Invalidate signal sent (and published to Kafka) for namespace " + ns;
    }
}
