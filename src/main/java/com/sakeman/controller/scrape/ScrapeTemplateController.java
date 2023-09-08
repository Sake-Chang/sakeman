package com.sakeman.controller.scrape;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sakeman.service.ScrapeConvertService;
import com.sakeman.service.WebMangaUpdateInfoSaveService;

@Controller
@RequestMapping("scrape")
public class ScrapeTemplateController {

    private final WebMangaUpdateInfoSaveService saveService;
    private final ScrapeConvertService convertService;
    public ScrapeTemplateController (WebMangaUpdateInfoSaveService saveService, ScrapeConvertService convertService) {
        this.saveService = saveService;
        this.convertService = convertService;
    }

    /** 設定 */
    String flashComment = "ComicHowlの更新情報が登録されました！";
    String rootUrl = "https://ichijin-plus.com/howl";
    String additionalUrl = "https://ichijin-plus.com";
    String listsXpath = "//div[@class='sc-d3c447da-0 gMSqaP']/a";

    String mediaName = "comic HOWL";
    Integer freeFlag = 1;

    /** Xpath */
    String titleStringXpath = "//h1";
    String authorStringXpath = "//div[@class='sc-155acf28-3 gEToCC']";
    String urlXpath = "//a[@class='sc-ecb78c8b-0 lnqYpo']";
    String imgUrlXpath = "//div[@class='sc-ecb78c8b-2 llKFlx']/div[@class='sc-952d0b48-0 esfhwh']";
    String subTitleXpath = "//span[@class='sc-ecb78c8b-6 dMXNOG']";
    String updateXpath = "//div[contains(@class,'sc-ecb78c8b-1')]";


    /** メソッド */
    @GetMapping("/comic-howl2")
    public String scrape(RedirectAttributes attrs) throws IOException {
        Document doc = Jsoup.connect(rootUrl).get();
        Elements lists = doc.selectXpath(listsXpath);
        for (Element list: lists) {
            String childUrl = list.attr("href");
            //System.out.println(childUrl);
            scrapeChild(childUrl);
        }
        attrs.addFlashAttribute("success", flashComment);
        return "redirect:/admin/index";
    }

    public void scrapeChild(String childUrl) throws IOException {
        String linkUrl = additionalUrl + childUrl;
        Document doc = Jsoup.connect(linkUrl).get();

        String titleString = convertService.getText(doc, titleStringXpath);
        String authorString = convertService.getText(doc, authorStringXpath);
        String url = convertService.getUrl(doc, urlXpath, additionalUrl);
        String imgUrl = convertService.getImgUrl(doc, imgUrlXpath);
        String subTitle = convertService.getText(doc, subTitleXpath);
        String updateRaw = convertService.getText(doc, updateXpath);
        LocalDateTime update = convertService.getUpdate(updateRaw);

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
            parseContents.put("freeFlag", freeFlag);
        saveService.saveRss(parseContents);
    }
}
