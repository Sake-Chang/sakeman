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


@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository repository) {
        this.authorRepository = repository;
    }

    /** 全件を検索して返す **/
    public List<Author> getAuthorList() {
        return authorRepository.findAll();
    }

    /** ページネーション */
    public Page<Author> getAuthorListPageable(Pageable pageable){
        return authorRepository.findAll(pageable);
    }

    /** 検索結果 */
    @Transactional
    public List<Author> getSearchResult(Author author) {
        ExampleMatcher matcher = ExampleMatcher
                .matching() // and条件
                .withStringMatcher(StringMatcher.CONTAINING) // Like句
                .withIgnoreCase(); // 大文字小文字の両方
        return authorRepository.findAll(Example.of(author, matcher));
    }

    /** 1件を検索して返す */
    public Author getAuthor(Integer id) {
        return authorRepository.findById(id).get();
    }

    /** 著者名で検索して返す */
    public List<Author> findByName(String name) {
        return authorRepository.findByName(name);
    }

    /** まんがから検索 */
    public List<Author> getByManga(Manga manga) {
        return authorRepository.findByMangaAuthorsManga(manga);
    }

    /** 登録処理 */
    @Transactional
    public Author saveAuthor (Author author) {
        return authorRepository.save(author);
    }

}
