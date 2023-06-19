package com.sakeman.service;

/** 使ってない */

//import java.util.List;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.techacademy.entity.UserManga;
//import com.techacademy.repository.UserMangaRepository;
//
//@Service
//public class UserMangaService {
//    private final UserMangaRepository userMangaRepository;
//
//    public UserMangaService(UserMangaRepository repository) {
//        this.userMangaRepository = repository;
//    }
//
//    /** 全件を検索して返す **/
//    public List<UserManga> getUserMangaList() {
//        return userMangaRepository.findAll();
//    }
//
//    
//    /** 1件を検索して返す（いらない） */
////    public UserManga getUserManga(Integer id) {
////        return userMangaRepository.findById(id).get();
////    }
//
//    /** user_idで検索して返す */
//    public List<UserManga> findByUserId(Integer userId) {
//        return userMangaRepository.findByUserId(userId);
//    }
//
//    /** 登録処理(1件) */
////    @Transactional
//    public UserManga saveUserManga(UserManga userManga) {
//        return userMangaRepository.save(userManga);
//    }
//
//    /** 登録処理(複数件) */
////  @Transactional
//  public List<UserManga> saveUserMangaAll(List<UserManga> userMangas) {
//      return userMangaRepository.saveAll(userMangas);
//  }
//
//}
