package com.sakeman.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sakeman.entity.Author;
import com.sakeman.entity.Manga;
import com.sakeman.entity.Review;
import com.sakeman.entity.Uclist;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.service.AuthorService;
import com.sakeman.service.LikeService;
import com.sakeman.service.MangaService;
import com.sakeman.service.ReadStatusService;
import com.sakeman.service.ReviewService;
import com.sakeman.service.UclistService;
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
    private final AuthorService authorService;
    private final UclistService uclService;


    /** リスト表示 (全作品) **/
    @GetMapping({"/list", "/list/{tab}"})
    public String getList(@AuthenticationPrincipal UserDetail userDetail,
                          @PathVariable(name="tab", required=false) String tab,
                          @RequestParam(name="page", required=false, defaultValue = "1") int page,
                          Model model,
                          @ModelAttribute Manga manga) {

        if (tab==null) tab = "recent";
        if (page < 1) {
            return String.format("redirect:/manga/list/%s", tab);
        }

        Author author = null;

        Pageable pageable = getPageable(tab, page);
        Page<Manga> mangalistPage = service.getMangaListPageable(pageable);

        if (page > Math.max(mangalistPage.getTotalPages(), 1)) {
            return String.format("redirect:/manga/list/%s", tab);
        }

        model.addAttribute("mangaPages", mangalistPage);
        model.addAttribute("mangalist", mangalistPage.getContent());

        model.addAttribute("reviewlist", revService.getReviewList());
        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("tab", tab);
        model.addAttribute("author", author);

        return "manga/list";
    }

    /** リスト表示 (著者ごと) **/
    @GetMapping({"/list-author/{author-id}", "/list-author/{author-id}/{tab}"})
    public String getListAuthor(@AuthenticationPrincipal UserDetail userDetail,
                                @PathVariable(name="tab", required=false) String tab,
                                @PathVariable(name="author-id", required=true) Integer authorId,
                                @RequestParam(name="page", required=false, defaultValue = "1") int page,
                                Model model,
                                @ModelAttribute Manga manga,
                                HttpServletRequest request) {

        if (tab==null) tab = "recent";
        if (page < 1) {
            return String.format("redirect:/manga/list-author/%s/%s", authorId, tab);
        }

        Pageable pageable = getPageable(tab, page);
        Page<Manga> mangalistPage = service.getMangaByAuthorId(authorId, pageable);

        if (page > Math.max(mangalistPage.getTotalPages(), 1)) {
            return String.format("redirect:/manga/list-author/%s/%s", authorId, tab);
        }

        model.addAttribute("mangaPages", mangalistPage);
        model.addAttribute("mangalist", mangalistPage.getContent());

        model.addAttribute("reviewlist", revService.getReviewList());
        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("tab", tab);
        model.addAttribute("author", authorService.getAuthor(authorId));

        return "manga/list";
    }


    /** 詳細表示 */
    @GetMapping({"/detail/{id}", "/detail/{id}/{tab}"})
    public String getDetail(@AuthenticationPrincipal UserDetail userDetail,
                            @PathVariable("id") Integer id,
                            @PathVariable(name="tab", required=false) String tab,
                            Model model,
                            @PageableDefault(page=0, size=5, sort= {"updateAt"},
                            direction=Direction.DESC) Pageable pageable) {

        if (tab==null) tab = "info";
        Page<WebMangaUpdateInfo> infolistPage = infoService.findByMangaIdPageable(id, pageable, true);

        model.addAttribute("manga", service.getMangaOrThrow(id));
        model.addAttribute("infopage", infolistPage);
        model.addAttribute("infolist", infolistPage.getContent());
        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("followeelist", ufService.followeeIdListFollowedByUser(userDetail));
        model.addAttribute("likelist", webLikeService.webMangaUpdateInfoIdListWebLikedByUser(userDetail));
        model.addAttribute("webmangafollowlist", wmfService.webMangaIdListLikedByUser(userDetail));

        model.addAttribute("tab", tab);

        if (tab.equals("info")) {
            List<Author> authors = authorService.getByManga(service.getManga(id));

            Pageable pageableEd = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "updatedAt"));

            /** 著者ごとに出す **/
            List<List<Object>> allList = new ArrayList<>();
            for (Author author : authors) {
                Page<Manga> listPage = service.getMangaByAuthorId(author.getId(), pageableEd);
                List<Manga> listContent = listPage.getContent();
                List<Object> combinedList = new ArrayList<>();
                combinedList.add(listPage);
                combinedList.add(listContent);
                combinedList.add(author);
                allList.add(combinedList);
            }
            model.addAttribute("allList", allList);
        } else if (tab.equals("review")) {
            Pageable pageableEd = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "updatedAt"));
            Page<Review> reviewlistPage = revService.getReviewByMangaIdPageable(id, pageableEd);
            Page<Uclist> ucllistPage = uclService.getByMangaPageable(service.getManga(id), pageableEd);
            model.addAttribute("reviewpage", reviewlistPage);
            model.addAttribute("reviewlist", reviewlistPage.getContent());
            model.addAttribute("uclpage", ucllistPage);
            model.addAttribute("uclistlist", ucllistPage.getContent());
        }

        return "manga/detail";
    }


    @GetMapping("/minilist")
    public String getMiniList(Model model) {
        model.addAttribute("mangalist", service.getMangaList());
        return "manga/minilist";
    }

    /** Pageableオブジェクトの取得 */
    public Pageable getPageable(String tab, int page) {
        Pageable pageable;
        switch (tab) {
            case "recent":
                pageable = PageRequest.of(page-1, 20, Sort.by(Sort.Direction.DESC, "updatedAt"));
                break;
            case "popular":
                pageable = PageRequest.of(page-1, 20, Sort.by(Sort.Direction.DESC, "readStatus"));
                break;
            default:
                pageable = PageRequest.of(page-1, 20, Sort.by(Sort.Direction.DESC, "updatedAt"));
                break;
        }
        return pageable;
    }

//    private static <T> Set<T> getFirstNElements(Set<T> set, int n) {
//        Set<T> result = new HashSet<>();
//        int count = 0;
//
//        for (T element : set) {
//            if (count >= n) {
//                break;
//            }
//
//            result.add(element);
//            count++;
//        }
//
//        return result;
//    }

}
