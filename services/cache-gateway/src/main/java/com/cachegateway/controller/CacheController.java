package com.cachegateway.controller;

import com.cachegateway.models.Product;
import com.cachegateway.service.CacheService;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for cache access.
 * <p>
 * Exposes endpoints to read cached entities, falling back to DB-fetch if cache miss occurs.
 */
@RestController
@RequestMapping("/cache")
public class CacheController {

    private final CacheService cacheService;

    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * Fetches an entity from cache or DB fallback.
     *
     * @param namespace The cache namespace
     * @param entity    The entity type (e.g., "products")
     * @param id        The entity ID
     * @return Product object from cache or DB
     * @throws Exception if retrieval fails
     */
    @GetMapping("/{namespace}/{entity}/{id}")
    public Product getFromCache(@PathVariable String namespace,
                                @PathVariable String entity,
                                @PathVariable Long id) throws Exception {
        return cacheService.getProduct(namespace, entity, id);
    }
}
