package com.sakeman.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Author;
import com.sakeman.entity.Genre;
import com.sakeman.entity.Manga;
import com.sakeman.entity.Tag;
import com.sakeman.repository.AuthorRepository;
import com.sakeman.repository.GenreRepository;
import com.sakeman.repository.TagRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository repository;

    /** 全件を検索して返す **/
    @Transactional(readOnly = true)
    public List<Genre> getGenreList() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return repository.count();
    }

    /** 全件を表示順で返す **/
    @Transactional(readOnly = true)
    @Cacheable("genreListOrdered")
    public List<Genre> getGenreListOrdered() {
        return repository.findAllByOrderByDisplayOrder();
    }

    /** ページネーション */
    @Transactional(readOnly = true)
    public Page<Genre> getGenreListPageable(Pageable pageable){
        return repository.findAll(pageable);
    }

    /** 検索結果 */
    @Transactional(readOnly = true)
    public List<Genre> getSearchResult(Genre genre) {
        ExampleMatcher matcher = ExampleMatcher
                .matching() // and条件
                .withStringMatcher(StringMatcher.CONTAINING) // Like句
                .withIgnoreCase(); // 大文字小文字の両方
        return repository.findAll(Example.of(genre, matcher));
    }

    @Transactional(readOnly = true)
    public Page<Genre> getSearchResultWithPaging(Genre genre, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher
                .matching() // and条件
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // Like句
                .withIgnoreCase(); // 大文字小文字の両方

        return repository.findAll(Example.of(genre, matcher), pageable);
    }

    /** 1件を検索して返す */
    @Transactional(readOnly = true)
    public Genre getGenre(Integer id) {
        return repository.findById(id).get();
    }

    /** 著者名で検索して返す */
    @Transactional(readOnly = true)
    public List<Genre> findByName(String name) {
        return repository.findByName(name);
    }

    /** 登録処理 */
    @Transactional
    @CacheEvict(value = "genreListOrdered", allEntries = true)
    public Genre saveGenre (Genre genre) {
        return repository.save(genre);
    }

}
