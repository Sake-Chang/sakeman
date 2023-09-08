package com.sakeman.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sakeman.entity.Manga;
import com.sakeman.service.LikeService;
import com.sakeman.service.MangaService;
import com.sakeman.service.ReadStatusService;
import com.sakeman.service.ReviewService;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserFollowService;
import com.sakeman.service.WebLikeService;
import com.sakeman.service.WebMangaFollowService;
import com.sakeman.service.WebMangaUpdateInfoService;

import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("manga")
@RequiredArgsConstructor
public class MangaController {
    private final MangaService service;
    private final ReviewService revService;
    private final LikeService likeService;
    private final ReadStatusService rsService;
    private final UserFollowService ufService;
    private final WebMangaUpdateInfoService infoService;
    private final WebLikeService  webLikeService;
    private final WebMangaFollowService wmfService;

    /** 一覧表示（新着順） **/
    @GetMapping("")
    public String getListNew(@AuthenticationPrincipal UserDetail userDetail, Model model, @ModelAttribute Manga manga, @PageableDefault(page=0, size=20, sort= {"registeredAt"}, direction=Direction.DESC) Pageable pageable) {
        model.addAttribute("pages", service.getMangaListPageable(pageable));
        model.addAttribute("mangalist", service.getMangaListPageable(pageable).getContent());
        model.addAttribute("reviewlist", revService.getReviewList());
        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        return "manga/list";
    }

    /** 一覧表示（読みたい順） **/
    @GetMapping("/popular")
    public String getListPop(@AuthenticationPrincipal UserDetail userDetail, Model model, @ModelAttribute Manga manga, @PageableDefault(page=0, size=20, sort= {"readStatus"}, direction=Direction.DESC) Pageable pageable) {
        model.addAttribute("pages", service.getMangaListPageable(pageable));
        model.addAttribute("mangalist", service.getMangaListPageable(pageable).getContent());
        model.addAttribute("reviewlist", revService.getReviewList());
        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        return "manga/list-popular";
    }

    /** 詳細表示（おすすめ） */
    @GetMapping("/{id}")
    public String getDetail(@AuthenticationPrincipal UserDetail userDetail, @PathVariable("id") Integer id, Model model, @PageableDefault(page=0, size=10, sort= {"updateAt"}, direction=Direction.DESC) Pageable pageable) {
        model.addAttribute("manga", service.getManga(id));
        model.addAttribute("infolist", infoService.findByMangaIdPageable(id, pageable).getContent());
        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("followeelist", ufService.followeeIdListFollowedByUser(userDetail));
        model.addAttribute("likelist", webLikeService.webMangaUpdateInfoIdListWebLikedByUser(userDetail));
        model.addAttribute("webmangafollowlist", wmfService.webMangaIdListLikedByUser(userDetail));

        return "manga/detail";
        }

    /** 詳細表示（レビュー） */
    @GetMapping("/review/{id}")
    public String getDetailReview(@AuthenticationPrincipal UserDetail userDetail, @PathVariable("id") Integer id, Model model) {
        model.addAttribute("manga", service.getManga(id));
        model.addAttribute("reviewlist", revService.getReviewByMangaId(id));
        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("followeelist", ufService.followeeIdListFollowedByUser(userDetail));
        return "manga/detail-review";
        }

    @GetMapping("/minilist")
    public String getMiniList(Model model) {
        model.addAttribute("mangalist", service.getMangaList());
        return "manga/minilist";
    }

}
