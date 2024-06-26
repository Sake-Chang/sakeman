package com.sakeman.controller.admin;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sakeman.service.S3Service;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/s3")
@Slf4j
public class AdminSS3Controller {
    @Autowired
    private S3Service s3Service;

    /** ファイルリストの表示 */
    @GetMapping("/list")
    public String getFileList(@ModelAttribute("fileName") String fileName, Model model) {
        List<Resource> resourceList = s3Service.fileList(fileName);
        model.addAttribute("fileList", resourceList);
        return "s3/fileList";
    }

    /** ファイルリストの検索 */
    @PostMapping("/search")
    public String searchFileList(@RequestParam("fileName") String fileName, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("fileName", fileName);
        return "redirect:/s3/list";
    }

//    /** ファイルアップロード */
//    @PostMapping("/upload")
//    public String uploadFile(@RequestParam("fileContents") MultipartFile fileContents) {
//        s3Service.upload(fileContents);
//        return "redirect:/s3/list";
//    }

    /** ファイルダウンロード */
    @GetMapping("/download/{fileName}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileName") String fileName) {
        ByteArrayResource resource = null;
        try (InputStream is = s3Service.download(fileName)) {
            resource = new ByteArrayResource(is.readAllBytes());
        } catch (IOException e) {
            log.error("FileDownloadError", e);
            return ResponseEntity.internalServerError().body(null);
        }
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION,  "attachment; filename=" + fileName);
        return ResponseEntity
                .ok()
                .headers(header)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }
}
