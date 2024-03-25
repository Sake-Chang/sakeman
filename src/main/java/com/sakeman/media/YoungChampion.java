package com.sakeman.media;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class YoungChampion {
    private final String mediaName = "ヤンチャンWeb";
    private final String flashComment = "ヤンチャンWebの更新情報が登録されました！";
    private final List<String> rootUrls = new ArrayList<>() {
        {
            add("https://youngchampion.jp/category/manga?type=%E9%80%A3%E8%BC%89%E4%B8%AD&day=%E6%9C%88");
            add("https://youngchampion.jp/category/manga?type=%E9%80%A3%E8%BC%89%E4%B8%AD&day=%E7%81%AB");
            add("https://youngchampion.jp/category/manga?type=%E9%80%A3%E8%BC%89%E4%B8%AD&day=%E6%B0%B4");
            add("https://youngchampion.jp/category/manga?type=%E9%80%A3%E8%BC%89%E4%B8%AD&day=%E6%9C%A8");
            add("https://youngchampion.jp/category/manga?type=%E9%80%A3%E8%BC%89%E4%B8%AD&day=%E9%87%91");
            add("https://youngchampion.jp/category/manga?type=%E9%80%A3%E8%BC%89%E4%B8%AD&day=%E5%9C%9F");
            add("https://youngchampion.jp/category/manga?type=%E9%80%A3%E8%BC%89%E4%B8%AD&day=%E6%97%A5");
            add("https://youngchampion.jp/category/manga?type=%E8%AA%AD%E3%81%BF%E5%88%87%E3%82%8A");
        }
    };

    private final String listsXpath = "//div[@class='category-list grid-category-template']/a";
    private String epListXpath = "//div[@class='series-ep-list']/div";
    /** Xpath */
    private final String titleStringXpath = "//h1/span[not(contains(@class, 'g-hidden'))]";
    private final String authorStringXpath = "//div[@class='series-h-credit-user']";
    private final String urlXpath = "//a";
    private final String imgUrlXpath = "//source[contains(@media, '(max-width: 767px)')]";
    private final String subTitleXpath = "//span[@class='series-ep-list-item-h-text']";
    private final String updateXpath = "//time";
    private final String freeFlagXpath1 = "/descendant::div[@class='series-ep-list-symbols']//div[@class='free-icon-new-container']";
    private final String freeFlagXpath2 = "/descendant::div[@class='series-ep-list-symbols']//img";

    public String getMediaName() {
        return mediaName;
    }
    public String getFlashComment() {
        return flashComment;
    }
    public List<String> getRootUrls() {
        return rootUrls;
    }
    public String getListsXpath() {
        return listsXpath;
    }
    public String getTitleStringXpath() {
        return titleStringXpath;
    }
    public String getAuthorStringXpath() {
        return authorStringXpath;
    }
    public String getUrlXpath() {
        return urlXpath;
    }
    public String getImgUrlXpath() {
        return imgUrlXpath;
    }
    public String getSubTitleXpath() {
        return subTitleXpath;
    }
    public String getUpdateXpath() {
        return updateXpath;
    }
    public String getFreeFlagXpath1() {
        return freeFlagXpath1;
    }
    public String getFreeFlagXpath2() {
        return freeFlagXpath2;
    }
    public String getEpListXpath() {
        return epListXpath;
    }
    public void setEpListXpath(String epListXpath) {
        this.epListXpath = epListXpath;
    }


}
