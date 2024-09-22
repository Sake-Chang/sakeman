package com.sakeman.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sakeman.service.CacheService;

@RestController
public class CacheController {
    private final CacheService cacheService;

    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @PostMapping("/evictCache")
    public void evictCache() {
        cacheService.clearCache();
    }
}
