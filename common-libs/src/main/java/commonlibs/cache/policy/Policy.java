package commonlibs.cache.policy;

public record Policy(long ttlSeconds, ConsistencyMode consistencyMode) {
    public Policy {
        if (consistencyMode == null) {
            throw new IllegalArgumentException("ConsistencyMode cannot be null");
        }
        if (ttlSeconds < 0) {
            throw new IllegalArgumentException("TTL seconds cannot be negative");
        }
    }
}
