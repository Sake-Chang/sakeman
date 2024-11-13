package com.sakeman.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sakeman.entity.UserWebMangaSetting;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserWebMangaSettingService {

    public boolean isDefaultSetting(UserWebMangaSetting setting) {
        return  setting.getFreeflagSetting() == 0 &&
                setting.getFollowflagSetting() == 0 &&
                setting.getOneshotflagSetting() == 0 &&
                (setting.getGenreSettings() == null || setting.getGenreSettings().isEmpty());
    }

    public List<Integer> getGenreIdsAll(UserWebMangaSetting setting) {
        if (setting == null || setting.getGenreSettings() == null) {
            return Collections.singletonList(0);
        }
        return setting.getGenreSettings().stream()
                .map(settingGenre -> settingGenre.getGenre().getId())  // Genre IDを取得
                .collect(Collectors.toList());
    }

    public List<Integer> getGenreIdsExist(UserWebMangaSetting setting) {
        if (setting == null || setting.getGenreSettings() == null) {
            return Collections.singletonList(0);
        }
        return setting.getGenreSettings().stream()
                .filter(settingGenre -> settingGenre.isDeleteFlag() == false)  // 削除されていないもののみ
                .map(settingGenre -> settingGenre.getGenre().getId())  // Genre IDを取得
                .collect(Collectors.toList());
    }

    public List<Integer> getFreeflagNums(UserWebMangaSetting setting) {
        return setting.getFreeflagSetting() == 0 ? List.of(0, 1, 2) : List.of(1);
    }

}
