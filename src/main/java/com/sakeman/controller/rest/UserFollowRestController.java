package com.sakeman.controller.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sakeman.entity.User;
import com.sakeman.entity.UserFollow;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserFollowService;
import com.sakeman.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserFollowRestController {

    private final UserService userService;
    private final UserFollowService ufService;

    @PutMapping("/user/follow")
    @ResponseBody
    @PreAuthorize("isAuthenticated")
    public int createLike(@AuthenticationPrincipal UserDetail userDetail, @RequestBody User objUser, Model model) {

        Integer followerId = userDetail.getUser().getId();
        Integer followeeId = objUser.getId();
        User follower = userService.getUser(followerId);
        User followee = userService.getUser(followeeId);
        UserFollow uf = ufService.findByFollowerAndFollowee(follower, followee);

        if (uf != null) {
            Integer id = uf.getId();
            ufService.deleteById(id);
        } else {
            UserFollow userFollow = new UserFollow();
            userFollow.setFollower(follower);
            userFollow.setFollowee(followee);
            ufService.saveUserFollow(userFollow);
        }
        return 0;
    }

}
