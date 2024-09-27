package com.sakeman.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Author;
import com.sakeman.entity.Tag;


public interface TagRepository extends JpaRepository<Tag, Integer> {
    Page<Tag> findAll(Pageable pageable);
    List<Tag> findByTagname(String tagname);
    Page<Tag> findAll(Specification<Tag> spec, Pageable pageable);


}
