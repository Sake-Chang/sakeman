package com.sakeman.entity.projection.manga;

import java.util.List;

import com.sakeman.entity.projection.mangatag.MangaTagProjectionForWebMangaUpdateInfo;
import com.sakeman.entity.projection.review.ReviewProjectionForWebMangaUpdateInfo;
import com.sakeman.entity.projection.webmangafollow.WebMangaFollowProjectionForWebMangaUpdateInfo;

public interface MangaProjectionForWebMangaUpdateInfo {
    Integer getId();
    String getTitle();
    String getSynopsis();
    Boolean getIsOneShot();

    List<ReviewProjectionForWebMangaUpdateInfo> getReviews();
    List<MangaTagProjectionForWebMangaUpdateInfo> getMangaTags();
    List<WebMangaFollowProjectionForWebMangaUpdateInfo> getWebMangaFollows();

}
