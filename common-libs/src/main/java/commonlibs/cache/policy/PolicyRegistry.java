package commonlibs.cache.policy;

import commonlibs.cache.config.CachePolicyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RefreshScope
@Slf4j
public class PolicyRegistry {

    private final Map<String, Policy> policies = new ConcurrentHashMap<>();
    private final CachePolicyProperties cachePolicyProperties;

    public PolicyRegistry(CachePolicyProperties cachePolicyProperties) {
        this.cachePolicyProperties = cachePolicyProperties;
        reload();
    }

    /** Reload policies from configuration */
    public void reload() {
        policies.clear();
        cachePolicyProperties.getPolicies().forEach((ns, cfg) ->
                policies.put(ns, new Policy(cfg.getTtl(), cfg.getConsistency()))
        );
        log.info("PolicyRegistry reloaded: {}", policies);
    }

    /** Fetch policy for namespace or fallback to "default" */
    public Policy getPolicy(String namespace) {
        return policies.getOrDefault(namespace, policies.get("default"));
    }

    /** Update a policy at runtime */
    public void updatePolicy(String namespace, Policy policy) {
        if (policy == null) throw new IllegalArgumentException("Policy cannot be null");
        policies.put(namespace, policy);
    }

    /** Get all policies (read-only copy) */
    public Map<String, Policy> getAllPolicies() {
        return Map.copyOf(policies);
    }
}
