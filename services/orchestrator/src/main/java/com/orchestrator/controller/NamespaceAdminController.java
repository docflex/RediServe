package com.orchestrator.controller;

import commonlibs.cache.policy.Policy;
import commonlibs.cache.policy.PolicyRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/admin/namespaces")
public class NamespaceAdminController {

    private final PolicyRegistry policyRegistry;
    private final WebClient webClient;

    public NamespaceAdminController(PolicyRegistry policyRegistry, @Value("${services.cachegateway.url}") String cacheGatewayUrl) {
        this.policyRegistry = policyRegistry;
        this.webClient = WebClient.builder().baseUrl(cacheGatewayUrl).build();
    }

    @PostMapping("/{ns}/policy")
    public String updatePolicy(
            @PathVariable String ns,
            @RequestBody Policy newPolicy) {
        policyRegistry.updatePolicy(ns, newPolicy);

        webClient.post()
                .uri("/admin/namespaces/{ns}/policy", ns)
                .bodyValue(newPolicy)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return "Updated policy for namespace " + ns + " (orchestrator + gateway)";
    }

    @PostMapping("/{ns}/invalidate")
    public String invalidateNamespace(@PathVariable String ns) {
        // Forward invalidate to gateway
        String response = webClient.post()
                .uri("/admin/namespaces/{ns}/invalidate", ns)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return "Orchestrator triggered invalidate for namespace " + ns + " → Gateway says: " + response;
    }

}
