package com.sakeman.controller.scrape;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
public class ScrapeAlphapolisController {

    private final WebMangaUpdateInfoSaveService saveService;
    private final ScrapeConvertService convertService;
    public ScrapeAlphapolisController (WebMangaUpdateInfoSaveService saveService, ScrapeConvertService convertService) {
        this.saveService = saveService;
        this.convertService = convertService;
    }

    /** 設定 */
    String flashComment = "アルファポリスの更新情報が登録されました！";
    String rootUrl = "https://www.alphapolis.co.jp/manga/official/search?category%5B0%5D=men&category%5B1%5D=women&category%5B2%5D=tl&category%5B3%5D=bl&page=1";
    String additionalUrl = "https://www.alphapolis.co.jp";
    String listsXpath = "//div[@class='mangas-list']/div/a";

    String mediaName = "アルファポリス";
    Integer freeFlag = 1;

    /** Xpath */
    String titleStringXpath = "//h1";
    String authorStringXpath = "//div[@class='author-label']";
    String urlXpath = "//div[@class='episode-unit']";
    String imgUrlXpath = "//div[@class='episode-unit'][1]//img";
    String subTitleXpath = "//div[@class='episode-unit'][1]//div[@class='title']";
    String updateXpath = "//div[@class='episode-unit'][1]//div[@class='up-time']";


    /** メソッド */
    @PostMapping("/alphapolis")
    public String scrape(RedirectAttributes attrs) throws IOException {
        Document doc = Jsoup.connect(rootUrl).get();
        Elements lists = doc.selectXpath(listsXpath);
        for (Element list: lists) {
            String childUrl = list.attr("href");
            if (childUrl == "/manga/official/571000385") {
                continue;
            }
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
        String authorString = getAuthor(doc, authorStringXpath);
        String url = getUrl(doc, urlXpath, linkUrl);
        String imgUrl = getImgUrl(doc, imgUrlXpath);
        String subTitle = convertService.getText(doc, subTitleXpath);
        LocalDateTime update = getUpdate(doc, updateXpath);
        if (update.getYear() == 1975) {
            return;
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
            parseContents.put("freeFlag", freeFlag);
        saveService.saveRss(parseContents);
    }

    public String getAuthor(Element doc, String xpath) {
        String authorRaw = doc.selectXpath(xpath).text();
        String authorReplace = authorRaw.replace(" お気に入りに追加 ", "$$$");
        String author = authorReplace.substring(0, authorReplace.indexOf("$$$"));
        return author;
    }

    public String getUrl(Element doc, String xpath, String linkUrl) {
        String data = doc.selectXpath(xpath).attr("data-order");
        String url = linkUrl + "/" + data;
        return url;
    }

    public String getImgUrl(Element doc, String xpath) {
        String imgUrl = doc.selectXpath(xpath).attr("data-src");
        return imgUrl;
    }

    public LocalDateTime getUpdate(Element doc, String updateXpath) {
        String updateRaw = doc.selectXpath(updateXpath).text();
        String updateReplace = updateRaw.replace("更新", "").replace(".", "/");
        if (updateReplace.isEmpty()) {
            LocalDateTime update = LocalDateTime.of(1975, 1, 1, 1, 1);
            return update;
        } else {

//        String updateYear = updateSplit[0];
//        String updateMonth = (String)String.format("%02d", updateSplit[1]);
//        String updateDay = (String)String.format("%02d", updateSplit[2]);
//        String updateString = updateYear + "/" + updateMonth + "/" +updateDay;
            LocalDateTime update = LocalDateTime.of(LocalDate.parse(updateReplace, DateTimeFormatter.ofPattern("yyyy/MM/dd")), LocalTime.of(12,0));
            return update;
        }
    }
}
