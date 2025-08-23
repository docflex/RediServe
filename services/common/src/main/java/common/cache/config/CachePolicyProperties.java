package common.cache.config;

import common.cache.policy.ConsistencyMode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "cache")
public class CachePolicyProperties {

    private Map<String, PolicyConfig> policies = new HashMap<>();

    @Setter
    @Getter
    public static class PolicyConfig {
        private long ttl;
        private ConsistencyMode consistency;
    }
}
