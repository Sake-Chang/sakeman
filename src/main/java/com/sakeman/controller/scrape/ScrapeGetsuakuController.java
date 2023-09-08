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
public class ScrapeGetsuakuController {

    private final WebMangaUpdateInfoSaveService saveService;
    private final ScrapeConvertService convertService;
    public ScrapeGetsuakuController (WebMangaUpdateInfoSaveService saveService, ScrapeConvertService convertService) {
        this.saveService = saveService;
        this.convertService = convertService;
    }

    /** 設定 */
    String flashComment = "げつあくWEB の更新情報が登録されました！";
    String rootUrl = "https://getsuaku.com/";
    String additionalUrl = "";
    String listsXpath = "//div[@class='comics__gridItem']/a";

    String mediaName = "げつあくWEB";
    Integer freeFlag = 1;

    /** Xpath */
    String titleStringXpath = "//h2[@class='detail__title']";
    String authorStringXpath = "//div[@class='detail__artist']";
    String urlXpath = "";
    String imgUrlXpath = "//div[@class='detail__img']/img";
    String subTitleXpath = "//h1[@class='detailHead__title']";
    String updateXpath = "//div[@class='detailHead__body']//span[@class='detailHead__bold'][1]";


    /** メソッド */
    @PostMapping("/getsuaku")
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
        String url = childUrl;
        String imgUrl = convertService.getImgUrl(doc, imgUrlXpath);
        String subTitle = convertService.getText(doc, subTitleXpath);
        //String update = convertService.getText(doc,  updateXpath);
        LocalDateTime update = getUpdate(doc, updateXpath);

//        System.out.println("===============================");
//        System.out.println(titleString);
//        System.out.println(authorString);
//        System.out.println(url);
//        System.out.println(imgUrl);
//        System.out.println(subTitle);
//        System.out.println(update);

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

    private LocalDateTime getUpdate(Element doc, String xpath) {
        String updateRaw = doc.selectXpath(xpath).text();
        String updateReplace = updateRaw.replace("月", "/").replace("日", "");
        String[] updates = updateReplace.split("/");
        LocalDateTime now = LocalDateTime.now();
        String year = Integer.valueOf(now.getYear()).toString();
        Integer month = Integer.parseInt(updates[0]);
        Integer day = Integer.parseInt(updates[1]);
        String updateMonth = (String)String.format("%02d", month);
        String updateDay = (String)String.format("%02d", day);
        String updateString = year + "/" + updateMonth + "/" +updateDay;
        LocalDateTime update = LocalDateTime.of(LocalDate.parse(updateString, DateTimeFormatter.ofPattern("yyyy/MM/dd")), LocalTime.of(12,0));

        return update;
    }
}
