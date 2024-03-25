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
public class ScrapeComicnettaiController {

    private final WebMangaUpdateInfoSaveService saveService;
    private final ScrapeConvertService convertService;
    public ScrapeComicnettaiController (WebMangaUpdateInfoSaveService saveService, ScrapeConvertService convertService) {
        this.saveService = saveService;
        this.convertService = convertService;
    }

    /** 設定 */
    String mediaName = "COMIC熱帯";
    String flashComment = "COMIC熱帯の更新情報が登録されました！";
    List<String> rootUrls = new ArrayList<>() {
        {
            add("https://www.comicnettai.com/series");
            add("https://www.comicnettai.com/series?page=2");
        }
    };

    String additionalUrl = "";
    String listsXpath = "//a[@class='full--comic__item col3--comic__item']";


    /** Xpath */
    String titleStringXpath = "//h1";
    String authorStringXpath = "//span[@class='detail__author__item']";
    String urlXpath = "/";
    String imgUrlXpath = "//img";
    String subTitleXpath = "//h2";
    String updateXpath = "//p[@class='detail--product__item__sdate']";

    /** メソッド */
    @PostMapping("/comicnettai/{no}")
    public String scrape(RedirectAttributes attrs, @PathVariable("no") Integer no) throws IOException {
        String rootUrl = rootUrls.get(no);
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
        Document doc = Jsoup.connect(childUrl).get();

        String titleString = convertService.getText(doc, titleStringXpath);
        String authorString = convertService.getText(doc, authorStringXpath);

        Elements lists = doc.selectXpath("//a[@class='js-open-next-url detail--product__item is-open']");
        for (int i = 0; i < lists.size(); i++) {
            String html = lists.get(i).html();
            Document document = Jsoup.parse(html);
//            String url = lists.get(i).attr("href");   //401unauthorizedになってしまうので作品ページへのリンクに
            String url = childUrl;
            String imgUrl = document.selectXpath(imgUrlXpath).attr("data-src");
            String subTitle = document.selectXpath(subTitleXpath).text();
            LocalDateTime update = getUpdate(document, updateXpath);
            Integer freeFlag = 1;

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
            saveService.saveRssNettai(parseContents);
        }
    }

    public LocalDateTime getUpdate(Document document, String xpath) {
        String updateRaw = document.selectXpath(updateXpath).text();
        String updateReplace = updateRaw.replace(".", "/");
        String[] updateSplit = updateReplace.split("/");

        int month = Integer.parseInt(updateSplit[1]);
        int day = Integer.parseInt(updateSplit[2]);
        String updateMonth = (String)String.format("%02d", month);
        String updateDay = (String)String.format("%02d", day);
        String updateString = updateSplit[0] + "/" + updateMonth + "/" +updateDay;
        LocalDateTime update = LocalDateTime.of(LocalDate.parse(updateString, DateTimeFormatter.ofPattern("yyyy/MM/dd")), LocalTime.of(17,0));

        return update;
    }
}
