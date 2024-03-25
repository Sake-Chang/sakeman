package com.sakeman.thymeleaf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sakeman.entity.ReadStatus;
import com.sakeman.repository.ReadStatusRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetReadManga {

    private final ReadStatusRepository rsRepository;

    public List<ReadStatus> getReadManga(Integer userId) {
        List<ReadStatus> readlist = rsRepository.findByUserIdAndStatus(userId, ReadStatus.Status.読んだ);
        Collections.shuffle(readlist);
        List<ReadStatus> list10 = new ArrayList<>();
        if (readlist.size() < 10) {
            list10 = readlist.subList(0,readlist.size());
        } else {
            list10 = readlist.subList(0,10);
        }
        return list10;
    }
}
