package com.cachegateway.controller;

import com.cachegateway.models.Product;
import com.cachegateway.service.CacheService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private final CacheService cacheService;

    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @GetMapping("/{namespace}/{entity}/{id}")
    public Product getFromCache(@PathVariable String namespace,
                                @PathVariable String entity,
                                @PathVariable Long id) throws Exception {
        return cacheService.getProduct(namespace, entity, id);
    }
}
