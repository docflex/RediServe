package commonlibs.cache.config;

import commonlibs.cache.policy.ConsistencyMode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cache")
public class CachePolicyProperties {

    private Map<String, PolicyConfig> policies = new HashMap<>();

    @Getter
    @Setter
    public static class PolicyConfig {
        private long ttl;                        // in seconds
        private ConsistencyMode consistency = ConsistencyMode.ASIDE; // default
    }
}
