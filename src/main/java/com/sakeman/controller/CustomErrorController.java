package com.sakeman.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                // 404 Not Found エラー
                model.addAttribute("errorCode", "404");
                model.addAttribute("errorMessage", "The requested page was not found.");
                return "error/404";
            }
            else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                // 403 Forbidden
                model.addAttribute("errorCode", "403");
                model.addAttribute("errorMessage", "You do not have permission to access this page.");
                return "error/403";
            }
            // その他のエラーコードに対する処理をここに追加
        }
        // デフォルトエラーページ
        return "error/error";
    }

}

