package com.sakeman.service;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Author;
import com.sakeman.entity.Manga;
import com.sakeman.repository.AuthorRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;

    /** 全件を検索して返す **/
    @Transactional(readOnly = true)
    public List<Author> getAuthorList() {
        return authorRepository.findAll();
    }

    /** ページネーション */
    @Transactional(readOnly = true)
    public Page<Author> getAuthorListPageable(Pageable pageable){
        return authorRepository.findAll(pageable);
    }

    /** 検索結果 */
    @Transactional(readOnly = true)
    public List<Author> getSearchResult(Author author) {
        ExampleMatcher matcher = ExampleMatcher
                .matching() // and条件
                .withStringMatcher(StringMatcher.CONTAINING) // Like句
                .withIgnoreCase(); // 大文字小文字の両方
        return authorRepository.findAll(Example.of(author, matcher));
    }

    @Transactional(readOnly = true)
    public Page<Author> getSearchResultWithPaging(Author author, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher
                .matching() // and条件
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // Like句
                .withIgnoreCase(); // 大文字小文字の両方

        return authorRepository.findAll(Example.of(author, matcher), pageable);
    }

    /** 1件を検索して返す */
    @Transactional(readOnly = true)
    public Author getAuthor(Integer id) {
        return authorRepository.findById(id).get();
    }

    /** 著者名で検索して返す */
    @Transactional(readOnly = true)
    public List<Author> findByName(String name) {
        return authorRepository.findByName(name);
    }

    /** まんがから検索 */
    @Transactional(readOnly = true)
    public List<Author> getByManga(Manga manga) {
        return authorRepository.findByMangaAuthorsManga(manga);
    }

    /** 登録処理 */
    @Transactional
    public Author saveAuthor (Author author) {
        return authorRepository.save(author);
    }

}
