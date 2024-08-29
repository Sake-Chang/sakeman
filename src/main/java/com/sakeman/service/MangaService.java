package com.sakeman.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Author;
import com.sakeman.entity.Manga;
import com.sakeman.exception.ResourceNotFoundException;
import com.sakeman.repository.MangaRepository;
import com.sakeman.repository.MangaSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MangaService {
    private final MangaRepository mangaRepository;

    /** 全件を検索して返す **/
    @Transactional(readOnly = true)
    public List<Manga> getMangaList() {
        return mangaRepository.findAll();
    }

    /** ページネーション */
    @Transactional(readOnly = true)
    public Page<Manga> getMangaListPageable(Pageable pageable){
        return mangaRepository.findAll(pageable);
    }

    /** 検索結果 */
    @Transactional(readOnly = true)
    public Page<Manga> getSearchResult(Manga manga, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher
                .matching() // and条件
                .withStringMatcher(StringMatcher.CONTAINING) // Like句
                .withIgnoreCase(); // 大文字小文字の両方
        return mangaRepository.findAll(Example.of(manga, matcher), pageable);
    }

    @Transactional(readOnly = true)
    public Page<Manga> searchManga(String[] keywords, Pageable pageable) {
        Page<Manga> resultPage = mangaRepository.findAll(MangaSpecifications.searchManga(keywords), pageable);
        List<Manga> uniqueMangas = new ArrayList<>(new HashSet<>(resultPage.getContent()));

        return new PageImpl<>(uniqueMangas, pageable, uniqueMangas.size());
    }

    /** 検索結果 (select2用) */
    @Transactional(readOnly = true)
    public List<Manga> getSearchResult(Manga manga) {
        ExampleMatcher matcher = ExampleMatcher
                .matching() // and条件
                .withStringMatcher(StringMatcher.CONTAINING) // Like句
                .withIgnoreCase(); // 大文字小文字の両方
        return mangaRepository.findAll(Example.of(manga, matcher));
    }

    /** Like検索結果 */
    @Transactional(readOnly = true)
    public List<Manga> getLikeSearch(String string) {
        return mangaRepository.findByTitleLike("%" + string + "%");
    }

    @Transactional(readOnly = true)
    public List<Manga> getByIdBetween(int start, int end) {
        return mangaRepository.findByIdBetween(start, end);
    }

    /** クレンズで検索 */
    @Transactional(readOnly = true)
    public List<Manga> getMangaByTitleCleanse(String titleCleanse) {
        return mangaRepository.findByTitleCleanse(titleCleanse);
    }

    /** 1件を検索して返す */
    @Transactional(readOnly = true)
    public Manga getManga(Integer id) {
        return mangaRepository.findById(id).get();
    }

    @Transactional(readOnly = true)
    public Manga getMangaOrThrow(Integer id) {
        return mangaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manga not found with id " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Manga> getMangaByTitle(String title) {
        return mangaRepository.findByTitle(title);
    }

    /** 著者IDで検索 */
    @Transactional(readOnly = true)
    public List<Manga> getMangaByAuthorId(Integer userId) {
        return mangaRepository.findByMangaAuthorsAuthorId(userId);
    }

    /** 著者IDで検索Pageable */
    @Transactional(readOnly = true)
    public Page<Manga> getMangaByAuthorId(Integer userId, Pageable pageable) {
        return mangaRepository.findByMangaAuthorsAuthorId(userId, pageable);
    }

    /** 著者リストで検索 */
    @Transactional(readOnly = true)
    public List<Manga> getMangaByAuthorsIn(List<Author> authors) {
        return mangaRepository.findByMangaAuthorsAuthorIn(authors);
    }

    /** 著者リストで検索 Distinct */
    @Transactional(readOnly = true)
    public Page<Manga> getDistinctMangaByAuthorsIn(List<Author> authors, Pageable pageable) {
        return mangaRepository.findDistinctByMangaAuthorsAuthorIn(authors, pageable);
    }

    /** 登録処理 */
    @Transactional
    public Manga saveManga (Manga manga) {
        return mangaRepository.save(manga);
    }

    @Transactional
    public List<Manga> saveAllManga(List<Manga> mangalist) {
        return mangaRepository.saveAll(mangalist);
    }

}
