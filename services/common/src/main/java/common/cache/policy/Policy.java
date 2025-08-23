package common.cache.policy;

public record Policy(long ttlSeconds, ConsistencyMode consistencyMode) {
}
