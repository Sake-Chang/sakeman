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
import com.sakeman.entity.BadgeUser;
import com.sakeman.entity.Manga;
import com.sakeman.entity.Tag;
import com.sakeman.entity.User;
import com.sakeman.repository.AuthorRepository;
import com.sakeman.repository.BadgeUserRepository;
import com.sakeman.repository.TagRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class BadgeUserService {
    private final BadgeUserRepository repository;

    /** ページネーション */
    public Page<BadgeUser> getAllPageable(Pageable pageable){
        return repository.findAll(pageable);
    }

    public Optional<BadgeUser> getById(Integer id) {
        return repository.findById(id);
    }

    public List<BadgeUser> getByUserId(Integer userId) {
        return repository.findByUserId(userId);
    }

    public Optional<BadgeUser> getByUserIdAndBadgeId(Integer uId, Integer bId) {
        return repository.findByUserIdAndBadgeId(uId, bId);
    }

    /** 登録処理 */
    @Transactional
    public BadgeUser saveBadgeUser (BadgeUser badgeUser) {
        return repository.save(badgeUser);
    }

}
