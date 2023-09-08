package com.sakeman.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
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
import com.sakeman.entity.WebMangaFollow;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.service.AuthorService;
import com.sakeman.service.LikeService;
import com.sakeman.service.MangaService;
import com.sakeman.service.ReadStatusService;
import com.sakeman.service.ReviewService;
import com.sakeman.service.UclistService;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserFollowService;
import com.sakeman.service.UserService;
import com.sakeman.service.WebLikeService;
import com.sakeman.service.WebMangaFollowService;
import com.sakeman.service.WebMangaMediaService;
import com.sakeman.service.WebMangaUpdateInfoService;

import lombok.RequiredArgsConstructor;


@Controller("/")
@RequiredArgsConstructor
public class WebMangaUpdateInfoController {

    private final WebMangaUpdateInfoService webService;
    private final MangaService maService;
    private final WebLikeService  webLikeService;
    private final WebMangaFollowService wmfService;
    private final WebMangaMediaService mediaService;

    /** 一覧を表示 */
    @GetMapping("/web-manga-update-info")
    public String getList(@ModelAttribute Manga manga, Model model, @AuthenticationPrincipal UserDetail userDetail, @PageableDefault(page = 0, size = 50) @SortDefault.SortDefaults({@SortDefault(sort="updateAt", direction=Direction.DESC), @SortDefault(sort="webMangaMedia", direction=Direction.ASC), @SortDefault(sort="id", direction=Direction.DESC)}) Pageable pageable) {
        model.addAttribute("likelist", webLikeService.webMangaUpdateInfoIdListWebLikedByUser(userDetail));
        model.addAttribute("pages", webService.getInfoListPageable(pageable));
        model.addAttribute("infolist", webService.getInfoListPageable(pageable).getContent());
        model.addAttribute("webmangafollowlist", wmfService.webMangaIdListLikedByUser(userDetail));
        model.addAttribute("medialist", mediaService.getWebMangaMediaList());

        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(0, 0, 0);
        LocalDateTime today = LocalDateTime.of(date, time);
        model.addAttribute("todaylistsize", webService.getTodayInfoList(today).size());

        return "web-manga-update-info/list";
    }

    /** 一覧（フォローのみ）を表示 */
    @GetMapping("/web-manga-update-info/follow")
    public String getFollowList(@ModelAttribute Manga manga, Model model, @AuthenticationPrincipal UserDetail userDetail, @PageableDefault(page = 0, size = 20) @SortDefault.SortDefaults({@SortDefault(sort="updateAt", direction=Direction.DESC), @SortDefault(sort="webMangaMedia", direction=Direction.ASC), @SortDefault(sort="id", direction=Direction.DESC)}) Pageable pageable) {
        //Pageable pageable = PageRequest.of(0, 20, Sort.by("updateAt").descending().and(Sort.by("webMangaMedia.id").ascending()).and(Sort.by("id").descending()));
        Integer userId = userDetail.getUser().getId();
        List<WebMangaFollow> followlist = wmfService.findByUserId(userId);
        List<Integer> mangaIds = new ArrayList<>();
        for (int i=0; i<followlist.size(); i++) {
            Integer mangaId = followlist.get(i).getManga().getId();
            mangaIds.add(mangaId);
        }

        model.addAttribute("pages", webService.findByMangaIdIn(mangaIds, pageable));
        model.addAttribute("infolist", webService.findByMangaIdIn(mangaIds, pageable).getContent());
        model.addAttribute("likelist", webLikeService.webMangaUpdateInfoIdListWebLikedByUser(userDetail));
        model.addAttribute("webmangafollowlist", wmfService.webMangaIdListLikedByUser(userDetail));
        model.addAttribute("medialist", mediaService.getWebMangaMediaList());

        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(0, 0, 0);
        LocalDateTime today = LocalDateTime.of(date, time);
        model.addAttribute("todaylistsize", webService.getTodayInfoList(today).size());

        return "web-manga-update-info/list-follow";
    }


    /** 無料のみ一覧を表示 */
    @GetMapping("/web-manga-update-info/free")
    public String getFreeList(@ModelAttribute Manga manga, Model model, Integer freeFlag, @AuthenticationPrincipal UserDetail userDetail, @PageableDefault(page = 0, size = 50) @SortDefault.SortDefaults({@SortDefault(sort="updateAt", direction=Direction.DESC), @SortDefault(sort="webMangaMedia", direction=Direction.ASC), @SortDefault(sort="id", direction=Direction.DESC)}) Pageable pageable) {
        //Pageable pageable = PageRequest.of(0, 50, Sort.by("updateAt").descending().and(Sort.by("webMangaMedia.id").ascending()).and(Sort.by("id").descending()));
        model.addAttribute("likelist", webLikeService.webMangaUpdateInfoIdListWebLikedByUser(userDetail));
        model.addAttribute("pages", webService.getFreeInfoListPageable(1, pageable));
        model.addAttribute("infolist", webService.getFreeInfoListPageable(1, pageable).getContent());
        model.addAttribute("webmangafollowlist", wmfService.webMangaIdListLikedByUser(userDetail));
        model.addAttribute("medialist", mediaService.getWebMangaMediaList());

        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(0, 0, 0);
        LocalDateTime today = LocalDateTime.of(date, time);
        model.addAttribute("todaylistsize", webService.getTodayInfoList(today).size());

        return "web-manga-update-info/list-free";
    }

    /** 一覧を表示 (今日更新のみ) */
    @GetMapping("/web-manga-update-info/today")
    public String getTodayList(@ModelAttribute Manga manga, Model model, Integer freeFlag, @AuthenticationPrincipal UserDetail userDetail, @PageableDefault(page = 0, size = 50) @SortDefault.SortDefaults({@SortDefault(sort="updateAt", direction=Direction.DESC), @SortDefault(sort="webMangaMedia", direction=Direction.ASC), @SortDefault(sort="id", direction=Direction.DESC)}) Pageable pageable) {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(0, 0, 0);
        LocalDateTime today = LocalDateTime.of(date, time);

        //Pageable pageable = PageRequest.of(0, 50, Sort.by("updateAt").descending().and(Sort.by("webMangaMedia.id").ascending()).and(Sort.by("id").descending()));
        model.addAttribute("likelist", webLikeService.webMangaUpdateInfoIdListWebLikedByUser(userDetail));
        model.addAttribute("pages", webService.getTodayInfoListPageable(today, pageable));
        model.addAttribute("infolist", webService.getTodayInfoListPageable(today, pageable).getContent());
        model.addAttribute("webmangafollowlist", wmfService.webMangaIdListLikedByUser(userDetail));
        model.addAttribute("medialist", mediaService.getWebMangaMediaList());

        model.addAttribute("todaylistsize", webService.getTodayInfoList(today).size());

        return "web-manga-update-info/list-today";
    }

    /** メディアIDごと一覧を表示 */
    @GetMapping("/web-manga-update-info/media/{id}")
    public String getListByMediaId(@ModelAttribute Manga manga, Model model, @PathVariable("id") Integer mediaId, @AuthenticationPrincipal UserDetail userDetail, @PageableDefault(page = 0, size = 50) @SortDefault.SortDefaults({@SortDefault(sort="updateAt", direction=Direction.DESC), @SortDefault(sort="webMangaMedia", direction=Direction.ASC), @SortDefault(sort="id", direction=Direction.DESC)}) Pageable pageable) {
        //Pageable pageable = PageRequest.of(0, 50, Sort.by("updateAt").descending().and(Sort.by("webMangaMedia.id").ascending()).and(Sort.by("id").descending()));
        model.addAttribute("likelist", webLikeService.webMangaUpdateInfoIdListWebLikedByUser(userDetail));
        model.addAttribute("pages", webService.findByMediaIdPageable(mediaId, pageable));
        model.addAttribute("infolist", webService.findByMediaIdPageable(mediaId, pageable).getContent());
        model.addAttribute("webmangafollowlist", wmfService.webMangaIdListLikedByUser(userDetail));
        model.addAttribute("medialist", mediaService.getWebMangaMediaList());

        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(0, 0, 0);
        LocalDateTime today = LocalDateTime.of(date, time);
        model.addAttribute("todaylistsize", webService.getTodayInfoList(today).size());

        return "web-manga-update-info/list";
    }

    /** マンガIDごと一覧を表示 */
    @GetMapping("/web-manga-update-info/manga/{id}")
    public String getListByMangaId(@ModelAttribute Manga manga, Model model, @PathVariable("id") Integer mangaId, @AuthenticationPrincipal UserDetail userDetail, @PageableDefault(page = 0, size = 50) @SortDefault.SortDefaults({@SortDefault(sort="updateAt", direction=Direction.DESC), @SortDefault(sort="webMangaMedia", direction=Direction.ASC), @SortDefault(sort="id", direction=Direction.DESC)}) Pageable pageable) {
        //Pageable pageable = PageRequest.of(0, 50, Sort.by("updateAt").descending().and(Sort.by("webMangaMedia.id").ascending()).and(Sort.by("id").descending()));
        model.addAttribute("likelist", webLikeService.webMangaUpdateInfoIdListWebLikedByUser(userDetail));
        model.addAttribute("pages", webService.findByMangaIdPageable(mangaId, pageable));
        model.addAttribute("infolist", webService.findByMangaIdPageable(mangaId, pageable).getContent());
        model.addAttribute("webmangafollowlist", wmfService.webMangaIdListLikedByUser(userDetail));
        model.addAttribute("medialist", mediaService.getWebMangaMediaList());

        return "web-manga-update-info/list";
    }
}

