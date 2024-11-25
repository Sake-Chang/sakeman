package com.sakeman.controller.rest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sakeman.entity.Manga;
import com.sakeman.entity.ReadStatus;
import com.sakeman.entity.User;
import com.sakeman.service.MangaService;
import com.sakeman.service.ReadStatusService;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserService;


@RestController
@RequestMapping
public class ReadStatusRestController {

    private ReadStatusService readStatusService;
    private UserService userService;
    private MangaService mangaService;

    public ReadStatusRestController(ReadStatusService readStatusService, UserService userService, MangaService mangaService) {
        this.readStatusService = readStatusService;
        this.userService = userService;
        this.mangaService = mangaService;

    }

    @PutMapping("/readstatus")
    @ResponseBody
    public int createReadStatus(@AuthenticationPrincipal UserDetail userDetail, @RequestBody Manga manga, Model model) {
        // UserIdとMangaIdを取得
        Integer userId = userDetail.getUser().getId();
        Integer mangaId = manga.getId();
        // 上記Idで対象のUserとMangaを取得
        User objuser = userService.getUser(userId);
        Manga objmanga = mangaService.getManga(mangaId);
        // 対象UserとMangaの組み合わせでReadStatusがあるか確認
        Optional<ReadStatus> OptionRS = readStatusService.findByUserAndManga(objuser, objmanga);
        // あった場合はReadStatusのIdを取得（id）

        if (OptionRS.isEmpty()) {
            ReadStatus newrs = new ReadStatus();
            newrs.setStatus(ReadStatus.Status.気になる);
            newrs.setUser(objuser);
            newrs.setManga(objmanga);
            readStatusService.saveReadStatus(newrs);
        } else {
            Integer id = OptionRS.get().getId();
            // そのidでReadStatusを取得（rs）
            ReadStatus rs = readStatusService.getReadStatus(id);
            switch (OptionRS.get().getStatus()) {
                case 未登録:
                    rs.setStatus(ReadStatus.Status.気になる);
                    readStatusService.saveReadStatus(rs);
                    break;
                case 気になる:
                    rs.setStatus(ReadStatus.Status.未登録);
                    readStatusService.saveReadStatus(rs);
                    break;
                default:
                    break;
            }
        }

        return 0;
    }

    //* 本棚から */
    @PutMapping("/readstatus2")
    @ResponseBody
    public int createReadStatus(@AuthenticationPrincipal UserDetail userDetail, @RequestBody ReadStatus readStatus, Model model) {
        // IdからreadStatusを取得
//        ReadStatus rs = readStatusService.getReadStatus(readStatus.getId());

        User objuser = userDetail.getUser();
//        Manga objmanga = rs.getManga();
        Manga objmanga = mangaService.getManga(readStatus.getId());
        Optional<ReadStatus> OptionRS = readStatusService.findByUserAndManga(objuser, objmanga);

        if (OptionRS.isEmpty()) {
            ReadStatus newrs = new ReadStatus();
            newrs.setStatus(readStatus.getStatus());
            newrs.setUser(objuser);
            newrs.setManga(objmanga);
            readStatusService.saveReadStatus(newrs);
        } else {
            Integer id = OptionRS.get().getId();
            // そのidでReadStatusを取得（rs）
            ReadStatus existrs = readStatusService.getReadStatus(id);
            existrs.setStatus(readStatus.getStatus());
            existrs.setUser(objuser);
            existrs.setManga(objmanga);
            readStatusService.saveReadStatus(existrs);
        }

        return 0;
    }

}
