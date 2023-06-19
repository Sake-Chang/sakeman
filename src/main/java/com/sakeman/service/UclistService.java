package com.sakeman.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Uclist;
import com.sakeman.repository.UclistRepository;


@Service
@Transactional
public class UclistService {
    private final UclistRepository uclistRepository;

    public UclistService(UclistRepository repository) {
        this.uclistRepository = repository;
    }

    /** 全件を検索して返す **/
    public List<Uclist> getUclistList() {
        return uclistRepository.findAll();
    }

    /** ページネーション */
    public Page<Uclist> getUclistListPageable(Pageable pageable){
        return uclistRepository.findAll(pageable);
    }

    /** 1件を検索して返す */
    public Uclist getManga(Integer id) {
        return uclistRepository.findById(id).get();
    }

    /** 登録処理 */
    @Transactional
    public Uclist saveUclist (Uclist uclist) {
        return uclistRepository.save(uclist);
    }

}
