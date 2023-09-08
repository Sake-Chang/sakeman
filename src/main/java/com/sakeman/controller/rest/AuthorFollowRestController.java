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

import com.sakeman.entity.Author;
import com.sakeman.entity.AuthorFollow;
import com.sakeman.entity.User;
import com.sakeman.entity.UserFollow;
import com.sakeman.service.AuthorFollowService;
import com.sakeman.service.AuthorService;
import com.sakeman.service.LikeService;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserFollowService;
import com.sakeman.service.UserService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthorFollowRestController {

    private final UserService userService;
    private final AuthorService authorService;
    private final AuthorFollowService afService;

    @PutMapping("/author/follow")
    @ResponseBody
    public int followAuthor(@AuthenticationPrincipal UserDetail userDetail, @RequestBody Author author, Model model) {

        Integer userId = userDetail.getUser().getId();
        Integer authorId = author.getId();
        User user = userService.getUser(userId);
        Author auth = authorService.getAuthor(authorId);
        AuthorFollow af = afService.findByUserAndAuthor(user, auth);

        // Followしてた場合
        if (af != null) {
            Integer id = af.getId();
            afService.deleteById(id);

        // まだFollowしてない場合
        } else {
            AuthorFollow authorFollow = new AuthorFollow();
            authorFollow.setUser(user);
            authorFollow.setAuthor(auth);
            afService.save(authorFollow);
        }

        return 0;
    }

}
