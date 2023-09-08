package com.sakeman.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.WebMangaTitleConverter;


public interface WebMangaTitleConverterRepository extends JpaRepository<WebMangaTitleConverter, Integer> {
    Optional<WebMangaTitleConverter> findByTitleStringAndAuthorString(String titleString, String authorString);
}
