package com.sakeman.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class CacheService {
    @CacheEvict(value = "cacheName", allEntries = true)
    public void clearCache() {
    }
}
