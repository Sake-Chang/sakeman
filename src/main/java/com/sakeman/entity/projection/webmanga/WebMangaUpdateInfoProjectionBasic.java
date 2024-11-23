package com.sakeman.entity.projection.webmanga;

import java.time.LocalDateTime;
import java.util.List;

public interface WebMangaUpdateInfoProjectionBasic {
    Integer getId();
    String getTitleString();
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
