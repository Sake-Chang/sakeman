package com.sakeman.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.User;
import com.sakeman.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository repository) {
        this.userRepository = repository;
    }

    /** 全件を検索して返す **/
    public List<User> getUserList() {
        return userRepository.findAll();
    }

    /** 1件を検索して返す */
    public User getUser(Integer id) {
        return userRepository.findById(id);
    }

    /** 登録処理 */
    @Transactional
    public User saveUser (User user) {
        return userRepository.save(user);
    }

}
