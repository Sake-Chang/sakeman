package com.sakeman.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class LogoutController {

    @PostMapping("/logout")
    public String postLogout(RedirectAttributes attrs) {
        attrs.addFlashAttribute("success", "ログアウトしました");
        return "redirect:/login";
    }
}