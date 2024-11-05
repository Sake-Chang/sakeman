package com.sakeman.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Manga;
import com.sakeman.entity.Review;
import com.sakeman.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    /** 全件を検索して返す **/
    @Transactional(readOnly = true)
    public List<Review> getReviewList() {
        return reviewRepository.findAll();
    }

    /** ページネーション */
    @Transactional(readOnly = true)
    public Page<Review> getReviewListPageable(Pageable pageable){
        return reviewRepository.findAll(pageable);
    }

    /** 1件を検索して返す */
    @Transactional(readOnly = true)
    public Review getReview(Integer id) {
        return reviewRepository.findById(id).get();
    }

    /** マンガIDで検索して返す */
    @Transactional(readOnly = true)
    @Cacheable(value = "review", key = "'allEntries'")
    public List<Review> getReviewByMangaId(Integer id) {
        return reviewRepository.findByMangaId(id);
    }

    /** マンガIDで検索して返す(Pageable) */
    @Transactional(readOnly = true)
    public Page<Review> getReviewByMangaIdPageable(Integer id, Pageable pageable) {
        return reviewRepository.findByMangaId(id, pageable);
    }

    /** 著者で検索 **/
    @Transactional(readOnly = true)
    public Page<Review> getDistinctByMangaMangaAuthorsAuthorId(Integer aId, Pageable pageable) {
        return reviewRepository.findDistinctByMangaMangaAuthorsAuthorId(aId, pageable);
    }

    /** 登録処理 */
    @Transactional
    @CacheEvict(value = {"review"}, allEntries = true)
    public Review saveReview (Review review) {
        return reviewRepository.save(review);
    }

}
