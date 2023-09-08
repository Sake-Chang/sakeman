package com.sakeman.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sakeman.entity.Manga;
import com.sakeman.entity.WebMangaMedia;
import com.sakeman.entity.WebMangaTitleConverter;
import com.sakeman.entity.WebMangaUpdateInfo;

@Service
public class WebMangaUpdateInfoSaveService {

    private final WebMangaUpdateInfoService webService;
    private final MangaService mangaService;
    private final WebMangaMediaService webMangaMediaService;
    private final WebMangaTitleConverterService webMangaTitleConverterService;

    public WebMangaUpdateInfoSaveService ( WebMangaUpdateInfoService webService,
                            MangaService mangaService,
                            WebMangaMediaService webMangaMediaService,
                            WebMangaTitleConverterService webMangaTitleConverterService
                          ) {
        this.webService = webService;
        this.mangaService = mangaService;
        this.webMangaMediaService = webMangaMediaService;
        this.webMangaTitleConverterService = webMangaTitleConverterService;
    }

    /** セーブする */
    public void saveRss(Map<String, Object> parseContents) {

        String mediaName = (String)parseContents.get("mediaName");
        String titleString = (String)parseContents.get("titleString");
        String subTitle = (String)parseContents.get("subTitle");
        String authorString = (String)parseContents.get("authorString");
        String url = (String)parseContents.get("url");
        String imgUrl = (String)parseContents.get("imgUrl");
        LocalDateTime updateAt = (LocalDateTime)parseContents.get("updateAt");
        Integer freeFlag = (Integer)parseContents.get("freeFlag");

        if (webService.findByUrl(url).isEmpty()) {
            WebMangaUpdateInfo info = new WebMangaUpdateInfo();
                info.setWebMangaMedia(getWebMangaMedia(mediaName));
                info.setMediaName(mediaName);
                info.setTitleString(titleString);
                info.setManga(getManga(titleString, authorString));
                info.setSubTitle(subTitle);
                info.setAuthorString(authorString);
                info.setUrl(url);
                info.setImgUrl(imgUrl);
                info.setUpdateAt(updateAt);
                info.setFreeFlag(freeFlag);
            webService.saveInfo(info);
        } else if (!(webService.findByUrl(url).get().getFreeFlag().equals(freeFlag))) {
                WebMangaUpdateInfo info = webService.findByUrl(url).get();
                LocalDateTime now = LocalDateTime.now().with(LocalTime.of(0, 0));
                info.setFreeFlag(freeFlag);
                info.setUpdateAt(now);
                webService.saveInfo(info);
        }
    }

    /** メディア名(web_manga_media)の紐付け */
    private WebMangaMedia getWebMangaMedia(String mediaName) {
        if (webMangaMediaService.getWebMangaMediaByName(mediaName).isPresent()) {
            return webMangaMediaService.getWebMangaMediaByName(mediaName).get();
        } else {
            return webMangaMediaService.getWebMangaMedia(1);
        }
    }

    /** マンガ名(manga)の紐付け */
    private Manga getManga(String titleString, String authorString) {
        Optional<WebMangaTitleConverter> converterlist
            = webMangaTitleConverterService.findByTitleStrignAndAuthorString(titleString, authorString);
        if (converterlist.isPresent()) {
            return converterlist.get().getManga();                      //コンバータ登録があればそのManga
        } else if (mangaService.getMangaByTitle(titleString).isPresent()) {
            return mangaService.getMangaByTitle(titleString).get();     //titleStringで検索して紐付け
        } else {
            return mangaService.getManga(1);                            //なければ一旦id=1で紐付け
        }
    }

}
