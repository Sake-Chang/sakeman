package com.sakeman.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sakeman.service.CacheService;

@Controller
public class CacheController {
    private final CacheService cacheService;

    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @PostMapping("/admin/evict-cache")
    public String evictCacheAdmin(RedirectAttributes attrs) {
        cacheService.clearCache();
        attrs.addFlashAttribute("success", "キャッシュをクリアしました〜♫");
        return "redirect:/admin/index";
    }

    @PostMapping("/evict-cache")
    @ResponseBody
    public void evictCache() {
        cacheService.clearCache();
    }
}
