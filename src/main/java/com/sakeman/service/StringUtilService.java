package com.sakeman.service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sakeman.entity.Manga;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StringUtilService {
    private final MangaService maService;

    private final List<String> DELETE_CHARS = new ArrayList<>() {
        {
            add("~"); add("-"); add("〜"); add("―");
        }
    };

    public boolean isSame(String str1, String str2) {
        Boolean res;
        if (cleanse(str1) == cleanse(str2)) {
            res = true;
        }
        else {
            res = false;
        }
        return res;
    }

    public String cleanse(String str) {
        // Normalize
        String strNorm = Normalizer.normalize(str, Normalizer.Form.NFKC);
        StringBuilder sbNorm = new StringBuilder(strNorm);
        while (sbNorm.indexOf(" ") != -1) {
            sbNorm = sbNorm.deleteCharAt(sbNorm.indexOf(" "));
        }
        // 大文字を小文字に変換
        for (int i=0; i < sbNorm.length(); i++) {
            char c = sbNorm.charAt(i);
            char uc = Character.toLowerCase(c);
            sbNorm.setCharAt(i, uc);
        }

        // 変換されないものを変換
        for (String deleteChar : DELETE_CHARS) {
            while (sbNorm.indexOf(deleteChar) != -1) {
                sbNorm = sbNorm.deleteCharAt(sbNorm.indexOf(deleteChar));
            }
        }
        return sbNorm.toString();
    }

    public void titleCleanse(int start, int end) {
        List<Manga> mangalist = maService.getByIdBetween(start, end);
        int num = mangalist.size();
            List<Manga> mangas = new ArrayList<>();
            for (int j=0; j<num; j++) {
                Manga manga = mangalist.get(j);
                String titleCleanse = cleanse(manga.getTitle());
                manga.setTitleCleanse(titleCleanse);
                mangas.add(manga);
                System.out.println("manga_id = " + manga.getId() + " SET");
            }
            maService.saveAllManga(mangas);
    }

}
