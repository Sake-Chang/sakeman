package com.sakeman.controller.scrape;

import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
public class ScrapeWebcomicganmaController {

    private final WebMangaUpdateInfoService webService;
    private final MangaService mangaService;
    private final WebMangaMediaService webMangaMediaService;
    private final WebMangaTitleConverterService webMangaTitleConverterService;
    private final WebMangaUpdateInfoSaveService saveService;

    public ScrapeWebcomicganmaController (WebMangaUpdateInfoSaveService saveService, WebMangaUpdateInfoService webService, MangaService mangaService, WebMangaMediaService webMangaMediaService, WebMangaTitleConverterService webMangaTitleConverterService) {
        this.webService = webService;
        this.mangaService = mangaService;
        this.webMangaMediaService = webMangaMediaService;
        this.webMangaTitleConverterService = webMangaTitleConverterService;
        this.saveService = saveService;
    }

    @PostMapping("/web-comic-ganma")
    public String scrape(RedirectAttributes attrs) throws IOException {
        String rootUrl = "https://webcomicgamma.takeshobo.co.jp/manga";
        Document doc = Jsoup.connect(rootUrl).get();

        Elements lists = doc.selectXpath("//div[@class='tab_panel active']//div[@class='manga_item alpha']/a");
        for (Element list: lists) {
            String childUrl = list.attr("href");
            scrapeChild(childUrl);
        }
        attrs.addFlashAttribute("success", "WEBコミックガンマの更新情報が登録されました！");
        return "redirect:/admin/index";
    }

    public void scrapeChild(String childUrl) throws IOException {
        String linkUrl = "https://webcomicgamma.takeshobo.co.jp" + childUrl;
        Document doc = Jsoup.connect(linkUrl).get();

        String mediaName = "WEBコミックガンマ";
        String titleStringRaw = doc.selectXpath("//ul[@class='manga__title']/li").first().text();
        String titleString = Normalizer.normalize(titleStringRaw, Form.NFKC);
        String authorString = doc.selectXpath("//ul[@class='manga__title']/li").next().first().text();

        Elements contents = doc.selectXpath("//li[contains(text(), '公開日')]");
        for (int i = 1; i<=contents.size(); i++) {
            String xpathUrl = "(//li[contains(text(), '公開日')])[" + i + "]/ancestor::div[@class='read__outer']/descendant::a";
            String xpathImgUrl = "(//li[contains(text(), '公開日')])[" + i + "]/ancestor::div[@class='read__outer']/descendant::li[@class='thumb']/img";
            String xpathSubTitle = "(//li[contains(text(), '公開日')])[" + i + "]/ancestor::div[@class='read__outer']/descendant::li[@class='episode']";
            String xpathUpdate = "(//li[contains(text(), '公開日')])[" + i + "]/ancestor::div[@class='read__outer']/descendant::li[@class='episode__text']";

            String urlAb = doc.selectXpath(xpathUrl).attr("href");
            String urlRel = urlAb.replace("../../..", "");
            String url = "https://webcomicgamma.takeshobo.co.jp" + urlRel;
            String imgUrlRaw = doc.selectXpath(xpathImgUrl).attr("src");
            String imgUrlRel = imgUrlRaw.replace("../..", "");
            String imgUrl = "https://webcomicgamma.takeshobo.co.jp" + imgUrlRel;
            String subTitle = doc.selectXpath(xpathSubTitle).first().text();
            String updateRaw = doc.selectXpath(xpathUpdate).first().text();
            String updateReplace = updateRaw.replace("年", "/").replace("月", "/").replace("日", "");
            String[] updateSplit = updateReplace.split("/");
            int month = Integer.parseInt(updateSplit[1]);
            int day = Integer.parseInt(updateSplit[2]);
            String updateMonth = (String)String.format("%02d", month);
            String updateDay = (String)String.format("%02d", day);
            String updateString = updateSplit[0] + "/" + updateMonth + "/" +updateDay;
            LocalDateTime update = LocalDateTime.of(LocalDate.parse(updateString, DateTimeFormatter.ofPattern("yyyy/MM/dd")), LocalTime.of(0,0));

//            System.out.println("===============================");
//            System.out.println(titleString);
//            System.out.println(authorString);
//            System.out.println(url);
//            System.out.println(imgUrl);
//            System.out.println(subTitle);
//            System.out.println(update);

            Map<String, Object> parseContents = new HashMap<>();
                parseContents.put("mediaName", mediaName);
                parseContents.put("titleString", titleString);
                parseContents.put("subTitle", subTitle);
                parseContents.put("authorString", authorString);
                parseContents.put("url", url);
                parseContents.put("imgUrl", imgUrl);
                parseContents.put("updateAt", update);
                parseContents.put("freeFlag", 1);
            saveService.saveRss(parseContents);
        }
    }
}
