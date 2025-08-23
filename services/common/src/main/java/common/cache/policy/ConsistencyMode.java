package common.cache.policy;

public enum ConsistencyMode {
    ASIDE,         // Cache-Aside (fetch from DB on miss, then populate cache)
    READ_THROUGH;  // Read-Through (cache layer fetches from DB automatically)
}
