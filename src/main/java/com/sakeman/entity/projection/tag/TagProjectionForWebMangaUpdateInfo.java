package com.sakeman.entity.projection.tag;

import java.util.List;

import com.sakeman.entity.projection.genretag.GenreTagProjectionForWebMangaUpdateInfo;

public interface TagProjectionForWebMangaUpdateInfo {
    String getTagname();
    List<GenreTagProjectionForWebMangaUpdateInfo> getGenreTags();

}
