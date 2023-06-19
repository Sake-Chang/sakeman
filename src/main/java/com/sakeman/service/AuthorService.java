package com.sakeman.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Author;
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

    /** 1件を検索して返す */
    public Author getAuthor(Integer id) {
        return authorRepository.findById(id).get();
    }

    /** 著者名で検索して返す */
    public List<Author> findByName(String name) {
        return authorRepository.findByName(name);
    }

    /** 登録処理 */
    @Transactional
    public Author saveAuthor (Author author) {
        return authorRepository.save(author);
    }

}
