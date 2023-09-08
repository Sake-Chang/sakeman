package com.sakeman.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import com.sakeman.entity.Manga;
import com.sakeman.entity.ReadStatus;
import com.sakeman.entity.User;
import com.sakeman.service.ReadStatusService;
import com.sakeman.service.ReviewService;
import com.sakeman.service.S3Service;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserFollowService;
import com.sakeman.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    private final ReadStatusService rsService;
    private final UserFollowService ufService;
    private final ReviewService revService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private S3Service s3Service;

    /** 一覧表示（新着順） */
    @GetMapping("")
    public String getListNew(@AuthenticationPrincipal UserDetail userDetail, @ModelAttribute Manga manga, Model model, @PageableDefault(page=0, size=20, sort= {"registeredAt"}, direction=Direction.ASC) Pageable pageable) {
        model.addAttribute("pages", service.getUserListPageable(pageable));
        model.addAttribute("userlist", service.getUserListPageable(pageable).getContent());
        model.addAttribute("followeelist", ufService.followeeIdListFollowedByUser(userDetail));
        model.addAttribute("reviewlist", revService.getReviewList());
        return "user/list";
        }

    /** 一覧表示（人気順） */
    @GetMapping("/popular")
    public String getListPop(@AuthenticationPrincipal UserDetail userDetail, @ModelAttribute Manga manga, Model model, @PageableDefault(page=0, size=20, sort= {"registeredAt"}, direction=Direction.ASC) Pageable pageable) {
        model.addAttribute("pages", service.getUserListPageable(pageable));
        model.addAttribute("userlist", service.getUserListPageable(pageable).getContent());
        model.addAttribute("followeelist", ufService.followeeIdListFollowedByUser(userDetail));
        model.addAttribute("reviewlist", revService.getReviewList());
        return "user/list-popular";
        }

    /** 詳細表示 */
    @GetMapping("{id}")
    public String getDetail(@AuthenticationPrincipal UserDetail userDetail, @PathVariable("id") Integer id, Model model, @PageableDefault(page=0, size=20, sort= {"registeredAt"}, direction=Direction.DESC) Pageable pageable) {
        model.addAttribute("user", service.getUser(id));
        model.addAttribute("usermangalist", rsService.findByUserAndStatus(service.getUser(id), ReadStatus.Status.気になる));
        model.addAttribute("followeelist", ufService.followeeIdListFollowedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        return "user/detail";
        }

    /** 詳細表示（読んだ） */
    @GetMapping("{id}/read")
    public String getDetailRead(@AuthenticationPrincipal UserDetail userDetail, @PathVariable("id") Integer id, Model model) {
        model.addAttribute("user", service.getUser(id));
        model.addAttribute("usermangalist", rsService.findByUserAndStatus(service.getUser(id), ReadStatus.Status.読んだ));
        model.addAttribute("followeelist", ufService.followeeIdListFollowedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        return "user/detail-read";
        }

    /** 新規登録（画面表示） */
    @GetMapping("register")
    public String getRegister(@ModelAttribute User user, Model model) {
        return "user/register";
    }

    /** 登録処理 */
    @PostMapping("/register")
    public String postRegister(@Validated User user, BindingResult res, Model model) {
        if(res.hasErrors()) {
            return getRegister(user, model);
        }
        user.setDeleteFlag(0);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        service.saveUser(user);
        return "redirect:user/list";
    }

    /** 編集画面を表示 */
    @GetMapping("update/{id}")
    public String getUpdate(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("user", service.getUser(id));
        return "user/update";
    }

    /** 編集処理 */
    @PostMapping("update/{id}")
    @Transactional
    public String updateUser(@PathVariable Integer id, @ModelAttribute User user, Model model, @RequestParam("fileContents") MultipartFile fileContents) {
        User thisuser = service.getUser(id);
        thisuser.setUsername(user.getUsername());
        thisuser.setSelfIntro(user.getSelfIntro());
        thisuser.setImg(fileContents.getOriginalFilename());
        service.saveUser(thisuser);

        s3Service.upload(fileContents);

//        byte[] contents;
//        try (BufferedOutputStream bos = new BufferedOutputStream (new FileOutputStream (Utils.makeAttachedFilePath("@{/img}", )) ) ) {
//            contents = fileContents.getBytes();
//            bos.write(contents);
//        }


        return "redirect:/user/{id}";
    }

    /** 削除処理 */
    @GetMapping("delete/{id}/")
    public String deleteUser(@PathVariable("id") Integer id, @ModelAttribute User user, Model model) {
        User usr = service.getUser(id);
        usr.setDeleteFlag(1);
        service.saveUser(usr);
        return "redirect:/user/list";
    }
}