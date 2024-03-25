package com.sakeman.thymeleaf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sakeman.entity.Manga;
import com.sakeman.entity.ReadStatus;
import com.sakeman.entity.WebMangaMedia;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.repository.MangaRepository;
import com.sakeman.repository.ReadStatusRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ToSetMedia {

    public Set<WebMangaMedia> toSetMedia(Manga manga) {
        Set<WebMangaMedia> mediaSet = new HashSet<>();

        List<WebMangaUpdateInfo> infos = manga.getWebMangaUpdateInfos();

        if (infos != null) {
            for (WebMangaUpdateInfo info : infos) {
                WebMangaMedia media = info.getWebMangaMedia();
                if (media != null) {
                    mediaSet.add(media);
                }
            }
        }
        return mediaSet;
    }

}
