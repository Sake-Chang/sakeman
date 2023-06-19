package com.sakeman.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Manga;
import com.sakeman.entity.ReadStatus;
import com.sakeman.entity.ReadStatus.Status;
import com.sakeman.entity.User;
import com.sakeman.repository.ReadStatusRepository;


@Service
public class ReadStatusService {

    private final ReadStatusRepository readStatusRepository;

    public ReadStatusService(ReadStatusRepository repository) {
        this.readStatusRepository = repository;
    }

    /** UserとMangaで検索 */
    public Optional<ReadStatus> findByUserAndManga(User user, Manga manga){
        return readStatusRepository.findByUserAndManga(user, manga);
    }

    /** 1件を検索して返す */
    public ReadStatus getReadStatus(Integer id) {
        return readStatusRepository.findById(id).get();
    }

    /** user_idで検索して返す */
    public List<ReadStatus> findByUserId(Integer userId) {
        return readStatusRepository.findByUserId(userId);
    }

    /** ログインユーザーの気になるしてるmangaIdのリストを作成して返す */
    public List<Integer> getWantMangaIdByUser(@AuthenticationPrincipal UserDetail userDetail) {
        List<ReadStatus> wantlist = readStatusRepository.findByUserAndStatus(userDetail.getUser(), ReadStatus.Status.気になる);
        List<Integer> wantIdList = new ArrayList<Integer>();
        wantlist.forEach(i -> wantIdList.add(i.getManga().getId()));

        return wantIdList;
    }

    /** ログインユーザーの読んだしてるmangaIdのリストを作成して返す */
    public List<Integer> getReadMangaIdByUser(@AuthenticationPrincipal UserDetail userDetail) {
        List<ReadStatus> readlist = readStatusRepository.findByUserAndStatus(userDetail.getUser(), ReadStatus.Status.読んだ);
        List<Integer> readIdList = new ArrayList<Integer>();
        readlist.forEach(i -> readIdList.add(i.getManga().getId()));

        return readIdList;
    }

    /** ユーザーとステータスで検索して返す */
    public List<ReadStatus> findByUserAndStatus(User user, Status status) {
        List<ReadStatus> readlist = readStatusRepository.findByUserAndStatus(user, status);
        return readlist;
    }

    /** 登録処理 */
    @Transactional
    public ReadStatus saveReadStatus(ReadStatus readStatus) {
        return readStatusRepository.save(readStatus);
    }

}
