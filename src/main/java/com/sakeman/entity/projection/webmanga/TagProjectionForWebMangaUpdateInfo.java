package com.sakeman.entity.projection.webmanga;

import java.util.List;

public interface TagProjectionForWebMangaUpdateInfo {
    String getTagname();
    List<GenreTagProjectionForWebMangaUpdateInfo> getGenreTags();

}
