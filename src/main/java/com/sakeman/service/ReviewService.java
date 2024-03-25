package com.sakeman.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Manga;
import com.sakeman.entity.Review;
import com.sakeman.repository.ReviewRepository;


@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository repository) {
        this.reviewRepository = repository;
    }

    /** 全件を検索して返す **/
    public List<Review> getReviewList() {
        return reviewRepository.findAll();
    }

    /** ページネーション */
    public Page<Review> getReviewListPageable(Pageable pageable){
        return reviewRepository.findAll(pageable);
    }

    /** 1件を検索して返す */
    public Review getReview(Integer id) {
        return reviewRepository.findById(id).get();
    }

    /** マンガIDで検索して返す */
    public List<Review> getReviewByMangaId(Integer id) {
        return reviewRepository.findByMangaId(id);
    }

    /** マンガIDで検索して返す(Pageable) */
    public Page<Review> getReviewByMangaIdPageable(Integer id, Pageable pageable) {
        return reviewRepository.findByMangaId(id, pageable);
    }

    /** 著者で検索 **/
    public Page<Review> getDistinctByMangaMangaAuthorsAuthorId(Integer aId, Pageable pageable) {
        return reviewRepository.findDistinctByMangaMangaAuthorsAuthorId(aId, pageable);
    }

    /** 登録処理 */
    @Transactional
    public Review saveReview (Review review) {
        return reviewRepository.save(review);
    }

}
