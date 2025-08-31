package com.orchestrator.controller;

import com.orchestrator.publisher.PolicyEventPublisher;
import commonlibs.cache.policy.Policy;
import commonlibs.cache.policy.PolicyRegistry;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/namespaces")
public class NamespaceAdminController {

    private final PolicyRegistry policyRegistry;
    private final PolicyEventPublisher publisher;

    public NamespaceAdminController(PolicyRegistry policyRegistry,
                                    PolicyEventPublisher publisher) {
        this.policyRegistry = policyRegistry;
        this.publisher = publisher;
    }

    @PostMapping("/{ns}/policy")
    public String updatePolicy(@PathVariable String ns, @RequestBody Policy newPolicy) {
        policyRegistry.updatePolicy(ns, newPolicy);
        publisher.publishPolicyUpdate(ns, newPolicy);
        return "Updated + published policy for namespace " + ns;
    }

    @PostMapping("/{ns}/invalidate")
    public String invalidateNamespace(@PathVariable String ns) {
        // Publish an invalidate event to Kafka
        publisher.publishCacheInvalidate(ns);

        return "Invalidate signal sent (and published to Kafka) for namespace " + ns;
    }
}
