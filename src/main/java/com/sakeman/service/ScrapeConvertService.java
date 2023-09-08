package com.sakeman.service;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class ScrapeConvertService {

    public String getText(Element doc, String xpath) {
        String textRaw = doc.selectXpath(xpath).text();
        String text = Normalizer.normalize(textRaw, Form.NFKC).trim();
        return text;
    }

    public String getUrl(Element doc, String xpath) {
        String url = doc.selectXpath(xpath).attr("href");
        return url;
    }

    public String getUrl(Element doc, String xpath, String rootUrl) {
        String urlRaw = doc.selectXpath(xpath).attr("href");
        String url = rootUrl + urlRaw;
        return url;
    }

    public String getImgUrl(Element doc, String xpath) {
        String imgUrl = doc.selectXpath(xpath).attr("src");
        return imgUrl;
    }

    public LocalDateTime getUpdate(String updateRaw) {
        LocalDateTime update;
        if (updateRaw.contains("日前")) {
            String updateReplace = updateRaw.replace("最新話", "").replace("日前", "");
            Integer updateReplaceInteger = Integer.parseInt(updateReplace);
            LocalDateTime now = LocalDateTime.now();
            int year = now.getYear();
            int month = now.getMonthValue();
            int day = now.getDayOfMonth() - updateReplaceInteger;
            String updateMonth = (String)String.format("%02d", month);
            String updateDay = (String)String.format("%02d", day);
            String updateString = year + "/" + updateMonth + "/" +updateDay;
            update = LocalDateTime.of(LocalDate.parse(updateString, DateTimeFormatter.ofPattern("yyyy/MM/dd")), LocalTime.of(0,0));
        } else {
            String updateReplace = updateRaw.replace("最新話", "");
            String[] updateSplit = updateReplace.split("/");
            int month = Integer.parseInt(updateSplit[1]);
            int day = Integer.parseInt(updateSplit[2]);
            String updateMonth = (String)String.format("%02d", month);
            String updateDay = (String)String.format("%02d", day);
            String updateString = updateSplit[0] + "/" + updateMonth + "/" +updateDay;
            update = LocalDateTime.of(LocalDate.parse(updateString, DateTimeFormatter.ofPattern("yyyy/MM/dd")), LocalTime.of(0,0));
        }
        return update;
    }

}
