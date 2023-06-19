package com.sakeman.controller.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sakeman.entity.User;
import com.sakeman.entity.UserFollow;
import com.sakeman.service.LikeService;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserFollowService;
import com.sakeman.service.UserService;


@RestController
@RequestMapping
public class UserFollowRestController {

    private LikeService likeService;
    private UserService userService;
    private UserFollowService ufService;

    public UserFollowRestController(LikeService likeService, UserService userService, UserFollowService ufService) {
        this.likeService = likeService;
        this.userService = userService;
        this.ufService = ufService;

    }

    @PutMapping("/follow")
    @ResponseBody
    public int createLike(@AuthenticationPrincipal UserDetail userDetail, @RequestBody User objUser, Model model) {

        Integer followerId = userDetail.getUser().getId();
        Integer followeeId = objUser.getId();
        User follower = userService.getUser(followerId);
        User followee = userService.getUser(followeeId);
        UserFollow uf = ufService.findByFollowerAndFollowee(follower, followee);

        // Followしてた場合
        if (uf != null) {
            Integer id = uf.getId();
            ufService.deleteById(id);

        // まだFollowしてない場合
        } else {
            UserFollow userFollow = new UserFollow();
            userFollow.setFollower(follower);
            userFollow.setFollowee(followee);
            ufService.saveUserFollow(userFollow);
        }

        return 0;
    }

}
