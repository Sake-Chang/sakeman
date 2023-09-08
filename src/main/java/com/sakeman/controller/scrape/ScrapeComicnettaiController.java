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
public class ScrapeComicnettaiController {

    private final WebMangaUpdateInfoService webService;
    private final MangaService mangaService;
    private final WebMangaMediaService webMangaMediaService;
    private final WebMangaTitleConverterService webMangaTitleConverterService;

    public ScrapeComicnettaiController (WebMangaUpdateInfoService webService, MangaService mangaService, WebMangaMediaService webMangaMediaService, WebMangaTitleConverterService webMangaTitleConverterService) {
        this.webService = webService;
        this.mangaService = mangaService;
        this.webMangaMediaService = webMangaMediaService;
        this.webMangaTitleConverterService = webMangaTitleConverterService;
    }

    @GetMapping("/comicnettai")
    public String scrape(RedirectAttributes attrs) throws IOException {
        Document doc = Jsoup.connect("https://www.comicnettai.com").get();

        Elements lists = doc.selectXpath("//div[@class='comic__item']/a");
        for (Element list: lists) {
            String childUrl = list.attr("href");
            System.out.println(childUrl);
            scrapeChild(childUrl);
        }
        attrs.addFlashAttribute("success", "コミック熱帯の更新情報が登録されました！");
        return "test";
//        return "redirect:/admin/index";
    }


    public void scrapeChild(String childUrl) throws IOException {
        String linkUrl = "https://www.comicnettai.com" + childUrl;
        Document doc = Jsoup.connect(linkUrl).get();

        String mediaName = "コミック熱帯";
        String titleStringRaw = doc.selectXpath("//h1").text().trim();
        String titleString = Normalizer.normalize(titleStringRaw, Form.NFKC);
        Elements authorStrings = doc.selectXpath("//div[@class='detail__author__list']/span");
        String authorString = "";
        for (int i = 0; i < authorStrings.size(); i++) {
            if (i == 0) {
                authorString = authorStrings.get(i).text().trim();
            } else {
                authorString = authorString.concat(" / ").concat(authorStrings.get(i).text().trim());
            }
        }
        /** ビューワーへのリンクが401 */
//        String url = doc.selectXpath("//a[@class='js-open-next-url detail--product__item is-open']").first().attr("href");
        String url = linkUrl;
        String imgUrl = doc.selectXpath("//a[@class='js-open-next-url detail--product__item is-open']//div[@class='detail--product__item__left']/img").attr("data-src");
        String subTitle = doc.selectXpath("//a[@class='js-open-next-url detail--product__item is-open']//div[@class='detail--product__item__center']/h2").first().text();
        String updateRaw = doc.selectXpath("//a[@class='js-open-next-url detail--product__item is-open']//div[@class='detail--product__item__center']/p").first().text();
//        String[] updateSplit = updateRaw.split(".");
//        int month = Integer.parseInt(updateSplit[1]);
//        int day = Integer.parseInt(updateSplit[2]);
//        String updateMonth = (String)String.format("%02d", month);
//        String updateDay = (String)String.format("%02d", day);
//        String updateString = updateSplit[0] + "/" + updateMonth + "/" +updateDay;
        LocalDateTime update = LocalDateTime.of(LocalDate.parse(updateRaw, DateTimeFormatter.ofPattern("yyyy.MM.dd")), LocalTime.of(12,0));

//        System.out.println(titleString);
//        System.out.println(authorString);
//        System.out.println(url);
//        System.out.println(imgUrl);
//        System.out.println(subTitle);
//        System.out.println(update);

        Boolean test = webService.findByUrl(url).isEmpty();
        System.out.println(test);
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
            info.setFreeFlag(1);
            webService.saveInfo(info);
        }
    }
}
