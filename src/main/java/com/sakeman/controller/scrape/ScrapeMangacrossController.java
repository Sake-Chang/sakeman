//package com.sakeman.controller.scrape;
//
//import java.io.IOException;
//import java.text.Normalizer;
//import java.text.Normalizer.Form;
//import java.time.Duration;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Optional;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import com.gargoylesoftware.htmlunit.BrowserVersion;
//import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
//import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
//import com.gargoylesoftware.htmlunit.WebClient;
//import com.gargoylesoftware.htmlunit.html.HtmlPage;
//import com.microsoft.playwright.Browser;
//import com.microsoft.playwright.BrowserType;
//import com.microsoft.playwright.Locator;
//import com.microsoft.playwright.Page;
//import com.microsoft.playwright.Playwright;
//import com.sakeman.entity.WebMangaTitleConverter;
//import com.sakeman.entity.WebMangaUpdateInfo;
//import com.sakeman.service.MangaService;
//import com.sakeman.service.WebMangaMediaService;
//import com.sakeman.service.WebMangaTitleConverterService;
//import com.sakeman.service.WebMangaUpdateInfoService;
//
//@Controller
//@RequestMapping("scrape")
//public class ScrapeMangacrossController {
//
//    private final WebMangaUpdateInfoService webService;
//    private final MangaService mangaService;
//    private final WebMangaMediaService webMangaMediaService;
//    private final WebMangaTitleConverterService webMangaTitleConverterService;
//
//    public ScrapeMangacrossController (WebMangaUpdateInfoService webService, MangaService mangaService, WebMangaMediaService webMangaMediaService, WebMangaTitleConverterService webMangaTitleConverterService) {
//        this.webService = webService;
//        this.mangaService = mangaService;
//        this.webMangaMediaService = webMangaMediaService;
//        this.webMangaTitleConverterService = webMangaTitleConverterService;
//    }
//
//    @GetMapping("/mangacross")
//    public String scrape(RedirectAttributes attrs) throws IOException {
//        try (Playwright playwright = Playwright.create()) {
//            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions();
//            Browser browser = playwright.webkit().launch(options);
//            Page page = browser.newPage();
//            page.navigate("https://mangacross.jp");
//            page.waitForTimeout(1000);
//
//            List<Locator> elements = page.locator("//div[@class='card']/a").all();
//            for (Locator element : elements) {
//                String childUrl = element.getAttribute("href");
//                if (webService.findByUrl(childUrl).isEmpty()) {
//                    scrapeChild(childUrl, browser);
//                }
//            }
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//        return "test";
//    }
//
//    public void scrapeChild(String childUrl, Browser browser) throws IOException {
////        try (Playwright playwright = Playwright.create()) {
////            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions();
////            Browser browser = playwright.webkit().launch(options);
//            Page page = browser.newPage();
//            String childUrlAbs = "https://mangacross.jp" + childUrl;
//            page.navigate(childUrlAbs);
//            page.waitForTimeout(1000);
//
//            String mediaName = "マンガクロス";
//            String titleXpath = "//h1[@class='comic-area__title']";
//            String titleStringRaw = page.locator(titleXpath).textContent().trim();
//            String titleString = Normalizer.normalize(titleStringRaw, Form.NFKC);
//            String authorXpath = "//h2[@class='comic-area__author']";
//            String authorString = page.locator(authorXpath).textContent().replace(" ", "").trim();
//            String url = childUrl;
//            String imgUrlXpath = "//div[@class='backnumber-list__img']/div[@class='img-box']/img";
//            String imgUrl = page.locator(imgUrlXpath).first().getAttribute("src");
//            String subTitleXpath1 = "//div[@class='backnumber-list__text']/span[@class='backnumber-list__text--episode']";
//            String subTitleXpath2 = "//div[@class='backnumber-list__text']/span[@class='backnumber-list__text--sub-title']";
//            String subTitle1 = page.locator(subTitleXpath1).first().textContent();
//            String subTitle2 = page.locator(subTitleXpath2).first().textContent();
//            String subTitle = subTitle1 + " " + subTitle2;
//            String updateXpath = "//div[@class='backnumber-list__text']/span[@class='backnumber-list__text--update']";
//            String updateRaw = page.locator(updateXpath).first().textContent().replace(" UP", "").replace("NEW", "");
//            String[] updateSplit = updateRaw.split("/");
//            int month = Integer.parseInt(updateSplit[1]);
//            int day = Integer.parseInt(updateSplit[2]);
//            String updateMonth = (String)String.format("%02d", month);
//            String updateDay = (String)String.format("%02d", day);
//            String updateString = updateSplit[0] + "/" + updateMonth + "/" +updateDay;
//            LocalDateTime update = LocalDateTime.of(LocalDate.parse(updateString, DateTimeFormatter.ofPattern("yyyy/MM/dd")), LocalTime.of(12,0));
//
////            System.out.println(mediaName);
////            System.out.println(titleString);
////            System.out.println(authorString);
////            System.out.println(url);
////            System.out.println(imgUrl);
////            System.out.println(subTitle);
////            System.out.println(update);
//
////        } catch (Exception e) {
////            System.out.println(e);
////        }
//
//            WebMangaUpdateInfo info = new WebMangaUpdateInfo();
//            if (webMangaMediaService.getWebMangaMediaByName(mediaName).isPresent()) {
//                info.setWebMangaMedia(webMangaMediaService.getWebMangaMediaByName(mediaName).get());
//            } else {
//                info.setWebMangaMedia(webMangaMediaService.getWebMangaMedia(1));
//            }
//            info.setMediaName(mediaName);
//            info.setTitleString(titleString);
//            Optional<WebMangaTitleConverter> converterlist = webMangaTitleConverterService.findByTitleStrignAndAuthorString(titleString, authorString);
//            if (converterlist.isPresent()) {
//                info.setManga(converterlist.get().getManga());
//            } else if (mangaService.getMangaByTitle(titleString).isPresent()) {
//                info.setManga(mangaService.getMangaByTitle(titleString).get());
//            } else {
//                info.setManga(mangaService.getManga(1));
//            }
//            info.setSubTitle(subTitle);
//            info.setAuthorString(authorString);
//            info.setUrl(url);
//            info.setImgUrl(imgUrl);
//            info.setUpdateAt(update);
//            info.setFreeFlag(1);
//            webService.saveInfo(info);
//    }
//}
