package com.sakeman.thymeleaf;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.stereotype.Component;

public class PercentEncode {

    public String percentEncode(String mangaTitle) {
        String result = "";
        try {
            result = URLEncoder.encode(mangaTitle, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
