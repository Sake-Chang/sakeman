package com.sakeman.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Manga;
import com.sakeman.entity.ReadStatus;
import com.sakeman.entity.ReadStatus.Status;
import com.sakeman.entity.User;


public interface ReadStatusRepository extends JpaRepository<ReadStatus, Integer> {
    Optional<ReadStatus> findByUserAndManga(User user, Manga manga);
    List<ReadStatus> findByUserId(Integer userId);
    Optional<ReadStatus> findByUser(User user);
    List<ReadStatus> findByUserAndStatus(User user, Status status);
}
