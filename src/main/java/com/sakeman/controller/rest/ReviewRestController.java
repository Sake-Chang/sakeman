package com.sakeman.controller.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sakeman.entity.Manga;
import com.sakeman.entity.ReadStatus;
import com.sakeman.entity.Review;
import com.sakeman.entity.User;
import com.sakeman.service.MangaService;
import com.sakeman.service.ReadStatusService;
import com.sakeman.service.ReviewService;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping
@RequiredArgsConstructor
public class ReviewRestController {

    private final ReviewService service;

    @PostMapping("/updateRating")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateOrCreateRating(@AuthenticationPrincipal UserDetail userDetail, @RequestBody Map<String, Object> requestData) {
        Integer userId = userDetail.getUser().getId();
        Integer mangaId = (Integer) requestData.get("mangaId");
        Integer rating = (Integer) requestData.get("rating");

        service.updateOrCreateReview(userId, mangaId, rating);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/review/check")
    public ResponseEntity<Map<String, Object>> checkReview(@RequestParam Integer mangaId, @RequestParam Integer userId) {
        Optional<Review> reviewOpt = service.getReviewByUserIdAndMangaId(userId, mangaId);

        Map<String, Object> response = new HashMap<>();
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            response.put("exists", true);
            response.put("id", review.getId());
            response.put("rating", review.getRating());
            response.put("title", review.getTitle());
            response.put("content", review.getContent());
        } else {
            response.put("exists", false);
        }
        return ResponseEntity.ok(response);
    }

}
