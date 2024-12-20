package com.sakeman.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.sakeman.entity.Manga;
import com.sakeman.entity.Review;
import com.sakeman.entity.Uclist;
import com.sakeman.form.SearchbarForm;
import com.sakeman.service.LikeService;
import com.sakeman.service.MangaService;
import com.sakeman.service.ReadStatusService;
import com.sakeman.service.ReviewService;
import com.sakeman.service.UclistService;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserFollowService;
import com.sakeman.service.WebMangaUpdateInfoService;

import lombok.RequiredArgsConstructor;


@Controller("index")
@RequiredArgsConstructor
public class IndexController {

    private final MangaService maService;
    private final ReviewService reService;
    private final LikeService likeService;
    private final ReadStatusService rsService;
    private final UserFollowService ufService;
    private final UclistService uclService;
    private final WebMangaUpdateInfoService webService;

    /** トップページを表示 */
    @GetMapping("/")
    public String getList(@AuthenticationPrincipal UserDetail userDetail, Model model, @PageableDefault(page=0, size=10, sort= {"registeredAt"}, direction=Direction.DESC) Pageable pageable) {
        Page<Uclist> uclPage = uclService.getUclistListPageable(pageable);
//        Page<Review> reviewPage = reService.getReviewListPageable(pageable);
        Page<Review> reviewPage = reService.findByTitleIsNotNullOrContentIsNotNull(pageable);

        model.addAttribute("uclPage", uclPage);
        model.addAttribute("uclistlist", uclPage.getContent());
        model.addAttribute("reviewPage", reviewPage);
        model.addAttribute("reviewlist", reviewPage.getContent());
        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("followeelist", ufService.followeeIdListFollowedByUser(userDetail));

        model.addAttribute("todaylistsize", webService.getTodayInfoList(true).size());

        return "index";
    }

    @GetMapping("/search")
    public String getSearchResult(@ModelAttribute SearchbarForm searchbarForm,
                                  @RequestParam(required = false) String inputKeyword,
                                  @RequestParam(name="page", required=false, defaultValue = "1") int page,
                                  @AuthenticationPrincipal UserDetail userDetail,
                                  Model model) {

        String[] keywords = searchbarForm.getKeywords();

        if (page < 1) {
            String encodedKeyword = URLEncoder.encode(String.join(" ", keywords), StandardCharsets.UTF_8);
            return String.format("redirect:/search?inputKeyword=%s", encodedKeyword);
        }

        Pageable pageable = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.ASC, "updatedAt"));
        Page<Manga> searchResultPage = maService.searchManga(keywords, pageable);

        if (page > Math.max(searchResultPage.getTotalPages(), 1)) {
            String encodedKeyword = URLEncoder.encode(String.join(" ", keywords), StandardCharsets.UTF_8);
            return String.format("redirect:/search?inputKeyword=%s", encodedKeyword);
        }

        model.addAttribute("searchResultPage", searchResultPage);
        model.addAttribute("searchResult", searchResultPage.getContent());
        model.addAttribute("reviewlist", reService.getReviewList());
        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("query", String.join(" ", keywords));
        return "search";
    }

    /** 管理トップを表示 */
    @GetMapping("/admin/index")
    public String getAdmin() {
        return "admin/index";
    }

}

