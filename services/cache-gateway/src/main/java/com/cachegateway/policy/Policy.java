package com.cachegateway.policy;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Policy {
    private final long ttlSeconds;
    private final ConsistencyMode consistencyMode;
}
