package com.sakeman.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final UserDetailService userDetailService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String errorMessage;

        if (exception instanceof BadCredentialsException) {
            errorMessage = "badcredentialsex";
        } else if (exception.getCause() instanceof DisabledException) {
            errorMessage = "disabledex";
        } else {
            errorMessage = "unknownerror";
        }

        // デバッグ用ログ
        System.out.println("Exception class: " + exception.getClass().getName());
        System.out.println("Exception message: " + exception.getMessage());

        response.sendRedirect("/login?error=true&message=" + errorMessage);
    }

}
