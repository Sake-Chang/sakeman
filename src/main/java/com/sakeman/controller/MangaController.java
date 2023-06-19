package com.sakeman.controller;

import org.springframework.beans.factory.annotation.Autowired;
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


@Controller
@RequestMapping("manga")
public class MangaController {
    private final MangaService service;
    private final ReviewService revService;
    private final LikeService likeService;
    private final ReadStatusService rsService;
    private final UserFollowService ufService;


    public MangaController(MangaService service, ReviewService revService, LikeService likeService, ReadStatusService rsService, UserFollowService ufService) {
        this.service = service;
        this.revService = revService;
        this.likeService = likeService;
        this.rsService = rsService;
        this.ufService = ufService;
    }

    /** 一覧表示（新着順） **/
    @GetMapping("")
    public String getListNew(@AuthenticationPrincipal UserDetail userDetail, Model model, @ModelAttribute Manga manga, @PageableDefault(page=0, size=20, sort= {"updatedAt"}, direction=Direction.DESC) Pageable pageable) {
        model.addAttribute("mangalist", service.getMangaListPageable(pageable));
        model.addAttribute("reviewlist", revService.getReviewList());
        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        return "manga/list";
    }

    /** 一覧表示（読みたい順） **/
    @GetMapping("/popular")
    public String getListPop(@AuthenticationPrincipal UserDetail userDetail, Model model, @ModelAttribute Manga manga, @PageableDefault(page=0, size=20, sort= {"readStatus"}, direction=Direction.DESC) Pageable pageable) {
        model.addAttribute("mangalist", service.getMangaListPageable(pageable));
        model.addAttribute("reviewlist", revService.getReviewList());
        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        return "manga/list-popular";
    }

    /** 詳細表示 */
    @GetMapping("/{id}")
    public String getDetail(@AuthenticationPrincipal UserDetail userDetail, @PathVariable("id") Integer id, Model model) {
        model.addAttribute("manga", service.getManga(id));
        model.addAttribute("reviewlist", revService.getReviewByMangaId(id));
        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("followeelist", ufService.followeeIdListFollowedByUser(userDetail));
        return "manga/detail";
        }

    @GetMapping("/minilist")
    public String getMiniList(Model model) {
        model.addAttribute("mangalist", service.getMangaList());
        return "manga/minilist";
    }

//    /** 作品ごとのレビューを表示 */
//    @GetMapping("{id}/review")
//    public String getDetailReviews(@PathVariable("id") Integer id, Model model) {
//        model.addAttribute("reviewlist", revService.getReviewByMangaId(id));
//        model.addAttribute("manga", service.getManga(id));
//        return "manga/detail/review";
//    }

}
