package com.sakeman.controller.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WebMangaFollowController {

    private final WebMangaFollowService wmfService;
    private final UserService userService;
    private final MangaService maService;

    @PutMapping("/webmangafollow")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> webMangaFollow(@AuthenticationPrincipal UserDetail userDetail, @RequestBody Manga manga) {

        Integer userId = userDetail.getUser().getId();
        Integer maId = manga.getId();
        User user = userService.getUser(userId);
        Manga ma = maService.getManga(maId);
        Optional<WebMangaFollow> thiswmf = wmfService.findByUserAndManga(user, manga);

        boolean isFollowing;
        if (thiswmf.isPresent()) {
            Integer id = thiswmf.get().getId();
            wmfService.deleteById(id);
            isFollowing = false;
        } else {
            WebMangaFollow wmf = new WebMangaFollow();
            wmf.setUser(user);
            wmf.setManga(ma);
            wmfService.saveWebMangaFollow(wmf);
            isFollowing = true;
        }
        Map<String, Object> response = new HashMap<>();
        response.put("isFollowing", isFollowing);
        return ResponseEntity.ok(response);
    //        /** like数をカウント */
    //        int wmfcount = wmfService.countByManga(ma);
    //        System.out.println(wmfcount);


    }

}
