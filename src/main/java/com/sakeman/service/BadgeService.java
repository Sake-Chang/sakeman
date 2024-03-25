package com.sakeman.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Author;
import com.sakeman.entity.Badge;
import com.sakeman.repository.BadgeRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class BadgeService {
    private final BadgeRepository repository;

    /** 全件（選択肢用） */
    public List<Badge> getAll(){
        return repository.findAll();
    }

    /** 全件（ページ） */
    public Page<Badge> getAllPageable(Pageable pageable){
        return repository.findAll(pageable);
    }

    public Optional<Badge> getById(Integer id) {
        return repository.findById(id);
    }

    /** 検索結果 */
    @Transactional
    public List<Badge> getSearchResult(Badge badge) {
        ExampleMatcher matcher = ExampleMatcher
                .matching() // and条件
                .withStringMatcher(StringMatcher.CONTAINING) // Like句
                .withIgnoreCase(); // 大文字小文字の両方
        return repository.findAll(Example.of(badge, matcher));
    }

    /** 登録処理 */
    @Transactional
    public Badge saveBadge (Badge badge) {
        return repository.save(badge);
    }

}
