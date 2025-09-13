package com.cachegateway.controller;

import com.cachegateway.service.CacheService;
import commonlibs.dto.ProductDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * REST controller for cache access.
 * <p>
 * Exposes endpoints to read cached entities, falling back to DB-fetch if a cache miss occurs.
 */
@RestController
@RequestMapping("/cache")
public class CacheController {

    private final CacheService cacheService;

    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * Fetches an entity from cache, or requests it from DB if not cached.
     *
     * @param namespace The cache namespace
     * @param entity    The entity type (e.g., "products")
     * @param id        The entity ID
     * @return CompletableFuture of ResponseEntity<ProductDTO>
     */
    @GetMapping("/{namespace}/{entity}/{id}")
    public CompletableFuture<ResponseEntity<ProductDTO>> getFromCache(@PathVariable String namespace,
                                                                      @PathVariable String entity,
                                                                      @PathVariable Long id) {
        return cacheService.getProductAsync(namespace, entity, id)
                .thenApply(product -> {
                    if (product != null) {
                        return ResponseEntity.ok(product);
                    } else {
                        return ResponseEntity.notFound().build();
                    }
                });
    }
}
