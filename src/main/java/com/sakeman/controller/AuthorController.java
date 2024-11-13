package com.sakeman.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sakeman.entity.Manga;
import com.sakeman.entity.Review;
import com.sakeman.entity.Uclist;
import com.sakeman.service.AuthorFollowService;
import com.sakeman.service.AuthorService;
import com.sakeman.service.LikeService;
import com.sakeman.service.MangaService;
import com.sakeman.service.ReadStatusService;
import com.sakeman.service.ReviewService;
import com.sakeman.service.UclistService;
import com.sakeman.service.UserDetail;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("author")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService service;
    private final AuthorFollowService afService;
    private final ReadStatusService rsService;
    private final MangaService mangaService;
    private final ReviewService revService;
    private final UclistService uclService;
    private final LikeService likeService;

    /** 一覧表示（Pageable） */
//    @GetMapping("/list")
//    public String getList(Model model,  @PageableDefault(page=0, size=100, sort= {"id"}, direction=Direction.ASC) Pageable pageable) {
//        model.addAttribute("pages", service.getAuthorListPageable(pageable));
//        model.addAttribute("authorlist", service.getAuthorListPageable(pageable).getContent());
//        return "author/list";
//        }

    /** 詳細表示 */
    @GetMapping({"/{id}", "/{id}/{tab}"})
    public String getDetail(@PathVariable("id") Integer id,
                            @PathVariable(name = "tab", required = false) String tab,
                            Model model,
                            @AuthenticationPrincipal UserDetail userDetail,
                            @PageableDefault(page=0, size=10, sort= {"readStatus"},
                            direction=Direction.DESC) Pageable pageable) {

        if (tab == null) tab = "info";
        List<Manga> mangalistOrg = mangaService.getMangaByAuthorId(id);
        Collections.shuffle(mangalistOrg);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), mangalistOrg.size());

        Page<Manga> mangalistPage = new PageImpl<>(mangalistOrg.subList(start, end), pageable, mangalistOrg.size());

        model.addAttribute("author", service.getAuthor(id));
        model.addAttribute("mangalistPage", mangalistPage);
        model.addAttribute("mangalist", mangalistPage.getContent());
        model.addAttribute("authorlist", afService.authorIdListFollowedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("tab", tab);

        if (tab.equals("review")) {
            Pageable pageableEd = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "updatedAt"));

            Page<Review> reviewlistPage = revService.getDistinctByMangaMangaAuthorsAuthorId(id, pageableEd);
            model.addAttribute("reviewlistPaage", reviewlistPage);
            model.addAttribute("reviewlist", reviewlistPage.getContent());

            Page<Uclist> uclistlistPage = uclService.gettDistinctByUclistMangasMangaMangaAuthorsAuthorId(id, pageableEd);
            model.addAttribute("uclistlistPage", uclistlistPage);
            model.addAttribute("uclistlist", uclistlistPage.getContent());
        }

        return "author/detail";
        }

}