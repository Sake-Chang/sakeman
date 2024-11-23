package com.sakeman.entity.projection.webmanga;

import java.util.List;

public interface MangaProjectionForWebMangaUpdateInfo {
    Integer getId();
    String getTitle();
    String getSynopsis();
    Boolean getIsOneShot();

    List<ReviewProjectionForWebMangaUpdateInfo> getReviews();
    List<MangaTagProjectionForWebMangaUpdateInfo> getMangaTags();
    List<WebMangaFollowProjectionForWebMangaUpdateInfo> getWebMangaFollows();

}
