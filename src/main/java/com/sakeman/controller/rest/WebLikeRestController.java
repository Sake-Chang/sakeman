package com.sakeman.controller.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.sakeman.entity.WebLike;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.service.LikeService;
import com.sakeman.service.ReviewService;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserService;
import com.sakeman.service.WebLikeService;
import com.sakeman.service.WebMangaUpdateInfoService;


@RestController
@RequestMapping
public class WebLikeRestController {

    private WebLikeService webLikeService;
    private UserService userService;
    private WebMangaUpdateInfoService infoService;

    public WebLikeRestController(WebLikeService webLikeService, UserService userService, WebMangaUpdateInfoService infoService) {
        this.webLikeService = webLikeService;
        this.userService = userService;
        this.infoService = infoService;

    }

    @PutMapping("/webLike")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createWebLike(@AuthenticationPrincipal UserDetail userDetail, @RequestBody WebMangaUpdateInfo info, Model model) {

        Integer userId = userDetail.getUser().getId();
        Integer infoId = info.getId();
        User user = userService.getUser(userId);
        WebMangaUpdateInfo webMangaUpdateInfo = infoService.getWebMangaUpdateInfo(infoId);
        Optional<WebLike> thisweblike = webLikeService.findByUserAndWebMangaUpdateInfo(user, webMangaUpdateInfo);

        boolean isLiked;
        if (thisweblike.isPresent()) {
            Integer id = thisweblike.get().getId();
            webLikeService.deleteById(id);
            isLiked = false;
        } else {
            WebLike webLike = new WebLike();
            webLike.setUser(user);
            webLike.setWebMangaUpdateInfo(info);
            webLikeService.saveWebLike(webLike);
            isLiked = true;
        }

        Map<String, Object> response = new HashMap<>();
        int weblikecount = webLikeService.countByWebMangaUpdateInfo(info);
        response.put("weblikecount", weblikecount);
        response.put("isLiked", isLiked);
        return ResponseEntity.ok(response);
    }

}
