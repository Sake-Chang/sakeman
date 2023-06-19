package com.sakeman.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Author;


public interface AuthorRepository extends JpaRepository<Author, Integer> {
    public List<Author> findByName(String name);

}
