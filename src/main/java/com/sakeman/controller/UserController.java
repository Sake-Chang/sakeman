package com.sakeman.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sakeman.entity.Manga;
import com.sakeman.entity.ReadStatus;
import com.sakeman.entity.ReadStatus.Status;
import com.sakeman.entity.User;
import com.sakeman.service.BadgeUserService;
import com.sakeman.service.ReadStatusService;
import com.sakeman.service.ReviewService;
import com.sakeman.service.S3Service;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserFollowService;
import com.sakeman.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    private final ReadStatusService rsService;
    private final UserFollowService ufService;
    private final ReviewService revService;
    private final BadgeUserService buService;
    private final S3Service s3Service;

    /** 一覧表示 */
    @GetMapping({"/list", "/list/{tab}"})
    public String getListNew(@AuthenticationPrincipal UserDetail userDetail,
                             @ModelAttribute Manga manga,
                             @PathVariable(name="tab", required=false) String tab,
                             Integer page,
                             Model model) {

        if (tab==null) tab = "veteran";
        if (page==null) page = 0;

        Pageable pageable = null;
        if (tab.equals("veteran")) {
            pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.ASC, "id"));
        } else if (tab.equals("rookie")) {
            pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "id"));
        }

        Page<User> userEnabledPage = service.getEnabledUserListPageable(true, pageable);
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
                            @PageableDefault(page=0, size=99, sort= {"registeredAt"},
                            direction=Direction.DESC) Pageable pageable) {

        Page<ReadStatus> userwantlistPage = rsService.findByUserAndStatusPageable(service.getUser(id), Status.気になる, pageable);
        Page<ReadStatus> userreadlistPage = rsService.findByUserAndStatusPageable(service.getUser(id), Status.読んだ, pageable);
        List<ReadStatus> statuslist = new ArrayList<>();
        statuslist.addAll(userwantlistPage.getContent());
        statuslist.addAll(userreadlistPage.getContent());
        Collections.shuffle(statuslist);

        model.addAttribute("tab", tab);
        model.addAttribute("display-type", displayType);

        model.addAttribute("user", service.getUser(id));
        model.addAttribute("wantpages", userwantlistPage);
        model.addAttribute("userwantlist", userwantlistPage.getContent());
        model.addAttribute("readpages", userreadlistPage);
        model.addAttribute("userreadlist", userreadlistPage.getContent());

        model.addAttribute("followeelist", ufService.followeeIdListFollowedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("statuslist", statuslist);
        model.addAttribute("badgelist", buService.getByUserId(id));
        return "user/detail";
    }

    /** 編集画面を表示 */
    @GetMapping("update/{id}")
    public String getUpdate(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("user", service.getUser(id));
        return "user/update";
    }

    /** 編集処理 */
    @PostMapping("/update/{id}")
    //@Transactional
    public String updateUser(@PathVariable Integer id, @ModelAttribute User user, Model model, @RequestParam("fileContents") MultipartFile fileContents) {
        User thisuser = service.getUser(id);
        thisuser.setUsername(user.getUsername());
        thisuser.setSelfIntro(user.getSelfIntro());

        /** 画像アップロード */
        /** ファイル名生成 */
        String originalName = fileContents.getOriginalFilename();
        String ext = originalName.substring(originalName.lastIndexOf("."));
        String newFileName = "uid" + id + "/userprofimg_" + id + ext;

        if (!fileContents.isEmpty()) {
            s3Service.upload(fileContents, newFileName);
            thisuser.setImg(newFileName);
        }

        service.saveUser(thisuser);

        return "redirect:/user/{id}/want/grid";
    }

}