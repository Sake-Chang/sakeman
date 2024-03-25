package com.sakeman.controller.admin;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sakeman.service.StringUtilService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("admin")
@RequiredArgsConstructor
public class TitleCleanseController {

    private final StringUtilService utilService;

    @PostMapping("/title-cleanse")
    public String titleCleanse(@RequestParam int start, @RequestParam int end, RedirectAttributes attrs) {
        utilService.titleCleanse(start, end);
        attrs.addFlashAttribute("success", "タイトル処理が完了しました〜♫");
        return "redirect:/admin/index";
    }

}
