package com.sakeman.service;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Manga;
import com.sakeman.repository.MangaRepository;


@Service
public class MangaService {
    private final MangaRepository mangaRepository;

    public MangaService(MangaRepository repository) {
        this.mangaRepository = repository;
    }

    /** 全件を検索して返す **/
    public List<Manga> getMangaList() {
        return mangaRepository.findAll();
    }

    /** ページネーション */
    public Page<Manga> getMangaListPageable(Pageable pageable){
        return mangaRepository.findAll(pageable);
    }

    /** 検索結果 */
    public List<Manga> getSearchResult(Manga manga) {
        ExampleMatcher matcher = ExampleMatcher
                .matching() // and条件
                .withStringMatcher(StringMatcher.CONTAINING) // Like句
                .withIgnoreCase(); // 大文字小文字の両方
        return mangaRepository.findAll(Example.of(manga, matcher));
    }

    /** 1件を検索して返す */
    public Manga getManga(Integer id) {
        return mangaRepository.findById(id).get();
    }

    /** 登録処理 */
//    @Transactional
    public Manga saveManga (Manga manga) {
        return mangaRepository.save(manga);
    }

}
