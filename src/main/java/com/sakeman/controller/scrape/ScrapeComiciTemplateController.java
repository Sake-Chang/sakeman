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

import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.xml.sax.SAXException;

import com.sakeman.service.RssParseService;
import com.sakeman.service.ScrapeConvertService;
import com.sakeman.service.WebMangaUpdateInfoSaveService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
//@RequestMapping("scrape")
public class ScrapeComiciTemplateController {
    private final WebMangaUpdateInfoSaveService saveService;
    private final ScrapeConvertService convertService;

    /** 設定 */
    final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36";
    final String LOGIN_FORM_URL = "https://bigcomics.jp/signin";
    final String USERNAME = "ngrqspcial@gmail.com";
    final String PASSWORD = "Wakuwaku@@56";

    String mediaName = "Default";
    String domain = "default";
    String flashComment = String.format("%sの更新情報が登録されました！", mediaName);

    List<String> rootUrls = new ArrayList<>();

    /** Xpath */
    String rootListXpath = "//div[@class='category-list grid-category-template']/a";
    String childListXpath = "//div[@class='series-ep-list']/div";
    String titleStringXpath = "//h1/span[not(contains(@class, 'g-hidden'))]";
    String authorStringXpath = "//div[@class='series-h-credit-user']";
    String urlXpath = "//a";
    String imgUrlXpath = "//source[contains(@media, '(max-width: 767px)')]";
    String subTitleXpath = "//span[contains(@class, 'series-ep-list-item-h-text')]";
    String updateXpath = "//time";
    String freeFlagXpath1 = "/descendant::div[@class='series-ep-list-symbols']//div[@class='free-icon-new-container']";
    String freeFlagXpath2 = "/descendant::div[@class='series-ep-list-symbols']//img";

    /** メソッド */
    //@PostMapping("/bigcomics/{dow}")
    public String scrape(RedirectAttributes attrs, String rootUrl) throws IOException {
        Map<String, String> COOKIES = login();

        Document doc = Jsoup.connect(rootUrl).userAgent(USER_AGENT).cookies(COOKIES).get();
        Elements lists = doc.selectXpath(rootListXpath);

        List<Map> contentsList = new ArrayList<>();

        for (Element list: lists) {
            String childUrl = list.attr("href");
            if (childUrl.equals("")) {
                continue;
            }
            List<Map> contentsListChild = scrapeChild(childUrl, COOKIES);
            contentsList.addAll(contentsListChild);
        }
        saveService.saveAllRss(contentsList);

        attrs.addFlashAttribute("success", flashComment);
        return "redirect:/admin/index";
    }

    public List<Map> scrapeChild(String childUrl, Map<String, String> COOKIES) throws IOException {
        String linkUrl = childUrl + "?s=1" ;
        Document doc = Jsoup.connect(linkUrl).userAgent(USER_AGENT).cookies(COOKIES).get();

        String titleString = convertService.getText(doc, titleStringXpath);
        String authorString = convertService.getText(doc, authorStringXpath);

        Elements lists = doc.selectXpath(childListXpath);

        List<Map> contentsListChild = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            String html = lists.get(i).html();
            Document document = Jsoup.parse(html);

            String url = document.selectXpath(urlXpath).attr("data-href");
            String imgUrl = getImgUrl(document, imgUrlXpath);
            String subTitle = document.selectXpath(subTitleXpath).text();
            LocalDateTime update = getUpdate(document, updateXpath);
            Integer freeFlag = getFreeFlag(document, freeFlagXpath1, freeFlagXpath2);

            Map<String, Object> parseContents = new HashMap<>();
                parseContents.put("mediaName", mediaName);
                parseContents.put("titleString", titleString);
                parseContents.put("subTitle", subTitle);
                parseContents.put("authorString", authorString);
                parseContents.put("url", url);
                parseContents.put("imgUrl", imgUrl);
                parseContents.put("updateAt", update);
                parseContents.put("freeFlag", freeFlag);
            contentsListChild.add(parseContents);
        }
        return contentsListChild;
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

    public Map<String, String> login() throws IOException{
        // ログインページを取得
        Connection.Response loginFormResponse = Jsoup.connect(LOGIN_FORM_URL)
                                                    .method(Connection.Method.GET)
                                                    .userAgent(USER_AGENT)
                                                    .execute();
        Map<String, String> COOKIES = loginFormResponse.cookies();
        String redirectUrl = String.format("https://%s/?logout", domain);
        COOKIES.put("login_success_redirect_url", redirectUrl);
        COOKIES.put("Path", "/");

        // フォームを取得
        FormElement loginForm = (FormElement)loginFormResponse.parse()
                                                    .selectXpath("//form").first();
        checkElement("Login Form", loginForm);
        // ユーザー名欄を取得＆値をセット
        Element loginField = loginForm.selectXpath("//input[@name='username']").first();
        checkElement("Login Field", loginField);
        loginField.val(USERNAME);
        // パスワード欄を取得＆値をセット
        Element passwordField = loginForm.selectXpath("//input[@name='password']").first();
        checkElement("Password Field", passwordField);
        passwordField.val(PASSWORD);
        // ログイン実行
        Connection.Response loginActionResponse = loginForm.submit()
                                    .cookies(COOKIES)
                                    .userAgent(USER_AGENT)
                                    .execute();
        //System.out.println(loginActionResponse.parse().html());

        System.out.println(loginActionResponse.statusCode());
        COOKIES.putAll(loginActionResponse.cookies());
        COOKIES.put("login_success_redirect_url", "");
        COOKIES.put("Max-Age", "0");
        COOKIES.put("Expires", "Thu, 01-Jan-1970 00:00:10 GMT");

        return COOKIES;
    }

    public static void checkElement(String name, Element elem) {
        if (elem == null) {
        throw new RuntimeException("Unable to find " + name);
        }
    }

}
