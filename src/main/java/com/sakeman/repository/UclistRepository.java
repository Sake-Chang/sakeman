package com.sakeman.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Uclist;


public interface UclistRepository extends JpaRepository<Uclist, Integer> {
    Page<Uclist> findAll(Pageable pageable);

}
