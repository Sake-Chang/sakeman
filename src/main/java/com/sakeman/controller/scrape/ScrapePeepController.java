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
public class ScrapePeepController {

    private final WebMangaUpdateInfoSaveService saveService;
    private final ScrapeConvertService convertService;
    public ScrapePeepController (WebMangaUpdateInfoSaveService saveService, ScrapeConvertService convertService) {
        this.saveService = saveService;
        this.convertService = convertService;
    }

    /** 設定 */
    String mediaName = "peep";
    String flashComment = "peepの更新情報が登録されました！";
    String rootUrl = "https://peep.jp/topics/EZrLGGcki7g0fQcK";

    String additionalUrl = "https://peep.jp/topics";
    String listsXpath = "//li[@class='css-as0r6e-ListItemStyle e5wsu6n0']//a[@class='css-15580jj-FillSPALinkButton-ButtonSkin-FillButton ejam3v411']";


    /** Xpath */
    String titleStringXpath = "//h2";
    String authorStringXpath = "//div[@class='css-bvp0mx-Authors e48vg8j22']";
    String urlXpath = "/a";
    String imgUrlXpath = "";
    String subTitleXpath = "//h4";
    String updateXpath = "//dt[contains(text(), '更新日')][1]/following-sibling::dd[1]";


    /** メソッド */
    @PostMapping("/peep")
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
        String linkUrl = additionalUrl +childUrl;
        Document doc = Jsoup.connect(linkUrl).get();

        String titleString = convertService.getText(doc, titleStringXpath);
        String authorString = convertService.getText(doc, authorStringXpath);

        Elements lists = doc.selectXpath("//li[@class='css-5j812p-ChapterItem e647z0r5']");
        for (int i = 0; i < lists.size(); i++) {
            String html = lists.get(i).html();
            Document document = Jsoup.parse(html);

            String url = document.selectXpath(urlXpath).attr("href");
            Boolean isNotFree = document.selectXpath(urlXpath).attr("href").isEmpty();
            if (isNotFree) continue;
            //String imgUrl = getImgUrl(document, imgUrlXpath);
            String subTitle = document.selectXpath(subTitleXpath).get(0).text();
            LocalDateTime update = getUpdate(document, updateXpath);
            Integer freeFlag = 1;

            System.out.println("===============================");
            System.out.println(titleString);
            System.out.println(authorString);
            System.out.println(url);
//            System.out.println(imgUrl);
            System.out.println(subTitle);
            System.out.println(update);
            System.out.println(freeFlag);

            Map<String, Object> parseContents = new HashMap<>();
                parseContents.put("mediaName", mediaName);
                parseContents.put("titleString", titleString);
                parseContents.put("subTitle", subTitle);
                parseContents.put("authorString", authorString);
                parseContents.put("url", url);
                //parseContents.put("imgUrl", imgUrl);
                parseContents.put("updateAt", update);
                parseContents.put("freeFlag", freeFlag);
            //saveService.saveRss(parseContents);
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
