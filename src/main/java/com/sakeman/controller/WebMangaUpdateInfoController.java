package com.sakeman.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sakeman.entity.Manga;
import com.sakeman.entity.User;
import com.sakeman.entity.UserWebMangaSetting;
import com.sakeman.entity.WebMangaMedia;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.entity.projection.webmanga.WebMangaUpdateInfoProjectionBasic;
import com.sakeman.service.GenreService;
import com.sakeman.service.MangaService;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserService;
import com.sakeman.service.WebLikeService;
import com.sakeman.service.WebMangaFollowService;
import com.sakeman.service.WebMangaMediaService;
import com.sakeman.service.WebMangaUpdateInfoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/web-manga-update-info")
@RequiredArgsConstructor
public class WebMangaUpdateInfoController {

    private final WebMangaUpdateInfoService webService;
    private final MangaService maService;
    private final WebLikeService  webLikeService;
    private final WebMangaFollowService wmfService;
    private final WebMangaMediaService mediaService;
    private final GenreService genreService;
    private final UserService userService;
    private final int PAGE_SIZE_SHORT = 25;
    private final int PAGE_SIZE_LONG = 50;
    private final Sort DEFAULT_SORT = Sort.by(Sort.Order.desc("updateAt"),Sort.Order.asc("webMangaMedia"),Sort.Order.asc("titleString"),Sort.Order.desc("freeFlag"));


    /** 一覧を表示 */
    @GetMapping("")
    public String getList(  @ModelAttribute Manga manga,
                            @AuthenticationPrincipal UserDetail userDetail,
                            @RequestParam(defaultValue = "1") int page,
                            Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE_SHORT, DEFAULT_SORT);

        User thisUser = (userDetail != null) ? userService.getUser(userDetail.getUser().getId()) : null;
        UserWebMangaSetting thisSetting;
        if (thisUser == null) {
            thisSetting = new UserWebMangaSetting();
        } else {
            thisSetting = thisUser.getUserWebMangaSetting();
        }

        int followflag = (thisSetting != null) ? thisSetting.getWebMangaSettingsFollowflag() : 0;
        int freeflag = (thisSetting != null) ? thisSetting.getWebMangaSettingsFreeflag() : 0;
        int oneshotflag = (thisSetting != null) ? thisSetting.getWebMangaSettingsOneshotflag() : 0;
        List<Integer> genreSetting = (thisUser != null) ? thisUser.getGenreIdsExist() : new ArrayList<>();
        boolean isGenreEmpty = genreSetting == null || genreSetting.isEmpty();
        if (isGenreEmpty) {
            genreSetting = Collections.singletonList(0);
        }

        List<Integer> freeflagsSetting = freeflag == 0 ? List.of(0, 1, 2) : List.of(1);

        Page<WebMangaUpdateInfoProjectionBasic> result;
        if (userDetail == null  || (freeflag == 0 && followflag == 0 && oneshotflag == 0 && isGenreEmpty)) {
            result = webService.getInfoListPageableProjection(pageable, true);
        } else {
            result = webService.getFiltered(genreSetting, isGenreEmpty, freeflagsSetting, followflag, userDetail.getUser().getId(), oneshotflag, pageable, true);
        }

        if (result.isEmpty() && pageable.getPageNumber() > 0) {
            return "redirect:/web-manga-update-info?page=1";
        }

        model.addAttribute("pages", result);
        model.addAttribute("infolist", result.getContent());

        if (page > result.getTotalPages() && !result.isEmpty()) {
            redirectAttributes.addAttribute("page", 1);
            return "redirect:/web-manga-update-info";
        }

        model.addAttribute("likelist", webLikeService.webMangaUpdateInfoIdListWebLikedByUser(userDetail));
        model.addAttribute("webmangafollowlist", wmfService.webMangaIdListLikedByUser(userDetail));
        model.addAttribute("medialist", mediaService.getWebMangaMediaList());
        model.addAttribute("genrelist", genreService.getGenreListOrdered());

        model.addAttribute("genreflag", genreSetting);
        model.addAttribute("freeflag", freeflag);
        model.addAttribute("followflag", followflag);
        model.addAttribute("oneshotflag", oneshotflag);

        LocalDateTime today = LocalDateTime.now(ZoneId.of("Asia/Tokyo")).toLocalDate().atStartOfDay();
        model.addAttribute("todaylistsize", webService.getTodayInfoList(today, true).size());

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "module/card/info-card-module :: info-card-module";
        } else {
            return "web-manga-update-info/list";
        }

    }

    /** 設定＆設定の保存 */
    @PostMapping("")
    @PreAuthorize("isAuthenticated")
    @ResponseBody
    public ResponseEntity<String> postWebmangaSetting(
            @RequestParam(name="genres", required=false) List<Integer> genres,
            @RequestParam(name="freeflag", required=false, defaultValue="0") Integer freeflag,
            @RequestParam(name="followflag", required=false, defaultValue="0") Integer followflag,
            @RequestParam(name="oneshotflag", required=false, defaultValue="0") Integer oneshotflag,
            @AuthenticationPrincipal UserDetail userDetail,
            HttpServletRequest request) {
        /** ユーザーの設定を保存 */
        userService.saveSettings(userDetail, genres, freeflag, followflag, oneshotflag);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("redirect:/web-manga-update-info", HttpStatus.FOUND);
        }
    }

    /** メディアIDごと一覧を表示 */
    @GetMapping("/media/{id}")
    public String getListByMediaId(@ModelAttribute Manga manga,
                                   @PathVariable("id") Integer mediaId,
                                   @AuthenticationPrincipal UserDetail userDetail,
                                   @RequestParam(defaultValue = "1") int page,
                                   Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        Sort sort = Sort.by(
                Sort.Order.desc("updateAt"),
                Sort.Order.asc("webMangaMedia")
            );
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE_LONG, sort);

        WebMangaMedia media = mediaService.getWebMangaMedia(mediaId);

        Page<WebMangaUpdateInfo> result = webService.findByMediaIdPageable(mediaId, pageable, true);
        model.addAttribute("pages", result);
        model.addAttribute("infolist", result.getContent());

        if (page > result.getTotalPages()) {
            redirectAttributes.addAttribute("page", 1);
            return "redirect:/web-manga-update-info/media/" + mediaId;
        }

        model.addAttribute("likelist", webLikeService.webMangaUpdateInfoIdListWebLikedByUser(userDetail));
        model.addAttribute("webmangafollowlist", wmfService.webMangaIdListLikedByUser(userDetail));
        model.addAttribute("medialist", mediaService.getWebMangaMediaList());
        model.addAttribute("media", media);

        LocalDateTime today = LocalDateTime.now(ZoneId.of("Asia/Tokyo")).toLocalDate().atStartOfDay();
        model.addAttribute("todaylistsize", webService.getTodayInfoList(today, true).size());

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "module/card/info-card-module :: info-card-module";
        } else {
            return "web-manga-update-info/list";
        }
    }

    /** マンガIDごと一覧を表示 */
    @GetMapping("/manga/{id}")
    public String getListByMangaId(@ModelAttribute Manga manga,
                                   @PathVariable("id") Integer mangaId,
                                   @AuthenticationPrincipal UserDetail userDetail,
                                   @RequestParam(defaultValue = "1") int page,
                                   Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        Sort sort = Sort.by(
                Sort.Order.desc("updateAt"),
                Sort.Order.asc("webMangaMedia")
            );
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE_LONG, sort);

        Manga mangaobj = maService.getManga(mangaId);

        Page<WebMangaUpdateInfo> result = webService.findByMangaIdPageable(mangaId, pageable, true);
        model.addAttribute("pages", result);
        model.addAttribute("infolist", result.getContent());

        if (page > result.getTotalPages()) {
            redirectAttributes.addAttribute("page", 1);
            return "redirect:/web-manga-update-info/manga/" + mangaId;
        }

        model.addAttribute("likelist", webLikeService.webMangaUpdateInfoIdListWebLikedByUser(userDetail));
        model.addAttribute("webmangafollowlist", wmfService.webMangaIdListLikedByUser(userDetail));
        model.addAttribute("medialist", mediaService.getWebMangaMediaList());
        model.addAttribute("mangaobj", mangaobj);

        LocalDateTime today = LocalDateTime.now(ZoneId.of("Asia/Tokyo")).toLocalDate().atStartOfDay();
        model.addAttribute("todaylistsize", webService.getTodayInfoList(today, true).size());

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "module/card/info-card-module :: info-card-module";
        } else {
            return "web-manga-update-info/list";
        }
    }
}

