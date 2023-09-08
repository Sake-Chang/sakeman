package com.sakeman.service;

import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class RssParseService {
    private final WebMangaUpdateInfoSaveService saveService;
    public RssParseService(WebMangaUpdateInfoSaveService saveService) {
        this.saveService = saveService;
    }

    /** 1. 基本形 */
    public void getUpdateInfo(String source) throws ParserConfigurationException, SAXException, IOException {
        /** 設定 */
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(source);
        Element channel = document.getDocumentElement();        //ルート要素取得
        NodeList items = channel.getElementsByTagName("item");  //各item取得

        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);
            Map<String, Object> parseContents = new HashMap<>();
                parseContents.put("mediaName", getText(channel, "title"));
                parseContents.put("titleString", getText(item, "description"));
                parseContents.put("subTitle", getText(item, "title"));
                parseContents.put("authorString", getText(item, "author"));
                parseContents.put("url", getText(item, "link"));
                parseContents.put("imgUrl", getImgUrl(item, "enclosure"));
                parseContents.put("updateAt", getLocalDateTime(item, "pubDate", "UTC"));
                parseContents.put("freeFlag", getFreeFlag(item, "giga:freeTermStartDate"));
            saveService.saveRss(parseContents);
        }
    }

    /** 1ーb. 基本形（個別URL） */
    public void getUpdateInfo(String source, String media) throws ParserConfigurationException, SAXException, IOException {
        /** 設定 */
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(source);
        Element channel = document.getDocumentElement();        //ルート要素取得
        NodeList items = channel.getElementsByTagName("item");  //各item取得

        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);
            Map<String, Object> parseContents = new HashMap<>();
                parseContents.put("mediaName", media);
                parseContents.put("titleString", getText(item, "description"));
                parseContents.put("subTitle", getText(item, "title"));
                parseContents.put("authorString", getText(item, "author"));
                parseContents.put("url", getText(item, "link"));
                parseContents.put("imgUrl", getImgUrl(item, "enclosure"));
                parseContents.put("updateAt", getLocalDateTime(item, "pubDate", "UTC"));
                parseContents.put("freeFlag", getFreeFlag(item, "giga:freeTermStartDate"));
            saveService.saveRss(parseContents);
        }
    }


    /** 2. 全部無料 */
    public void getUpdateInfoFree(String source) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(source);
        Element channel = document.getDocumentElement();        //ルート要素取得
        NodeList items = channel.getElementsByTagName("item");  //各item取得

        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);
            Map<String, Object> parseContents = new HashMap<>();
                parseContents.put("mediaName", getText(channel, "title"));
                parseContents.put("titleString", getText(item, "description"));
                parseContents.put("subTitle", getText(item, "title"));
                parseContents.put("authorString", getText(item, "author"));
                parseContents.put("url", getText(item, "link"));
                parseContents.put("imgUrl", getImgUrl(item, "enclosure"));
                parseContents.put("updateAt", getLocalDateTime(item, "pubDate", "UTC"));
                parseContents.put("freeFlag", 1);       //全部無料にセット
            saveService.saveRss(parseContents);
        }
    }

    /** 3. 全部無料・タイトルの処理追加 */
    public void getUpdateInfoFreeYA(String source) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(source);
        Element channel = document.getDocumentElement();        //ルート要素取得
        NodeList items = channel.getElementsByTagName("item");  //各item取得

        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);
            Map<String, Object> parseContents = new HashMap<>();
                parseContents.put("mediaName", getText(channel, "title"));
                parseContents.put("titleString", getTextYA(item, "description"));   //処理追加
                parseContents.put("subTitle", getText(item, "title"));
                parseContents.put("authorString", getText(item, "author"));
                parseContents.put("url", getText(item, "link"));
                parseContents.put("imgUrl", getImgUrl(item, "enclosure"));
                parseContents.put("updateAt", getLocalDateTime(item, "pubDate", "UTC"));
                parseContents.put("freeFlag", 1);       //全部無料にセット
            saveService.saveRss(parseContents);
        }
    }

    /** 4. GANMA用：全部無料・タグ名とTZが違う */
    public void getUpdateInfoFreeGM(String source) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(source);
        Element channel = document.getDocumentElement();        //ルート要素取得
        NodeList items = channel.getElementsByTagName("item");  //各item取得

        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);
            Map<String, Object> parseContents = new HashMap<>();
                parseContents.put("mediaName", getText(channel, "title"));
                parseContents.put("titleString", getText(item, "ganma:magazineTitle"));
                parseContents.put("subTitle", getText(item, "ganma:storyTitle"));
                parseContents.put("authorString", getText(item, "dc:creator"));
                parseContents.put("url", getText(item, "link"));
                parseContents.put("imgUrl", getImgUrl(item, "media:thumbnail"));
                parseContents.put("updateAt", getLocalDateTime(item, "pubDate", "Asia/Tokyo")); //TZが違う
                parseContents.put("freeFlag", 1);       //全部無料にセット
            saveService.saveRss(parseContents);
        }
    }

    /** 4. GANMA用：全部無料・タグ名とTZが違う */
    public void getUpdateInfoFreeMLW(String source) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(source);
        Element channel = document.getDocumentElement();        //ルート要素取得
        NodeList items = channel.getElementsByTagName("item");  //各item取得

        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);
            Map<String, Object> parseContents = new HashMap<>();
                parseContents.put("mediaName", getText(channel, "title"));
                parseContents.put("titleString", getTextMLW1(item, "title"));   //特殊処理
                parseContents.put("subTitle", getTextMLW2(item, "title"));      //特殊処理
                parseContents.put("authorString", getTextMLW3(item, "description"));    //特殊処理
                parseContents.put("url", getText(item, "link"));
                parseContents.put("imgUrl", getTextMLW4(item, "description"));      //特殊処理(getText)
                parseContents.put("updateAt", getLocalDateTime(item, "pubDate", "Asia/Tokyo")); //TZが違う
                parseContents.put("freeFlag", 1);       //全部無料にセット
            saveService.saveRss(parseContents);
        }
    }


    /** 1-a. Strings共通処理：タグを指定してtextを取得 + Normalize + trim */
    private String getText(Element item, String tagName) {
        String rawText = item.getElementsByTagName(tagName).item(0).getTextContent();
        String textContent = Normalizer.normalize(rawText, Form.NFKC).trim();
        return textContent;
    }

    /** 1-b. ヤングエース用クレンジング：タグを指定してtextを取得 + Normalize + trim */
    private String getTextYA(Element item, String tagName) {
        String rawText = item.getElementsByTagName(tagName).item(0).getTextContent();
        String textNormalize = Normalizer.normalize(rawText, Form.NFKC);
        String textContent = textNormalize.replaceAll("(?<=\\[)(.*$)", "").replaceFirst(".$", "").trim();
        return textContent;
    }

    /** 1-c. まんがライフWIN用タイトル用クレンジング：タグを指定してtextを取得 + Normalize + trim */
    private String getTextMLW1(Element item, String tagName) {
        String textArray[] = item.getElementsByTagName(tagName).item(0).getTextContent().trim().split("】");
        String rawText = textArray[textArray.length - 1];
        String textContent =Normalizer.normalize(rawText, Form.NFKC).trim();
        return textContent;
    }

    /** 1-d. まんがライフWIN用サブタイトル用クレンジング：タグを指定してtextを取得 + Normalize + trim */
    private String getTextMLW2(Element item, String tagName) {
        String textArray[] = item.getElementsByTagName(tagName).item(0).getTextContent().trim().split("【");
        String rawText = textArray[0];
        String textContent =Normalizer.normalize(rawText, Form.NFKC).trim();
        return textContent;
    }

    /** 1-e. まんがライフWIN用著者用クレンジング：タグを指定してtextを取得 + Normalize + trim */
    private String getTextMLW3(Element item, String tagName) {
        String rawText = item.getElementsByTagName(tagName).item(0).getTextContent().trim();
        String textAuthor = rawText.split(">")[4];
        String textReplace = textAuthor.replace("作者紹介：", "").replace("<br /", "");
        String textContent =Normalizer.normalize(textReplace, Form.NFKC).trim();
        return textContent;
    }

    /** 1-f. まんがライフWIN用imgUrl用クレンジング：タグを指定してtextを取得 + Normalize + trim */
    private String getTextMLW4(Element item, String tagName) {
        String rawText = item.getElementsByTagName(tagName).item(0).getTextContent().trim();
        String textAuthor = rawText.split(">")[1];
        String textReplace = textAuthor.replace("<img src=\"", "").replace("/article_t.jpg\" width=\"136\" height=\"136\" /", "/article_l.jpg?20230118");
        String textContent =Normalizer.normalize(textReplace, Form.NFKC).trim();
        return textContent;
    }

    /** 2. imgUrlの取得 */
    private String getImgUrl(Element item, String tagName) {
        Element imgUrlElement = (Element) item.getElementsByTagName(tagName).item(0);
        String imgUrl = imgUrlElement.getAttribute("url");
        return imgUrl;
    }

    /** 3. UTC -> LocalDateTimeに変換 */
    private LocalDateTime getLocalDateTime(Element item, String tagName, String timeZone) {
        String updateString = getText(item, tagName);
        LocalDateTime updateAt = ZonedDateTime
                .parse(updateString, DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of(timeZone)))
                .withZoneSameInstant(ZoneId.of("Asia/Tokyo"))
                .toLocalDateTime();
        return updateAt;
    }

    /** 4. 無料フラグ判定 */
    private Integer getFreeFlag(Element item, String tagName) {
        Node freeFlagNode = item.getElementsByTagName(tagName).item(0);
        if (freeFlagNode != null) {
            return 1;
        } else {
            return 0;
        }
    }
}
