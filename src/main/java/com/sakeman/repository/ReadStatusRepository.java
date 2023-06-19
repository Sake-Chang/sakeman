package com.sakeman.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Manga;
import com.sakeman.entity.ReadStatus;
import com.sakeman.entity.ReadStatus.Status;
import com.sakeman.entity.User;


public interface ReadStatusRepository extends JpaRepository<ReadStatus, Integer> {
    public Optional<ReadStatus> findByUserAndManga(User user, Manga manga);
    public List<ReadStatus> findByUserId(Integer userId);
    public Optional<ReadStatus> findByUser(User user);
    public List<ReadStatus> findByUserAndStatus(User user, Status status);
}
