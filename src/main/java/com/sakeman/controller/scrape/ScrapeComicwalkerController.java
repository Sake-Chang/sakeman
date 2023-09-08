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
public class ScrapeComicwalkerController {

    private final WebMangaUpdateInfoService webService;
    private final MangaService mangaService;
    private final WebMangaMediaService webMangaMediaService;
    private final WebMangaTitleConverterService webMangaTitleConverterService;

    public ScrapeComicwalkerController (WebMangaUpdateInfoService webService, MangaService mangaService, WebMangaMediaService webMangaMediaService, WebMangaTitleConverterService webMangaTitleConverterService) {
        this.webService = webService;
        this.mangaService = mangaService;
        this.webMangaMediaService = webMangaMediaService;
        this.webMangaTitleConverterService = webMangaTitleConverterService;
    }

//    WebDriver driver;
//    void setupChromeDriver() {
//        WebDriverManager.chromedriver().setup();
//        driver = new ChromeDriver();
//    }

    @PostMapping("/comicwalker")
    public String scrapeComicwalker(RedirectAttributes attrs) throws IOException {

        // Seleniumバージョン
//        System.setProperty("webdriver.chrome.driver", "exe/chromedriver");
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--remote-allow-origins=*");
//        options.addArguments("--user-agent=Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
//        ChromeDriver driver = new ChromeDriver(options);
//
//        driver.get("https://comic-walker.com");
//        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
//
//        String xpath = "//ul[@class='tileList clearfix']/li/div/a";
//        List<WebElement> links = driver.findElements(By.xpath(xpath));
//        for (WebElement link : links) {
//            String childUrl = link.getAttribute("href");
//            System.out.println("childUrl : " + childUrl);
//        }
//        driver.quit();

        String userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1";
        Document doc = Jsoup.connect("https://comic-walker.com").userAgent(userAgent).get();


        Elements lists = doc.selectXpath("//ul[@class='tileList clearfix']/li/div/a");
        for (Element list: lists) {
            String childUrl = list.attr("href");
            System.out.println(childUrl);
            if (childUrl.contains("/news/detail/")) {
                continue;
            } else if (childUrl.contains("https://comic-walker.com/ranking/")) {
                continue;
            } else {
                scrapeComicwalkerChild(childUrl);
            }
        }
        attrs.addFlashAttribute("success", "ComicWalkerの更新情報が登録されました！");
        return "redirect:/admin/index";
    }


    public void scrapeComicwalkerChild(String childUrl) throws IOException {
        String linkUrl = "https://comic-walker.com" + childUrl;
        Document doc = Jsoup.connect(linkUrl).get();

        String mediaName = "ComicWalker";
        String titleStringRaw = doc.selectXpath("//div[@class='comicIndex-box']//img").attr("alt");
//        String titleStringRaw = doc.select(".comicIndex-box > div > h1").text();
            String titleString = Normalizer.normalize(titleStringRaw, Form.NFKC);
        String authorString = doc.selectXpath("//div[@class='acItem-copy']/a").text();
        String urlRelative = doc.selectXpath("//p[@class='btn-detailLink link-p']/a").attr("href");
        String url = "https://comic-walker.com" + urlRelative;
        String imgUrl = doc.selectXpath("//div[@class='comicIndex-box']//img").attr("src");
        String subTitle1 = doc.selectXpath("//div[@class='comicIndex-box']//p[@class='comicIndex-title']").text();
        String subTitle2 = doc.selectXpath("//div[@class='comicIndex-box']//p[@class='comicIndex-subTitle']").text();
        String subTitle = subTitle1 + " " + subTitle2;
        String updateRaw = doc.selectXpath("//div[@class='comicIndex-box']//span[@class='comicIndex-date']").text().replace(" 更新", "");
        String[] updateSplit = updateRaw.split("/");
        int month = Integer.parseInt(updateSplit[1]);
        int day = Integer.parseInt(updateSplit[2]);
        String updateMonth = (String)String.format("%02d", month);
        String updateDay = (String)String.format("%02d", day);
        String updateString = updateSplit[0] + "/" + updateMonth + "/" +updateDay;
        LocalDateTime update = LocalDateTime.of(LocalDate.parse(updateString, DateTimeFormatter.ofPattern("yyyy/MM/dd")), LocalTime.of(12,0));

//        System.out.println(titleString);
//        System.out.println(authorString);
//        System.out.println(url);
//        System.out.println(imgUrl);
//        System.out.println(subTitle);
//        System.out.println(update);

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
