package com.sakeman.service;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Manga;
import com.sakeman.entity.WebMangaMedia;
import com.sakeman.repository.MangaRepository;
import com.sakeman.repository.WebMangaMediaRepository;


@Service
public class WebMangaMediaService {
    private final WebMangaMediaRepository webMangaMediaRepository;

    public WebMangaMediaService(WebMangaMediaRepository repository) {
        this.webMangaMediaRepository = repository;
    }

    /** 全件を検索して返す **/
    @Cacheable("mediaList")
    public List<WebMangaMedia> getWebMangaMediaList() {
        return webMangaMediaRepository.findAll();
    }

    /** ページネーション */
    public Page<WebMangaMedia> getWebMangaMediaListPageable(Pageable pageable){
        return webMangaMediaRepository.findAll(pageable);
    }

    /** 1件を検索して返す */
    public WebMangaMedia getWebMangaMedia(Integer id) {
        return webMangaMediaRepository.findById(id).get();
    }

    public Optional<WebMangaMedia> getWebMangaMediaByName(String name) {
        return webMangaMediaRepository.findByName(name);
    }

    /** 登録処理 */
//    @Transactional
    @CacheEvict(value = "mediaList", allEntries = true)
    public WebMangaMedia saveWebMangaMedia (WebMangaMedia webMangaMedia) {
        return webMangaMediaRepository.save(webMangaMedia);
    }

}
