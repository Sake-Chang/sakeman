package com.sakeman.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Manga;
import com.sakeman.entity.WebMangaMedia;
import com.sakeman.entity.WebMangaTitleConverter;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.repository.WebMangaUpdateInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebMangaUpdateInfoSaveService {

    private final WebMangaUpdateInfoRepository repository;
    private final WebMangaUpdateInfoService webService;
    private final MangaService mangaService;
    private final WebMangaMediaService webMangaMediaService;
    private final WebMangaTitleConverterService webMangaTitleConverterService;
    private final StringUtilService utilService;


    /** セーブする */
    @Transactional
    public void saveRss(Map<String, Object> parseContents) {
        Manga spank = mangaService.getManga(111111);

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
                info.setManga(getManga(titleString, authorString, spank));
                info.setSubTitle(subTitle);
                info.setAuthorString(authorString);
                info.setUrl(url);
                info.setImgUrl(imgUrl);
                info.setUpdateAt(updateAt);
                info.setFreeFlag(freeFlag);
            webService.saveInfo(info);
        } else if (!(webService.findByUrl(url).get().getFreeFlag().equals(freeFlag))) {
                WebMangaUpdateInfo info = webService.findByUrl(url).get();
                LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Tokyo")).with(LocalTime.of(0, 0));
                info.setFreeFlag(freeFlag);
                info.setUpdateAt(now);
                webService.saveInfo(info);
        }
    }


    /** 一気にセーブする */
    @Transactional(readOnly=false)
    public void saveAllRss(List<Map> contentsList) {
        Manga spank = mangaService.getManga(111111);
        List<WebMangaUpdateInfo> infoList = new ArrayList<>();
        for (Map<String, Object> parseContents : contentsList) {
            String mediaName = (String)parseContents.get("mediaName");
            String titleString = (String)parseContents.get("titleString");
            String subTitle = (String)parseContents.get("subTitle");
            String authorString = (String)parseContents.get("authorString");
            String url = (String)parseContents.get("url");
            String imgUrl = (String)parseContents.get("imgUrl");
            LocalDateTime updateAt = (LocalDateTime)parseContents.get("updateAt");
            Integer freeFlag = (Integer)parseContents.get("freeFlag");

            System.out.println(url);

            Optional<WebMangaUpdateInfo> test = webService.findByUrl(url);
            if (test.isEmpty()) {
                WebMangaUpdateInfo info = new WebMangaUpdateInfo();
                info.setWebMangaMedia(getWebMangaMedia(mediaName));
                info.setMediaName(mediaName);
                info.setTitleString(titleString);
                info.setManga(getManga(titleString, authorString, spank));
                info.setSubTitle(subTitle);
                info.setAuthorString(authorString);
                info.setUrl(url);
                info.setImgUrl(imgUrl);
                info.setUpdateAt(updateAt);
                info.setFreeFlag(freeFlag);
                infoList.add(info);
            } else if (!(test.get().getFreeFlag().equals(freeFlag))) {
                WebMangaUpdateInfo info = test.get();
                LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Tokyo")).with(LocalTime.of(0, 0));
                info.setFreeFlag(freeFlag);
                info.setUpdateAt(now);
                infoList.add(info);
            }
        }
        repository.saveAll(infoList);
    }


    // Comic熱帯用（urlが作品ごとに同じ）
    @Transactional
    public void saveRssNettai(Map<String, Object> parseContents) {
        Manga spank = mangaService.getManga(111111);
        String mediaName = (String)parseContents.get("mediaName");
        String titleString = (String)parseContents.get("titleString");
        String subTitle = (String)parseContents.get("subTitle");
        String authorString = (String)parseContents.get("authorString");
        String url = (String)parseContents.get("url");
        String imgUrl = (String)parseContents.get("imgUrl");
        LocalDateTime updateAt = (LocalDateTime)parseContents.get("updateAt");
        Integer freeFlag = (Integer)parseContents.get("freeFlag");

        if (webService.findByTitleStringAndSubTitle(titleString, subTitle, false).isEmpty()) {
            WebMangaUpdateInfo info = new WebMangaUpdateInfo();
                info.setWebMangaMedia(getWebMangaMedia(mediaName));
                info.setMediaName(mediaName);
                info.setTitleString(titleString);
                info.setManga(getManga(titleString, authorString, spank));
                info.setSubTitle(subTitle);
                info.setAuthorString(authorString);
                info.setUrl(url);
                info.setImgUrl(imgUrl);
                info.setUpdateAt(updateAt);
                info.setFreeFlag(freeFlag);
            webService.saveInfo(info);
        }
    }

    /** メディア名(web_manga_media)の紐付け */
    @Transactional
    private WebMangaMedia getWebMangaMedia(String mediaName) {
        if (webMangaMediaService.getWebMangaMediaByName(mediaName).isPresent()) {
            return webMangaMediaService.getWebMangaMediaByName(mediaName).get();
        } else {
            return webMangaMediaService.getWebMangaMedia(1);
        }
    }

    /** マンガ名(manga)の紐付け */
    @Transactional
    private Manga getManga(String titleString, String authorString, Manga spank) {
        Optional<WebMangaTitleConverter> converterlist
            = webMangaTitleConverterService.findByTitleStrignAndAuthorString(titleString, authorString);

        String cleanseTitle = utilService.cleanse(titleString);
        List<Manga> manga = mangaService.getMangaByTitleCleanse(cleanseTitle);
        if (converterlist.isPresent()) {
            return converterlist.get().getManga();                      //コンバータ登録があればそのManga
        } else if (manga.size() == 1) {
            return manga.get(0);     //titleStringで検索して紐付け
        } else {
            return spank;                            //なければ一旦id=1で紐付け
        }
    }

}
