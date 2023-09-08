package com.sakeman.controller.rest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sakeman.entity.Like;
import com.sakeman.entity.Review;
import com.sakeman.entity.User;
import com.sakeman.service.LikeService;
import com.sakeman.service.ReviewService;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserService;


@RestController
@RequestMapping
public class LikeRestController {

    private LikeService likeService;
    private UserService userService;
    private ReviewService revService;

    public LikeRestController(LikeService likeService, UserService userService, ReviewService revService) {
        this.likeService = likeService;
        this.userService = userService;
        this.revService = revService;

    }

    @PutMapping("/like")
    @ResponseBody
    public int createLike(@AuthenticationPrincipal UserDetail userDetail, @RequestBody Review review, Model model) {

        Integer userId = userDetail.getUser().getId();
        Integer revId = review.getId();
        User user = userService.getUser(userId);
        Review rev = revService.getReview(revId);
        Optional<Like> thislike = likeService.findByUserAndReview(user, rev);

        // 既にLikeしてた場合
        if (thislike.isPresent()) {
            Integer id = thislike.get().getId();
            likeService.deleteById(id);

        // まだLikeしてない場合
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setReview(rev);
            likeService.saveLike(like);
        }

        /** like数をカウント */
        int likecount = likeService.countByReview(rev);
        System.out.println(likecount);
//
//        Optional<Like> likes = likeService.findByReview(rev);
//        System.out.println(likes);

        return likecount;
    }

}
