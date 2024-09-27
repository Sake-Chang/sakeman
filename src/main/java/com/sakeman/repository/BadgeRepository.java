package com.sakeman.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Author;
import com.sakeman.entity.Badge;

public interface BadgeRepository extends JpaRepository<Badge, Integer> {
    Page<Badge> findAll(Pageable pageable);
    Page<Badge> findAll(Specification<Badge> spec, Pageable pageable);

}
