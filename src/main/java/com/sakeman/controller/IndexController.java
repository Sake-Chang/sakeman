package com.sakeman.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
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

import com.sakeman.entity.BadgeUser;
import com.sakeman.entity.Manga;
import com.sakeman.entity.Review;
import com.sakeman.entity.Uclist;
import com.sakeman.entity.User;
import com.sakeman.service.AuthorService;
import com.sakeman.service.BadgeService;
import com.sakeman.service.BadgeUserService;
import com.sakeman.service.EmailService;
import com.sakeman.service.LikeService;
import com.sakeman.service.MangaService;
import com.sakeman.service.ReadStatusService;
import com.sakeman.service.ReviewService;
import com.sakeman.service.UclistService;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserFollowService;
import com.sakeman.service.UserService;
import com.sakeman.service.WebMangaUpdateInfoService;

import lombok.RequiredArgsConstructor;


@Controller("index")
@RequiredArgsConstructor
public class IndexController {

    private final MangaService maService;
    private final UserService userService;
    private final AuthorService authService;
    private final ReviewService reService;
    private final LikeService likeService;
    private final ReadStatusService rsService;
    private final UserFollowService ufService;
    private final UclistService uclService;
    private final WebMangaUpdateInfoService webService;
    private final BadgeService badgeService;
    private final BadgeUserService buService;
    private final EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** トップページを表示 */
    @GetMapping("/")
    public String getList(@AuthenticationPrincipal UserDetail userDetail, @ModelAttribute Manga manga, Model model, @PageableDefault(page=0, size=10, sort= {"registeredAt"}, direction=Direction.DESC) Pageable pageable) {
        Page<Uclist> uclPage = uclService.getUclistListPageable(pageable);
        Page<Review> reviewPage = reService.getReviewListPageable(pageable);

        model.addAttribute("uclPage", uclPage);
        model.addAttribute("uclistlist", uclPage.getContent());
        model.addAttribute("reviewPage", reviewPage);
        model.addAttribute("reviewlist", reviewPage.getContent());
        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("followeelist", ufService.followeeIdListFollowedByUser(userDetail));

        LocalDate date = LocalDate.now(ZoneId.of("Asia/Tokyo"));
        LocalTime time = LocalTime.of(0, 0, 0);
        LocalDateTime today = LocalDateTime.of(date, time);
        model.addAttribute("todaylistsize", webService.getTodayInfoList(today).size());

        return "index";
    }

//    /** 検索結果を表示 */
//    @GetMapping("/search")
//    public String getSearchResult(@ModelAttribute Manga manga, @AuthenticationPrincipal UserDetail userDetail, Model model, @PageableDefault(page=0, size=20, sort= {"registeredAt"}, direction=Direction.DESC) Pageable pageable) {
//        String query = manga.getTitle();
//        Page<Manga> searchResultPage = maService.getSearchResult(manga, pageable);
//        model.addAttribute("searchResultPage", searchResultPage);
//        model.addAttribute("searchResult", searchResultPage.getContent());
//        model.addAttribute("reviewlist", reService.getReviewList());
//        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
//        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
//        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
//        model.addAttribute("query", query);
//        return "search";
//    }

    @GetMapping("/search")
    public String getSearchResult(@ModelAttribute Manga manga, @AuthenticationPrincipal UserDetail userDetail, Model model, @PageableDefault(page=0, size=20, sort= {"registeredAt"}, direction=Direction.DESC) Pageable pageable) {
        String query = manga.getTitle();
        String[] keywords = query.split("[ 　]+");
        Page<Manga> searchResultPage = maService.searchManga(keywords, pageable);
        model.addAttribute("searchResultPage", searchResultPage);
        model.addAttribute("searchResult", searchResultPage.getContent());
        model.addAttribute("reviewlist", reService.getReviewList());
        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("query", query);
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

            // 追加ここから
            user.setEnabled(false);
            Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
            user.setVerificationToken(UUID.randomUUID().toString());
            user.setVerificationTokenStatus(User.VerificationTokenStatus.VALID);
            user.setVerificationTokenExpiration(expirationDate);
            // 追加ここまで

            userService.saveUser(user);

            // 追加ここから
            User regUser = userService.getByEmail(user.getEmail());
            // メール認証用のリンク
            String verificationLink = userService.getVerificationLink(request, regUser.getVerificationToken());
            // メール送信
            emailService.sendVerificationEmail(user.getEmail(), verificationLink);
            // 追加ここまで

        } catch (DataAccessException e) {
            model.addAttribute("signupError", "このメールアドレスは既に登録されています。");
            return "signup";
        }
        return "redirect:/confirm";
    }

    @GetMapping("/confirm")
    public String cofirm() {
        return "confirm";
    }

}

