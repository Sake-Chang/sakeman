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
public class ScrapeMichikusaController {

    private final WebMangaUpdateInfoSaveService saveService;
    private final ScrapeConvertService convertService;
    public ScrapeMichikusaController (WebMangaUpdateInfoSaveService saveService, ScrapeConvertService convertService) {
        this.saveService = saveService;
        this.convertService = convertService;
    }

    /** 設定 */
    String mediaName = "路草";
    String flashComment = "路草の更新情報が登録されました！";
    List<String> rootUrls = new ArrayList<>() {
        {
            add("https://michikusacomics.jp/product");
            add("https://michikusacomics.jp/product/page/2");
            add("https://michikusacomics.jp/product/page/3");
            add("https://michikusacomics.jp/product/page/4");
        }
    };

    String additionalUrl = "";
    String listsXpath = "//div[@class='entry__wrapper']//a[@class='entry-featured  entry-thumbnail']";


    /** Xpath */
    String titleStringXpath = "//h1";
    String authorStringXpath = "//span[@class='authorName']";
    String urlXpath = "//a";
    String imgUrlXpath = "//img";
    String subTitleXpath1 = "//div[@class='info']/div[1]";
    String subTitleXpath2 = "//div[@class='info']/div[@class='title']";
    String updateXpath = "//div[@class='info']/div[@class='publication_date']";

    /** メソッド */
    @PostMapping("/michikusa/{no}")
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

        Elements lists = doc.selectXpath("//div[@class='mix']");
        for (int i = 0; i < lists.size(); i++) {
            String html = lists.get(i).html();
            Document document = Jsoup.parse(html);

            String url = document.selectXpath(urlXpath).attr("href");
            Boolean urlIs = document.selectXpath(urlXpath).attr("href").isEmpty();
            if (urlIs) { continue; }
            String imgUrl = document.selectXpath(imgUrlXpath).attr("src");
            String subTitle1 = document.selectXpath(subTitleXpath1).text();
            String subTitle2 = document.selectXpath(subTitleXpath2).text();
            String subTitle = subTitle1 + " " + subTitle2;
            LocalDateTime update = getUpdate(document, updateXpath);
            Integer freeFlag = 1;

//            System.out.println("===============================");
//            System.out.println(titleString);
//            System.out.println(authorString);
//            System.out.println(url);
//            System.out.println(urlIs);
//            System.out.println(imgUrl);
//            System.out.println(subTitle);
//            System.out.println(updateString);
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
