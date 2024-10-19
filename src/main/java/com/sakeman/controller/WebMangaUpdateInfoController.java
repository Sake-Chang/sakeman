package com.sakeman.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sakeman.entity.Manga;
import com.sakeman.entity.User;
import com.sakeman.entity.WebMangaMedia;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.service.GenreService;
import com.sakeman.service.MangaService;
import com.sakeman.service.TagService;
import com.sakeman.service.UserDetail;
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
    private final GenreService genreService;
    private final UserService userService;

    /** 一覧を表示 */
    @GetMapping("/web-manga-update-info")
    public String getList(@ModelAttribute Manga manga, Model model,
            @AuthenticationPrincipal UserDetail userDetail,
            @PageableDefault(page = 0, size = 25)
                @SortDefault.SortDefaults({
                    @SortDefault(sort="updateAt", direction=Direction.DESC),
                    @SortDefault(sort="webMangaMedia", direction=Direction.ASC),
                    @SortDefault(sort="titleString", direction=Direction.ASC),
                    @SortDefault(sort="freeFlag", direction=Direction.DESC),
                    @SortDefault(sort="id", direction=Direction.DESC)
                }) Pageable pageable,
                HttpServletRequest request) {

        User thisUser = (userDetail != null) ? userService.getUser(userDetail.getUser().getId()) : null;

        List<Integer> genres = (thisUser != null) ? thisUser.getWebMangaSettingsGenre() : new ArrayList<>();
        int followflag = (thisUser != null) ? thisUser.getWebMangaSettingsFollowflag() : 0;
        int freeflag = (thisUser != null) ? thisUser.getWebMangaSettingsFreeflag() : 0;
        List<Integer> freeflagsSetting = freeflag == 0 ? List.of(0, 1, 2) : List.of(1);

        Page<WebMangaUpdateInfo> result;
        if (userDetail == null) {
            result = webService.getInfoListPageable(pageable, true);
        } else if (genres.isEmpty() && followflag == 0) {
            result = webService.getFilteredInfoListPageable(freeflagsSetting, pageable, true);
        } else if (genres.isEmpty() && followflag == 1) {
            result = webService.getFilteredInfoListPageable(freeflagsSetting, userDetail.getUser().getId(), pageable, true);
        } else if (!genres.isEmpty() && followflag == 0) {
            result = webService.getFilteredInfoListPageable(genres, freeflagsSetting, pageable, true);
        } else {
            result = webService.getFilteredInfoListPageable(genres, freeflagsSetting, userDetail.getUser().getId(), pageable, true);
        }

        model.addAttribute("pages", result);
        model.addAttribute("infolist", result.getContent());

        model.addAttribute("likelist", webLikeService.webMangaUpdateInfoIdListWebLikedByUser(userDetail));
        model.addAttribute("webmangafollowlist", wmfService.webMangaIdListLikedByUser(userDetail));
        model.addAttribute("medialist", mediaService.getWebMangaMediaList());
        model.addAttribute("genrelist", genreService.getGenreListOrdered());

        model.addAttribute("genreflag", genres);
        model.addAttribute("freeflag", freeflag);
        model.addAttribute("followflag", followflag);

        LocalDateTime today = LocalDateTime.now(ZoneId.of("Asia/Tokyo")).toLocalDate().atStartOfDay();
        model.addAttribute("todaylistsize", webService.getTodayInfoList(today, true).size());

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "module/card/info-card-module :: info-card-module";
        } else {
            return "web-manga-update-info/list";
        }

    }

    /** 設定＆設定の保存 */
    @PostMapping("/web-manga-update-info")
    @PreAuthorize("isAuthenticated")
    public String getFilteredList(
            @RequestParam(name="genres", required=false) List<Integer> genres,
            @RequestParam(name="freeflag", required=false) Integer freeflag,
            @RequestParam(name="followflag", required=false) Integer followflag,
            @AuthenticationPrincipal UserDetail userDetail) {
        /** ユーザーの設定を保存 */
        userService.saveSettings(userDetail, genres, freeflag, followflag);
        return "redirect:/web-manga-update-info";
    }

    /** メディアIDごと一覧を表示 */
    @GetMapping("/web-manga-update-info/media/{id}")
    public String getListByMediaId(@ModelAttribute Manga manga,
                                   Model model,
                                   @PathVariable("id") Integer mediaId,
                                   @AuthenticationPrincipal UserDetail userDetail,
                                   @PageableDefault(page = 0, size = 50)
                                        @SortDefault.SortDefaults({@SortDefault(sort="updateAt", direction=Direction.DESC),
                                                                   @SortDefault(sort="webMangaMedia", direction=Direction.ASC),
                                                                   @SortDefault(sort="id", direction=Direction.DESC)})
                                   Pageable pageable) {

        WebMangaMedia media = mediaService.getWebMangaMedia(mediaId);

        Page<WebMangaUpdateInfo> result = webService.findByMediaIdPageable(mediaId, pageable, true);
        model.addAttribute("pages", result);
        model.addAttribute("infolist", result.getContent());

        model.addAttribute("likelist", webLikeService.webMangaUpdateInfoIdListWebLikedByUser(userDetail));
        model.addAttribute("webmangafollowlist", wmfService.webMangaIdListLikedByUser(userDetail));
        model.addAttribute("medialist", mediaService.getWebMangaMediaList());
        model.addAttribute("media", media);

        LocalDateTime today = LocalDateTime.now(ZoneId.of("Asia/Tokyo")).toLocalDate().atStartOfDay();
        model.addAttribute("todaylistsize", webService.getTodayInfoList(today, true).size());

        return "web-manga-update-info/list";
    }

    /** マンガIDごと一覧を表示 */
    @GetMapping("/web-manga-update-info/manga/{id}")
    public String getListByMangaId(@ModelAttribute Manga manga,
                                   Model model,
                                   @PathVariable("id") Integer mangaId,
                                   @AuthenticationPrincipal UserDetail userDetail,
                                   @PageableDefault(page = 0, size = 50)
                                        @SortDefault.SortDefaults({@SortDefault(sort="updateAt", direction=Direction.DESC),
                                                                   @SortDefault(sort="webMangaMedia", direction=Direction.ASC),
                                                                   @SortDefault(sort="id", direction=Direction.DESC)})
                                   Pageable pageable) {

        Manga mangaobj = maService.getManga(mangaId);

        Page<WebMangaUpdateInfo> result = webService.findByMangaIdPageable(mangaId, pageable, true);
        model.addAttribute("pages", result);
        model.addAttribute("infolist", result.getContent());

        model.addAttribute("likelist", webLikeService.webMangaUpdateInfoIdListWebLikedByUser(userDetail));
        model.addAttribute("webmangafollowlist", wmfService.webMangaIdListLikedByUser(userDetail));
        model.addAttribute("medialist", mediaService.getWebMangaMediaList());
        model.addAttribute("mangaobj", mangaobj);

        LocalDateTime today = LocalDateTime.now(ZoneId.of("Asia/Tokyo")).toLocalDate().atStartOfDay();
        model.addAttribute("todaylistsize", webService.getTodayInfoList(today, true).size());

        return "web-manga-update-info/list";
    }
}

