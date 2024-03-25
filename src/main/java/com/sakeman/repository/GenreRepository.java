package com.sakeman.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Genre;
import com.sakeman.entity.Tag;


public interface GenreRepository extends JpaRepository<Genre, Integer> {
    Page<Genre> findAll(Pageable pageable);
    List<Genre> findByName(String name);
    List<Genre> findAllByOrderByDisplayOrder();

}
