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
import com.sakeman.entity.Manga;
import com.sakeman.entity.Review;
import com.sakeman.entity.User;
import com.sakeman.entity.WebMangaFollow;
import com.sakeman.service.LikeService;
import com.sakeman.service.MangaService;
import com.sakeman.service.ReviewService;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserService;
import com.sakeman.service.WebMangaFollowService;


@RestController
@RequestMapping
public class WebMangaFollowController {

    private WebMangaFollowService wmfService;
    private UserService userService;
    private MangaService maService;

    public WebMangaFollowController(WebMangaFollowService wmfService, UserService userService, MangaService maService) {
        this.wmfService = wmfService;
        this.userService = userService;
        this.maService = maService;

    }

    @PutMapping("/webmangafollow")
    @ResponseBody
    public int webMangaFollow(@AuthenticationPrincipal UserDetail userDetail, @RequestBody Manga manga, Model model) {

        Integer userId = userDetail.getUser().getId();
        Integer maId = manga.getId();
        User user = userService.getUser(userId);
        Manga ma = maService.getManga(maId);
        Optional<WebMangaFollow> thiswmf = wmfService.findByUserAndManga(user, manga);

        // 既にLikeしてた場合
        if (thiswmf.isPresent()) {
            Integer id = thiswmf.get().getId();
            wmfService.deleteById(id);

        // まだLikeしてない場合
        } else {
            WebMangaFollow wmf = new WebMangaFollow();
            wmf.setUser(user);
            wmf.setManga(ma);
            wmfService.saveWebMangaFollow(wmf);
        }

        /** like数をカウント */
        int wmfcount = wmfService.countByManga(ma);
        System.out.println(wmfcount);
//
//        Optional<Like> likes = likeService.findByReview(rev);
//        System.out.println(likes);

        return wmfcount;
    }

}
