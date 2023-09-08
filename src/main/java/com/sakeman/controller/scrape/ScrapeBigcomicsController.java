package com.sakeman.controller.scrape;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sakeman.service.ScrapeConvertService;
import com.sakeman.service.WebMangaUpdateInfoSaveService;

@Controller
@RequestMapping("scrape")
public class ScrapeBigcomicsController {

    private final WebMangaUpdateInfoSaveService saveService;
    private final ScrapeConvertService convertService;
    public ScrapeBigcomicsController (WebMangaUpdateInfoSaveService saveService, ScrapeConvertService convertService) {
        this.saveService = saveService;
        this.convertService = convertService;
    }

    /** 設定 */
    String mediaName = "ビッコミ";
    String flashComment = "ビッコミの更新情報が登録されました！";
    List<String> rootUrls = new ArrayList<>() {
        {
            add("https://bigcomics.jp/category/manga?type=%E9%80%A3%E8%BC%89%E4%B8%AD&day=%E6%9C%88");
            add("https://bigcomics.jp/category/manga?type=%E9%80%A3%E8%BC%89%E4%B8%AD&day=%E7%81%AB");
            add("https://bigcomics.jp/category/manga?type=%E9%80%A3%E8%BC%89%E4%B8%AD&day=%E6%B0%B4");
            add("https://bigcomics.jp/category/manga?type=%E9%80%A3%E8%BC%89%E4%B8%AD&day=%E6%9C%A8");
            add("https://bigcomics.jp/category/manga?type=%E9%80%A3%E8%BC%89%E4%B8%AD&day=%E9%87%91");
            add("https://bigcomics.jp/category/manga?type=%E9%80%A3%E8%BC%89%E4%B8%AD&day=%E5%9C%9F");
            add("https://bigcomics.jp/category/manga?type=%E9%80%A3%E8%BC%89%E4%B8%AD&day=%E6%97%A5");
            add("https://bigcomics.jp/category/manga?type=%E8%AA%AD%E3%81%BF%E5%88%87%E3%82%8A");
        }
    };

    String additionalUrl = "";
    String listsXpath = "//div[@class='category-list grid-category-template']/a";


    /** Xpath */
    String titleStringXpath = "//h1/span[not(contains(@class, 'g-hidden'))]";
    String authorStringXpath = "//div[@class='series-h-credit-user']";
    String urlXpath = "//a";
    String imgUrlXpath = "//source[contains(@media, '(max-width: 767px)')]";
    String subTitleXpath = "//span[@class='series-ep-list-item-h-text']";
    String updateXpath = "//time";
    String freeFlagXpath1 = "/descendant::div[@class='series-ep-list-symbols']//div[@class='free-icon-new-container']";
    String freeFlagXpath2 = "/descendant::div[@class='series-ep-list-symbols']//img";


    /** メソッド */
    @PostMapping("/bigcomics/{dow}")
    public String scrape(RedirectAttributes attrs, @PathVariable("dow") Integer dow) throws IOException {
        String rootUrl = rootUrls.get(dow);
        Document doc = Jsoup.connect(rootUrl).get();
        Elements lists = doc.selectXpath(listsXpath);
        for (Element list: lists) {
            String childUrl = list.attr("href");
            if (childUrl.equals("")) {
                continue;
            }
            scrapeChild(childUrl);
        }
        attrs.addFlashAttribute("success", flashComment);
        return "redirect:/admin/index";
    }

    public void scrapeChild(String childUrl) throws IOException {
        String linkUrl = childUrl + "?s=1" ;
        Document doc = Jsoup.connect(linkUrl).get();

        String titleString = convertService.getText(doc, titleStringXpath);
        String authorString = convertService.getText(doc, authorStringXpath);

        Elements lists = doc.selectXpath("//div[@class='series-ep-list']/div");
        for (int i = 0; i < lists.size(); i++) {
            String html = lists.get(i).html();
            Document document = Jsoup.parse(html);

            String url = document.selectXpath(urlXpath).attr("data-href");
            String imgUrl = getImgUrl(document, imgUrlXpath);
            String subTitle = document.selectXpath(subTitleXpath).text();
            LocalDateTime update = getUpdate(document, updateXpath);
            Integer freeFlag = getFreeFlag(document, freeFlagXpath1, freeFlagXpath2);

//            System.out.println("===============================");
//            System.out.println(titleString);
//            System.out.println(authorString);
//            System.out.println(url);
//            System.out.println(imgUrl);
//            System.out.println(subTitle);
//            System.out.println(update);
//            System.out.println(freeFlag);

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


    public String getImgUrl(Document document, String xpath) {
        String imgUrlRaw = document.selectXpath(xpath).attr("data-srcset");
        String imgUrlPart = imgUrlRaw.substring(0, imgUrlRaw.indexOf(" 1x"));
        String imgUrl = "https:" + imgUrlPart;
        return imgUrl;
    }

    public LocalDateTime getUpdate(Document document, String xpath) {
        String updateRaw = document.selectXpath(xpath).attr("datetime");
        LocalDateTime update = LocalDateTime.parse(updateRaw, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return update;
    }

    public Integer getFreeFlag(Document document, String xpath1, String xpath2) {
        String freeFlagText = document.selectXpath(xpath2).attr("alt");
        if (!document.selectXpath(xpath1).isEmpty()) {
            Integer freeFlag = 1;
            return freeFlag;
        } else {
            if (freeFlagText.equals("「待つと無料」")) {
                Integer freeFlag = 2;
                return freeFlag;
            } else {
                Integer freeFlag = 0;
                return freeFlag;
            }
        }
    }
}
