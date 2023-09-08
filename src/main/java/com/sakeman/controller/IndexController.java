package com.sakeman.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sakeman.entity.Manga;
import com.sakeman.entity.User;
import com.sakeman.service.AuthorService;
import com.sakeman.service.LikeService;
import com.sakeman.service.MangaService;
import com.sakeman.service.ReadStatusService;
import com.sakeman.service.ReviewService;
import com.sakeman.service.UclistService;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserFollowService;
import com.sakeman.service.UserService;


@Controller("index")
public class IndexController {

    private final MangaService maService;
    private final UserService userService;
    private final AuthorService authService;
    private final ReviewService reService;
    private final LikeService likeService;
    private final ReadStatusService rsService;
    private final UserFollowService ufService;
    private final UclistService uclService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public IndexController(MangaService maService, UserService userService, AuthorService authService, ReviewService reService, LikeService likeService, ReadStatusService rsService, UserFollowService ufService, UclistService uclService) {
        this.maService = maService;
        this.userService = userService;
        this.authService = authService;
        this.reService = reService;
        this.likeService = likeService;
        this.rsService = rsService;
        this.ufService = ufService;
        this.uclService = uclService;
    }

    /** トップページを表示 */
    @GetMapping("/")
    public String getList(@AuthenticationPrincipal UserDetail userDetail, @ModelAttribute Manga manga, Model model, @PageableDefault(page=0, size=10, sort= {"registeredAt"}, direction=Direction.DESC) Pageable pageable) {
        model.addAttribute("reviewlist", reService.getReviewListPageable(pageable));
        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("followeelist", ufService.followeeIdListFollowedByUser(userDetail));
        model.addAttribute("uclistlist", uclService.getUclistListPageable(pageable));
        return "index";
    }

    /** 検索結果を表示 */
    @PostMapping("/search")
    public String getSearchResult(@ModelAttribute Manga manga, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        model.addAttribute("searchResult", maService.getSearchResult(manga));
        model.addAttribute("reviewlist", reService.getReviewList());
        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        return "search";
    }

    /** 管理トップを表示 */
    @GetMapping("/admin/index")
    public String getAdmin() {
        return "admin/index";
    }

    /** 新規ユーザー登録（画面表示） */
    @GetMapping("signup")
    public String getSignup(@ModelAttribute User user, Model model) {
        return "signup";
    }

    /** 新規ユーザー登録（登録処理） */
    @PostMapping("/signup")
    public String postSignup(@Validated User user, BindingResult res, Model model, HttpServletRequest request, RedirectAttributes attrs) {
        if(res.hasErrors()) {
            return getSignup(user, model);
        }
        try {
            String rowpass = user.getPassword();

            user.setDeleteFlag(0);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(User.Role.一般);
            userService.saveUser(user);

            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = context.getAuthentication();
            if (authentication instanceof AnonymousAuthenticationToken == false) {
                SecurityContextHolder.clearContext();
            }
            try {
                request.login(user.getEmail(), rowpass);
            } catch (ServletException e) {
                e.printStackTrace();
            }
        } catch (DataAccessException e) {
            model.addAttribute("signupError", "このメールアドレスは既に登録されています。");
            return "signup";
        }
        attrs.addFlashAttribute("success", "ユーザー登録が完了しました！");
        return "redirect:/";
    }

}

