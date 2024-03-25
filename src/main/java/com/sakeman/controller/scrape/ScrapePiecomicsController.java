package com.sakeman.controller.scrape;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
public class ScrapePiecomicsController {

    private final WebMangaUpdateInfoSaveService saveService;
    private final ScrapeConvertService convertService;
    public ScrapePiecomicsController (WebMangaUpdateInfoSaveService saveService, ScrapeConvertService convertService) {
        this.saveService = saveService;
        this.convertService = convertService;
    }

    /** 設定 */
    String mediaName = "PIE COMICS";
    String flashComment = "PIE COMICSの更新情報が登録されました！";
    String rootUrl = "https://comics.pie.co.jp/comicart/manga/";
    String additionalUrl = "";
    String listsXpath = "//section[@class='p-categoryBlock_item']/div/div/a";
    Integer freeFlag = 1;


    /** Xpath */
    String titleStringXpath = "//h1[@class='p-work_title']";
    String authorStringXpath = "//p[@class='p-work_author']";
    String imgUrlXpath = "//div[@class='p-work_image-sp']/img";

    String urlXpath = "//a";
    String subTitleXpath = "//span[@class='p-series_itemTitle']";
    String updateXpath = "//time";


    /** メソッド */
    @PostMapping("/piecomics")
    public String scrape(RedirectAttributes attrs) throws IOException {
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
        String imgUrl = doc.selectXpath(imgUrlXpath).attr("src");

        Elements lists = doc.selectXpath("//li[@class='p-series_item is-readmore-item']");
        for (int i = 0; i < lists.size(); i++) {
            String html = lists.get(i).html();
            Document document = Jsoup.parse(html);

            if (document.selectXpath("//div[@class='p-series_item_btn']").isEmpty()) {
                continue;
            } else {
                String url = document.selectXpath(urlXpath).attr("href");
                String subTitle = document.selectXpath(subTitleXpath).text();
                LocalDateTime update = getUpdate(document, updateXpath);

                System.out.println("===============================");
                System.out.println(titleString);
                System.out.println(authorString);
                System.out.println(url);
                System.out.println(imgUrl);
                System.out.println(subTitle);
                System.out.println(update);
                System.out.println(freeFlag);

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
    }


    public LocalDateTime getUpdate(Document document, String updateXpath) {
        String updateRaw = document.selectXpath(updateXpath).text();
        String updateReplace = updateRaw.replace("更新", "").replace(".", "/");
        String[] updateSplit = updateReplace.split("/");

        int month = Integer.parseInt(updateSplit[1]);
        int day = Integer.parseInt(updateSplit[2]);
        String updateMonth = (String)String.format("%02d", month);
        String updateDay = (String)String.format("%02d", day);
        String updateString = updateSplit[0] + "/" + updateMonth + "/" +updateDay;
        LocalDateTime update = LocalDateTime.of(LocalDate.parse(updateString, DateTimeFormatter.ofPattern("yyyy/MM/dd")), LocalTime.of(0,0));

        return update;
    }
}
