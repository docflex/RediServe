package com.cachegateway.controller;

import common.cache.policy.Policy;
import common.cache.policy.PolicyRegistry;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/namespaces")
public class GatewayAdminController {
    private final PolicyRegistry policyRegistry;
    private final RedisTemplate<String, Object> redisTemplate;

    public GatewayAdminController(PolicyRegistry policyRegistry, RedisTemplate<String, Object> redisTemplate) {
        this.policyRegistry = policyRegistry;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/{ns}/policy")
    public String updatePolicy(@PathVariable String ns, @RequestBody Policy newPolicy) {
        policyRegistry.updatePolicy(ns, newPolicy);
        return "Cache-Gateway updated policy for namespace " + ns;
    }

    @PostMapping("/{ns}/invalidate")
    public String invalidateNamespace(@PathVariable String ns) {
        // Delete all keys for namespace
        String pattern = ns + ":*";
        var keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        return "Invalidated namespace " + ns + " (" + (keys != null ? keys.size() : 0) + " keys removed)";
    }
}