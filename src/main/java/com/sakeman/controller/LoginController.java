package com.sakeman.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String getLogin(Model model) {
        model.addAttribute("title", "ログイン｜サケマン");
        model.addAttribute("lib", "login::lib");
        model.addAttribute("main", "login::main");
        return "crud-userprof/login";
    }
}