package com.sakeman.controller.rss;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sakeman.entity.WebMangaTitleConverter;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.service.MangaService;
import com.sakeman.service.RssParseService;
import com.sakeman.service.WebMangaMediaService;
import com.sakeman.service.WebMangaTitleConverterService;
import com.sakeman.service.WebMangaUpdateInfoService;

@Controller
@RequestMapping("rssparse")
public class RssParseController {
    private final RssParseService rssParseService;
    public RssParseController (RssParseService rssParseService) {
        this.rssParseService = rssParseService;
    }

    @PostMapping("/getupdateinfo")
    public String getUpdateInfo(@RequestParam String source, RedirectAttributes attrs) throws ParserConfigurationException, SAXException, IOException {
        rssParseService.getUpdateInfo(source);
        attrs.addFlashAttribute("success", "更新情報が登録されました！");
        return "redirect:/admin/index";
    }

    /** 全部無料 */
    @PostMapping("/getupdateinfo-free")
    public String getUpdateInfoFree(@RequestParam String source, RedirectAttributes attrs) throws ParserConfigurationException, SAXException, IOException {
        rssParseService.getUpdateInfoFree(source);
        attrs.addFlashAttribute("success", "更新情報が登録されました！");
        return "redirect:/admin/index";
    }

    /** ヤングエースUP */
    @PostMapping("/getupdateinfo-YA")
    public String getUpdateInfofreeYA(@RequestParam String source, RedirectAttributes attrs) throws ParserConfigurationException, SAXException, IOException {
        rssParseService.getUpdateInfoFreeYA(source);
        attrs.addFlashAttribute("success", "更新情報が登録されました！");
        return "redirect:/admin/index";
    }

    /** GANMA */
    @PostMapping("/getupdateinfo-GM")
    public String getUpdateInfo2(@RequestParam String source, RedirectAttributes attrs) throws ParserConfigurationException, SAXException, IOException {
        rssParseService.getUpdateInfoFreeGM(source);
        attrs.addFlashAttribute("success", "更新情報が登録されました！");
        return "redirect:/admin/index";
    }

    /** まんがライフWIN */
    @PostMapping("/getupdateinfo-MLW")
    public String getUpdateInfo3(@RequestParam String source, RedirectAttributes attrs) throws ParserConfigurationException, SAXException, IOException {
        rssParseService.getUpdateInfoFreeMLW(source);
        attrs.addFlashAttribute("success", "更新情報が登録されました！");
        return "redirect:/admin/index";
    }

    /** 任意のURL */
    @PostMapping("/getupdateinfo-individual")
    public String getUpdateInfoIndividual(@RequestParam String source, @RequestParam String media, RedirectAttributes attrs) throws ParserConfigurationException, SAXException, IOException {
        rssParseService.getUpdateInfo(source, media);
        attrs.addFlashAttribute("success", "更新情報が登録されました！");
        return "redirect:/admin/index";
    }

}

