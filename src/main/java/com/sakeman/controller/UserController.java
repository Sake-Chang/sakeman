package com.sakeman.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sakeman.dto.MangaForShelfDTO;
import com.sakeman.entity.Manga;
import com.sakeman.entity.ReadStatus;
import com.sakeman.entity.ReadStatus.Status;
import com.sakeman.entity.User;
import com.sakeman.entity.UserFollow;
import com.sakeman.entity.WebMangaFollow;
import com.sakeman.form.EditUserProfForm;
import com.sakeman.form.RatingForm;
import com.sakeman.service.BadgeUserService;
import com.sakeman.service.ImageValidationService;
import com.sakeman.service.MangaService;
import com.sakeman.service.ReadStatusService;
import com.sakeman.service.ReviewService;
import com.sakeman.service.S3Service;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserFollowService;
import com.sakeman.service.UserService;
import com.sakeman.service.WebMangaFollowService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    private final MangaService maService;
    private final ReadStatusService rsService;
    private final UserFollowService ufService;
    private final ReviewService revService;
    private final BadgeUserService buService;
    private final S3Service s3Service;
    private final ImageValidationService ivService;
    private final WebMangaFollowService wfService;

    /** 一覧表示 */
    @GetMapping({"/list", "/list/{tab}"})
    public String getListNew(@AuthenticationPrincipal UserDetail userDetail,
                             @ModelAttribute Manga manga,
                             @PathVariable(name="tab", required=false) String tab,
                             @RequestParam(name="page", required=false, defaultValue = "1") int page,
                             Model model) {

        if (tab==null) tab = "veteran";
        if (page < 1) {
            return String.format("redirect:/user/list/%s", tab);
        }

        Pageable pageable = null;
        if (tab.equals("veteran")) {
            pageable = PageRequest.of(page-1, 20, Sort.by(Sort.Direction.ASC, "id"));
        } else if (tab.equals("rookie")) {
            pageable = PageRequest.of(page-1, 20, Sort.by(Sort.Direction.DESC, "id"));
        }

        Page<User> userEnabledPage = service.getEnabledUserListPageable(true, pageable);

        if (page > Math.max(userEnabledPage.getTotalPages(), 1)) {
            return String.format("redirect:/user/list/%s", tab);
        }

        model.addAttribute("pages", userEnabledPage);
        model.addAttribute("userlist", userEnabledPage.getContent());
        model.addAttribute("followeelist", ufService.followeeIdListFollowedByUser(userDetail));
        model.addAttribute("reviewlist", revService.getReviewList());
        model.addAttribute("tab", tab);

        return "user/list";
        }


    /** 詳細表示 */
    @GetMapping("{id}/{tab}/{display-type}")
    public String getDetail(@AuthenticationPrincipal UserDetail userDetail,
                            @PathVariable("id") Integer id,
                            @PathVariable("tab") String tab,
                            @PathVariable("display-type") String displayType,
                            Model model,
                            @RequestParam(name="page", required=false, defaultValue = "1") int page
                            ) {

        if (page < 1) {
            return String.format("redirect:/user/%s/%s/%s", id, tab, displayType);
        }

//        Sort sort = Sort.by(Sort.Order.desc("updatedAt"));
//        Pageable pageable = PageRequest.of(page - 1, 99, sort);
        Pageable pageable = PageRequest.of(page - 1, 99);

        Page<Manga> followlistPage  = maService.getMangaByWebMangaFollowsUserIdSortByRating(id, pageable);
        Page<MangaForShelfDTO> followlistDTOPage = maService.convertToMangaForShelfDTO(followlistPage, id);
        Page<Manga> readlistPage = maService.getMangaByUserIdAndReadStatusSortByRating(id, Status.読んだ, pageable);
        Page<MangaForShelfDTO> readlistDTOPage = maService.convertToMangaForShelfDTO(readlistPage, id);
        Page<Manga> wantlistPage = maService.getMangaByUserIdAndReadStatusSortByRating(id, Status.気になる, pageable);
        Page<MangaForShelfDTO> wantlistDTOPage = maService.convertToMangaForShelfDTO(wantlistPage, id);

        List<MangaForShelfDTO> forCoverList = Stream.concat(
                wantlistDTOPage.getContent().stream(),
                readlistDTOPage.getContent().stream()
            )
            .collect(Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    Collections.shuffle(list);
                    return list.stream().limit(5).collect(Collectors.toList());
                }
            ));

        Page<?> result;
        switch (tab) {
            case "want": result = wantlistDTOPage; break;
            case "read": result = readlistDTOPage; break;
            case "follow": result = followlistDTOPage; break;
            default: result = wantlistDTOPage;
        }
        if (page > Math.max(result.getTotalPages(), 1)) {
            return String.format("redirect:/user/%s/%s/%s", id, tab, displayType);
        }

        model.addAttribute("tab", tab);
        model.addAttribute("display-type", displayType);

        model.addAttribute("user", service.getUser(id));
        model.addAttribute("wantpages", wantlistDTOPage);
        model.addAttribute("userwantlist", wantlistDTOPage.getContent());
        model.addAttribute("readpages", readlistDTOPage);
        model.addAttribute("userreadlist", readlistDTOPage.getContent());
        model.addAttribute("followpages", followlistDTOPage);
        model.addAttribute("userfollowlist", followlistDTOPage.getContent());

        model.addAttribute("currentUserId", (userDetail != null) ? userDetail.getUser().getId() : 0 );
        model.addAttribute("followeelist", ufService.followeeIdListFollowedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("forCoverList", forCoverList);
        model.addAttribute("badgelist", buService.getByUserId(id));

        model.addAttribute("allStatus", ReadStatus.Status.values());

        return "user/detail";
    }

    /** 一覧表示 (フォロワー・フォロイー) */
    @GetMapping("/{id}/{tab}")
    public String getFollowUaer(@AuthenticationPrincipal UserDetail userDetail,
                                @PathVariable(name="id") Integer id,
                                @PathVariable(name="tab") String tab,
                                @RequestParam(name="page", required=false, defaultValue = "1") int page,
                                Model model) {

        if (tab==null) tab = "followings";
        if (page < 1) {
            return String.format("redirect:/user/%s/%s", id, tab);
        }

        Pageable pageable = PageRequest.of(page-1, 20, Sort.by(Sort.Direction.ASC, "id"));

        Page<User> userListPage = null;
        if (tab.equals("followings")) {
            userListPage = service.getFollowingsByUserId(id, pageable);
        } else if (tab.equals("followers")) {
            userListPage = service.getFollowersByUserId(id, pageable);
        }

        if (page > Math.max(userListPage.getTotalPages(), 1)) {
            return String.format("redirect:/user/%s/%s", id, tab);
        }

        model.addAttribute("pages", userListPage);
        model.addAttribute("userlist", userListPage.getContent());
        model.addAttribute("followeelist", ufService.followeeIdListFollowedByUser(userDetail));
        model.addAttribute("reviewlist", revService.getReviewList());
        model.addAttribute("tab", tab);
        model.addAttribute("user", service.getUser(id));

        return "user/list";
        }

    /** 編集画面を表示 */
    @PreAuthorize("#id == authentication.principal.user.id")
    @GetMapping("/update/{id}")
    public String getUpdate(@PathVariable("id") Integer id, Model model) {
        User thisUser = service.getUser(id);
        EditUserProfForm form = new EditUserProfForm();
        form.setId(id);
        form.setImg(thisUser.getImg());
        form.setUsername(thisUser.getUsername());
        form.setSelfIntro(thisUser.getSelfIntro());
        model.addAttribute("editUserProfForm", form);
        return "user/update";
    }

    /** 編集処理 */
    @PreAuthorize("#id == authentication.principal.user.id")
    @PostMapping("/update/{id}")
    @Transactional
//    public String updateUser(@PathVariable Integer id, @ModelAttribute User user, Model model, @RequestParam("fileContents") MultipartFile fileContents) {
    public String updateUser(@PathVariable Integer id, @Validated EditUserProfForm form, BindingResult res, Model model) {
        MultipartFile file = form.getFileContents();
        if (!file.isEmpty()) {
            ivService.isValidFormat(file, res);
            ivService.isValidSize(file, res);
        }

        if (res.hasErrors()) {
            model.addAttribute("editUserProfForm", form);
            return "user/update";
        } else {
            User thisuser = service.getUser(id);
            thisuser.setUsername(form.getUsername());
            thisuser.setSelfIntro(form.getSelfIntro());

            /** 画像アップロード */
            /** ファイル名生成 */
            if (!file.isEmpty()) {

                String originalName = file.getOriginalFilename();
                String ext = originalName.substring(originalName.lastIndexOf("."));
                String newFileName = "uid" + id + "/userprofimg_" + System.currentTimeMillis() + ext;

                s3Service.upload(file, newFileName);
                thisuser.setImg(newFileName);
            }

            service.saveUser(thisuser);

            UserDetail updatedUserDetail = new UserDetail(thisuser);
            Authentication authentication = new UsernamePasswordAuthenticationToken(updatedUserDetail, null, updatedUserDetail.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return "redirect:/user/{id}/want/grid";
        }
    }

}