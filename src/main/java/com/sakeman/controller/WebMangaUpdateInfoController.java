package com.sakeman.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.sakeman.entity.Manga;
import com.sakeman.entity.User;
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

    /** 一覧を表示 */
    @GetMapping("")
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

        int followflag = (thisUser != null) ? thisUser.getWebMangaSettingsFollowflag() : 0;
        int freeflag = (thisUser != null) ? thisUser.getWebMangaSettingsFreeflag() : 0;
        int oneshotflag = (thisUser != null) ? thisUser.getWebMangaSettingsOneshotflag() : 0;
        List<Integer> genreSetting = (thisUser != null) ? thisUser.getWebMangaSettingsGenre() : new ArrayList<>();
        boolean isGenreEmpty = genreSetting == null || genreSetting.isEmpty();
        if (isGenreEmpty) {
            genreSetting = Collections.singletonList(0);  // 空リストが渡されるとエラーになるため、ダミーで0を設定
        }

//        Page<WebMangaUpdateInfoProjectionBasic> result = webService.getFilteredInfoListPageableSpecific(thisUser, freeflag, followflag, oneshotflag, genreSetting, pageable, true);

//ここから
        List<Integer> freeflagsSetting = freeflag == 0 ? List.of(0, 1, 2) : List.of(1);

//        Page<WebMangaUpdateInfoProjectionBasic> result;
//        if (userDetail == null) {
//            result = webService.getInfoListPageableProjection(pageable, true);
//        } else if (genreSetting.isEmpty() && followflag == 0) {
//            result = webService.getFilteredInfoListPageable(freeflagsSetting, pageable, true);
//        } else if (genreSetting.isEmpty() && followflag == 1) {
//            result = webService.getFilteredInfoListPageable(freeflagsSetting, userDetail.getUser().getId(), pageable, true);
//        } else if (!genreSetting.isEmpty() && followflag == 0) {
//            result = webService.getFilteredInfoListPageable(genreSetting, freeflagsSetting, pageable, true);
//        } else {
//            result = webService.getFilteredInfoListPageable(genreSetting, freeflagsSetting, userDetail.getUser().getId(), pageable, true);
//        }
// ここまで

        Page<WebMangaUpdateInfoProjectionBasic> result = webService.getFiltered(genreSetting, isGenreEmpty, freeflagsSetting, followflag, userDetail.getUser().getId(), oneshotflag, pageable, true);

        if (result.isEmpty() && pageable.getPageNumber() > 0) {
            return "redirect:/web-manga-update-info?page=0";
        }

        model.addAttribute("pages", result);
        model.addAttribute("infolist", result.getContent());

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
    @GetMapping("/manga/{id}")
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

