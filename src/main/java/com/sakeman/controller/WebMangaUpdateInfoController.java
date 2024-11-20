package com.sakeman.controller;

import java.time.LocalDateTime;
import com.sakeman.utils.WebUtils;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.sakeman.entity.Manga;
import com.sakeman.entity.User;
import com.sakeman.entity.UserWebMangaSetting;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.entity.projection.webmanga.WebMangaUpdateInfoProjectionBasic;
import com.sakeman.service.GenreService;
import com.sakeman.service.MangaService;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserService;
import com.sakeman.service.UserWebMangaSettingService;
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
    private final UserWebMangaSettingService settingService;

    private final int PAGE_SIZE_SHORT = 25;
    private final int PAGE_SIZE_LONG = 50;
    private final Sort DEFAULT_SORT = Sort.by(Sort.Order.desc("updateAt"),Sort.Order.asc("webMangaMedia"),Sort.Order.asc("titleString"),Sort.Order.desc("freeFlag"));


    /** 一覧を表示 */
    @GetMapping("")
    public String getList(  @ModelAttribute Manga manga,
                            @AuthenticationPrincipal UserDetail userDetail,
                            @RequestParam(name="page", required=false, defaultValue = "1") int page,
                            Model model, HttpServletRequest request) {

        if (page < 1) {
            return "redirect:/web-manga-update-info";
        }

        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE_SHORT, DEFAULT_SORT);

        User thisUser = userService.getUserFromUseerDetail(userDetail).orElse(null);
        UserWebMangaSetting thisSetting = userService.getUserWebMangaSetting(thisUser).orElseGet(UserWebMangaSetting::new);
        List<Integer> genreIdsExist = settingService.getGenreIdsExist(thisSetting);

        Page<WebMangaUpdateInfoProjectionBasic> result = settingService.isDefaultSetting(thisSetting, genreIdsExist)
                ? webService.getInfoListPageableProjection(pageable, true)
                : webService.getFiltered(thisSetting, genreIdsExist, userDetail.getUser().getId(), pageable, true);

        if (page > Math.max(result.getTotalPages(), 1)) {
            return "redirect:/web-manga-update-info";
        }

        model.addAttribute("pages", result);
        model.addAttribute("infolist", result.getContent());

        model.addAttribute("likelist", webLikeService.webMangaUpdateInfoIdListWebLikedByUser(userDetail));
        model.addAttribute("webmangafollowlist", wmfService.webMangaIdListLikedByUser(userDetail));
        model.addAttribute("medialist", mediaService.getWebMangaMediaList());
        model.addAttribute("genrelist", genreService.getGenreListOrdered());

        model.addAttribute("userSettings", thisSetting);
        model.addAttribute("genreIdsExist", genreIdsExist);

        model.addAttribute("todaylistsize", webService.getTodayInfoList(true).size());

        return WebUtils.isAjaxRequest(request)
                ? "module/card/info-card-module :: info-card-module"
                : "web-manga-update-info/list";
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
                                   @RequestParam(name="page", required=false, defaultValue = "1") int page,
                                   Model model, HttpServletRequest request) {

        if (page < 1) {
            return String.format("redirect:/web-manga-update-info/media/%s", mediaId) ;
        }

        Sort sort = Sort.by(Sort.Order.desc("updateAt"), Sort.Order.asc("webMangaMedia"));
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE_LONG, sort);

        Page<WebMangaUpdateInfo> result = webService.findByMediaIdPageable(mediaId, pageable, true);
        model.addAttribute("pages", result);
        model.addAttribute("infolist", result.getContent());

        if (page > Math.max(result.getTotalPages(), 1)) {
            return String.format("redirect:/web-manga-update-info/media/%s", mediaId) ;
        }

        model.addAttribute("likelist", webLikeService.webMangaUpdateInfoIdListWebLikedByUser(userDetail));
        model.addAttribute("webmangafollowlist", wmfService.webMangaIdListLikedByUser(userDetail));
        model.addAttribute("medialist", mediaService.getWebMangaMediaList());
        model.addAttribute("media", mediaService.getWebMangaMedia(mediaId));

        model.addAttribute("todaylistsize", webService.getTodayInfoList(true).size());

        return WebUtils.isAjaxRequest(request)
                ? "module/card/info-card-module :: info-card-module"
                : "web-manga-update-info/list";
    }

    /** マンガIDごと一覧を表示 */
    @GetMapping("/manga/{id}")
    public String getListByMangaId(@ModelAttribute Manga manga,
                                   @PathVariable("id") Integer mangaId,
                                   @AuthenticationPrincipal UserDetail userDetail,
                                   @RequestParam(name="page", required=false, defaultValue = "1") int page,
                                   Model model, HttpServletRequest request) {

        if (page < 1) {
            return String.format("redirect:/web-manga-update-info/manga/%s", mangaId) ;
        }

        Sort sort = Sort.by(Sort.Order.desc("updateAt"), Sort.Order.asc("webMangaMedia"));
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE_LONG, sort);

        Page<WebMangaUpdateInfo> result = webService.findByMangaIdPageable(mangaId, pageable, true);
        model.addAttribute("pages", result);
        model.addAttribute("infolist", result.getContent());

        if (page > Math.max(result.getTotalPages(), 1)) {
            return String.format("redirect:/web-manga-update-info/manga/%s", mangaId) ;
        }

        model.addAttribute("likelist", webLikeService.webMangaUpdateInfoIdListWebLikedByUser(userDetail));
        model.addAttribute("webmangafollowlist", wmfService.webMangaIdListLikedByUser(userDetail));
        model.addAttribute("medialist", mediaService.getWebMangaMediaList());
        model.addAttribute("mangaobj", maService.getManga(mangaId));

        model.addAttribute("todaylistsize", webService.getTodayInfoList(true).size());

        return WebUtils.isAjaxRequest(request)
                ? "module/card/info-card-module :: info-card-module"
                : "web-manga-update-info/list";
    }
}

