package com.sakeman.controller.scrape;

import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sakeman.entity.WebMangaTitleConverter;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.service.MangaService;
import com.sakeman.service.WebMangaUpdateInfoSaveService;
import com.sakeman.service.WebMangaMediaService;
import com.sakeman.service.WebMangaTitleConverterService;
import com.sakeman.service.WebMangaUpdateInfoService;

@Controller
@RequestMapping("scrape")
public class ScrapeComichowlController {

    private final WebMangaUpdateInfoSaveService saveService;
    public ScrapeComichowlController (WebMangaUpdateInfoSaveService saveService) {
        this.saveService = saveService;
    }

    @PostMapping("/comic-howl")
    public String scrape(RedirectAttributes attrs) throws IOException {
        String rootUrl = "https://ichijin-plus.com/howl";
        Document doc = Jsoup.connect(rootUrl).get();

        Elements lists = doc.selectXpath("//div[@class='sc-d3c447da-0 gMSqaP']/a");
        for (Element list: lists) {
            String childUrl = list.attr("href");
            //System.out.println(childUrl);
            scrapeChild(childUrl);
        }
        attrs.addFlashAttribute("success", "ComicHowlの更新情報が登録されました！");
        return "redirect:/admin/index";
    }

    public void scrapeChild(String childUrl) throws IOException {
        String linkUrl = "https://ichijin-plus.com" + childUrl;
        Document doc = Jsoup.connect(linkUrl).get();

        String mediaName = "comic HOWL";
        String titleStringRaw = doc.selectXpath("//h1").text();
        String titleString = Normalizer.normalize(titleStringRaw, Form.NFKC);
        String authorString = doc.selectXpath("//div[@class='sc-155acf28-3 gEToCC']").text();
        String urlAb = doc.selectXpath("//a[@class='sc-ecb78c8b-0 lnqYpo']").attr("href");
        String url = "https://ichijin-plus.com" + urlAb;
        String imgUrlRaw = doc.selectXpath("//div[@class='sc-ecb78c8b-2 llKFlx']/div[@class='sc-952d0b48-0 esfhwh']").html();
        String imgUrl = imgUrlRaw.replace("<img alt=\"\" data-src=\"", "").replace("\" class=\"sc-952d0b48-1 iitnLG fade-in lazyload\">", "").trim();
        String subTitle = doc.selectXpath("//span[@class='sc-ecb78c8b-6 dMXNOG']").text();
        String updateRaw = doc.selectXpath("//div[contains(@class,'sc-ecb78c8b-1')]").text();

        LocalDateTime update;
        if (updateRaw.contains("日前")) {
            String updateReplace = updateRaw.replace("最新話", "").replace("日前", "").replace("時間前", "");
            Integer updateReplaceInteger = Integer.parseInt(updateReplace);
            LocalDateTime now = LocalDateTime.now();
            int year = now.getYear();
            int month = now.getMonthValue();
            int day = now.getDayOfMonth() - updateReplaceInteger;
            String updateMonth = (String)String.format("%02d", month);
            String updateDay = (String)String.format("%02d", day);
            String updateString = year + "/" + updateMonth + "/" +updateDay;
            update = LocalDateTime.of(LocalDate.parse(updateString, DateTimeFormatter.ofPattern("yyyy/MM/dd")), LocalTime.of(0,0));
        } else if (updateRaw.contains("時間前")) {
            String updateReplace = updateRaw.replace("最新話", "").replace("時間前", "");
            LocalDateTime now = LocalDateTime.now();
            int year = now.getYear();
            int month = now.getMonthValue();
            int day = now.getDayOfMonth();
            String updateMonth = (String)String.format("%02d", month);
            String updateDay = (String)String.format("%02d", day);
            String updateString = year + "/" + updateMonth + "/" +updateDay;
            update = LocalDateTime.of(LocalDate.parse(updateString, DateTimeFormatter.ofPattern("yyyy/MM/dd")), LocalTime.of(0,0));
        } else {
            String updateReplace = updateRaw.replace("最新話", "");
            String[] updateSplit = updateReplace.split("/");
            int month = Integer.parseInt(updateSplit[1]);
            int day = Integer.parseInt(updateSplit[2]);
            String updateMonth = (String)String.format("%02d", month);
            String updateDay = (String)String.format("%02d", day);
            String updateString = updateSplit[0] + "/" + updateMonth + "/" +updateDay;
            update = LocalDateTime.of(LocalDate.parse(updateString, DateTimeFormatter.ofPattern("yyyy/MM/dd")), LocalTime.of(0,0));
        }

        System.out.println("===============================");
        System.out.println(titleString);
        System.out.println(authorString);
        System.out.println(url);
        System.out.println(imgUrl);
        System.out.println(subTitle);
        System.out.println(update);

        Map<String, Object> parseContents = new HashMap<>();
            parseContents.put("mediaName", mediaName);
            parseContents.put("titleString", titleString);
            parseContents.put("subTitle", subTitle);
            parseContents.put("authorString", authorString);
            parseContents.put("url", url);
            parseContents.put("imgUrl", imgUrl);
            parseContents.put("updateAt", update);
            parseContents.put("freeFlag", 1);
//        saveService.saveRss(parseContents);
    }
}
