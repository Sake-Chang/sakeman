package com.sakeman.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Author;


public interface AuthorRepository extends JpaRepository<Author, Integer> {
    Page<Author> findAll(Pageable pageable);
    List<Author> findByName(String name);


}
