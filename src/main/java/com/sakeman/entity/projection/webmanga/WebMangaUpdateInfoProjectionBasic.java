package com.sakeman.entity.projection.webmanga;

import java.time.LocalDateTime;
import java.util.List;

import com.sakeman.entity.projection.manga.MangaProjectionForWebMangaUpdateInfo;
import com.sakeman.entity.projection.weblike.WebLikeProjectionForWebMangaUpdateInfo;
import com.sakeman.entity.projection.webmangamedia.WebMangaMediaProjectionForWebMangaUpdateInfo;

public interface WebMangaUpdateInfoProjectionBasic {
    Integer getId();
    String getString();
    String getSubTitle();
    String getAuthorString();
    String getUrl();
    String getImgUrl();
    Integer getFreeFlag();
    LocalDateTime getUpdateAt();

    MangaProjectionForWebMangaUpdateInfo getManga();
    WebMangaMediaProjectionForWebMangaUpdateInfo getWebMangaMedia();
    List<WebLikeProjectionForWebMangaUpdateInfo> getWebLikes();
}
