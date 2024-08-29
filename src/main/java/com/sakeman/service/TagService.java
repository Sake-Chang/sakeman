package com.sakeman.service;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Author;
import com.sakeman.entity.Manga;
import com.sakeman.entity.Tag;
import com.sakeman.repository.AuthorRepository;
import com.sakeman.repository.TagRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    /** 全件を検索して返す **/
    @Transactional(readOnly = true)
    public List<Tag> getTagList() {
        return tagRepository.findAll();
    }

    /** ページネーション */
    @Transactional(readOnly = true)
    public Page<Tag> getTagListPageable(Pageable pageable){
        return tagRepository.findAll(pageable);
    }

    /** 検索結果 */
    @Transactional(readOnly = true)
    public List<Tag> getSearchResult(Tag tag) {
        ExampleMatcher matcher = ExampleMatcher
                .matching() // and条件
                .withStringMatcher(StringMatcher.CONTAINING) // Like句
                .withIgnoreCase(); // 大文字小文字の両方
        return tagRepository.findAll(Example.of(tag, matcher));
    }

    /** 1件を検索して返す */
    @Transactional(readOnly = true)
    public Tag getTag(Integer id) {
        return tagRepository.findById(id).get();
    }

    /** 著者名で検索して返す */
    @Transactional(readOnly = true)
    public List<Tag> findByTagname(String tagname) {
        return tagRepository.findByTagname(tagname);
    }

    /** 登録処理 */
    @Transactional
    public Tag saveTag (Tag tag) {
        return tagRepository.save(tag);
    }

}
