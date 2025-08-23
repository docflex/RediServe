package common.cache.policy;

import common.cache.config.CachePolicyProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RefreshScope
public class PolicyRegistry {
    private final Map<String, Policy> policies = new ConcurrentHashMap<>();
    private final CachePolicyProperties cachePolicyProperties;

    public PolicyRegistry(CachePolicyProperties cachePolicyProperties) {
        this.cachePolicyProperties = cachePolicyProperties;
        reload();
    }

    public void reload() {
        policies.clear();
        cachePolicyProperties.getPolicies().forEach((ns, cfg) -> {
            policies.put(ns, new Policy(cfg.getTtl(), cfg.getConsistency()));
        });
    }

    public Policy getPolicy(String namespace) {
        return policies.getOrDefault(namespace, policies.get("default"));
    }

    public void updatePolicy(String namespace, Policy policy) {
        policies.put(namespace, policy);
    }
}

