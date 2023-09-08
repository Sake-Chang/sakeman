package com.sakeman.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Author;
import com.sakeman.entity.AuthorFollow;
import com.sakeman.entity.User;


public interface AuthorFollowRepository extends JpaRepository<AuthorFollow, Integer> {
    List<AuthorFollow> findByUser(User user);
    AuthorFollow findByUserAndAuthor(User user, Author author);
}
