package com.cachegateway.policy;

import com.cachegateway.config.CachePolicyProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PolicyRegistry {

    private final Map<String, Policy> policies = new HashMap<>();

    public PolicyRegistry(CachePolicyProperties cachePolicyProperties) {
        cachePolicyProperties.getPolicies().forEach((namespace, config) -> {
            policies.put(namespace,
                    new Policy(config.getTtl(), config.getConsistency()));
        });
    }

    public Policy getPolicy(String namespace) {
        return policies.getOrDefault(namespace, policies.get("default"));
    }
}
