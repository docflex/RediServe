package com.cachegateway.config;

import com.cachegateway.policy.ConsistencyMode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "cache")
public class CachePolicyProperties {

    private Map<String, PolicyConfig> policies;

    @Setter
    @Getter
    public static class PolicyConfig {
        private long ttl;
        private ConsistencyMode consistency;
    }
}
