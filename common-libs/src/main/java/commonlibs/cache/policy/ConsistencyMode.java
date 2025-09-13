package commonlibs.cache.policy;

public enum ConsistencyMode {
    ASIDE,        // Cache-Aside (fetch from DB on miss)
    READ_THROUGH; // Read-Through (auto-fetch from DB)
}
