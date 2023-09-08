package com.sakeman.controller.scrape;

import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
import com.sakeman.service.WebMangaMediaService;
import com.sakeman.service.WebMangaTitleConverterService;
import com.sakeman.service.WebMangaUpdateInfoService;

@Controller
@RequestMapping("scrape")
public class ScrapeUrasundayController {

    private final WebMangaUpdateInfoService webService;
    private final MangaService mangaService;
    private final WebMangaMediaService webMangaMediaService;
    private final WebMangaTitleConverterService webMangaTitleConverterService;

    public ScrapeUrasundayController (WebMangaUpdateInfoService webService, MangaService mangaService, WebMangaMediaService webMangaMediaService, WebMangaTitleConverterService webMangaTitleConverterService) {
        this.webService = webService;
        this.mangaService = mangaService;
        this.webMangaMediaService = webMangaMediaService;
        this.webMangaTitleConverterService = webMangaTitleConverterService;
    }

    @PostMapping("/urasunday")
    public String scrape(RedirectAttributes attrs) throws IOException {
        Document doc = Jsoup.connect("https://urasunday.com/serial_title").get();

        Elements lists = doc.selectXpath("//div[@class='title-all-list']/ul/li/a");
        for (Element list: lists) {
            String childUrl = list.attr("href");
//            System.out.println(childUrl);
            scrapeChild(childUrl);
        }
        attrs.addFlashAttribute("success", "裏サンデーの更新情報が登録されました！");
        return "redirect:/admin/index";
    }


    public void scrapeChild(String childUrl) throws IOException {
        String linkUrl = "https://urasunday.com" + childUrl;
        Document doc = Jsoup.connect(linkUrl).get();

        String mediaName = "裏サンデー";
        String titleStringRaw = doc.selectXpath("//div[@class='info']/h1").text();
        String titleString = Normalizer.normalize(titleStringRaw, Form.NFKC);
        String authorString = doc.selectXpath("//div[@class='info']/div[@class='author']").text();
        String urlRelative = doc.selectXpath("//h1[text()='チャプター一覧']/following-sibling::ul/li/a").first().attr("href");
        String url = "https://urasunday.com" + urlRelative;
        String imgUrl = doc.selectXpath("//h1[text()='チャプター一覧']/following-sibling::ul/li/a/img").first().attr("src");
        String subTitle1 = doc.selectXpath("//h1[text()='チャプター一覧']/following-sibling::ul/li/a/div/div[not(contains(@class,'new'))]").first().text();
        String subTitle2 = doc.selectXpath("//h1[text()='チャプター一覧']/following-sibling::ul/li/a/div/div[not(contains(@class,'new'))]").next().first().text();
        String subTitle = subTitle1 + " " + subTitle2;
        String updateRaw = doc.selectXpath("//h1[text()='チャプター一覧']/following-sibling::ul/li/a/div/div[not(contains(@class,'new'))]").next().next().first().text();
        String[] updateSplit = updateRaw.split("/");
        int month = Integer.parseInt(updateSplit[1]);
        int day = Integer.parseInt(updateSplit[2]);
        String updateMonth = (String)String.format("%02d", month);
        String updateDay = (String)String.format("%02d", day);
        String updateString = updateSplit[0] + "/" + updateMonth + "/" +updateDay;
        LocalDateTime update = LocalDateTime.of(LocalDate.parse(updateString, DateTimeFormatter.ofPattern("yyyy/MM/dd")), LocalTime.of(0,0));

        String className = doc.selectXpath("//h1[text()='チャプター一覧']/following-sibling::ul/li").first().attr("class");

//        System.out.println(titleString);
//        System.out.println(authorString);
//        System.out.println(url);
//        System.out.println(imgUrl);
//        System.out.println(subTitle);
//        System.out.println(update);
//        System.out.println(className);


//        System.out.println(webService.findByUrl(url).isEmpty());
        if (webService.findByUrl(url).isEmpty()) {
            WebMangaUpdateInfo info = new WebMangaUpdateInfo();
            if (webMangaMediaService.getWebMangaMediaByName(mediaName).isPresent()) {
                info.setWebMangaMedia(webMangaMediaService.getWebMangaMediaByName(mediaName).get());
            } else {
                info.setWebMangaMedia(webMangaMediaService.getWebMangaMedia(1));
            }
            info.setMediaName(mediaName);
            info.setTitleString(titleString);
            Optional<WebMangaTitleConverter> converterlist = webMangaTitleConverterService.findByTitleStrignAndAuthorString(titleString, authorString);
            if (converterlist.isPresent()) {
                info.setManga(converterlist.get().getManga());
            } else if (mangaService.getMangaByTitle(titleString).isPresent()) {
                info.setManga(mangaService.getMangaByTitle(titleString).get());
            } else {
                info.setManga(mangaService.getManga(1));
            }
            info.setSubTitle(subTitle);
            info.setAuthorString(authorString);
            info.setUrl(url);
            info.setImgUrl(imgUrl);
            info.setUpdateAt(update);
            if (className == "") {
                info.setFreeFlag(1);
            } else {
                info.setFreeFlag(0);
            }
            webService.saveInfo(info);
        }
    }
}
