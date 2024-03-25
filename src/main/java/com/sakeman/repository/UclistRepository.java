package com.sakeman.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Manga;
import com.sakeman.entity.Uclist;


public interface UclistRepository extends JpaRepository<Uclist, Integer> {
    Page<Uclist> findAll(Pageable pageable);
    List<Uclist> findByUclistMangasManga(Manga manga);
    Page<Uclist> findByUclistMangasManga(Manga manga, Pageable pageable);
    Page<Uclist> findByUclistMangasMangaId(Integer mangaId, Pageable pageable);

    Page<Uclist> findDistinctByUclistMangasMangaMangaAuthorsAuthorId(Integer aId, Pageable pageable);

}
