package com.sakeman.utils;

import javax.servlet.http.HttpServletRequest;

public class WebUtils {
    private WebUtils() { /** インスタンス化防止 */ }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWithHeader = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWithHeader);
    }
}
