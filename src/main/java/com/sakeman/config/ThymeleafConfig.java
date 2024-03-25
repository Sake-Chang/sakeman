package com.sakeman.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sakeman.repository.ReadStatusRepository;
import com.sakeman.thymeleaf.CustomEncodeDialect;
import com.sakeman.thymeleaf.CustomGetMangaDialect;
import com.sakeman.thymeleaf.CustomToSetMediaDialect;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ThymeleafConfig {

    private final ReadStatusRepository rsRepository;

    @Bean
    public CustomEncodeDialect customEncodeDialect() {
        return new CustomEncodeDialect();
    }

    @Bean
    public CustomGetMangaDialect customGetMangaDialect() {

        return new CustomGetMangaDialect(rsRepository);
    }

    @Bean
    public CustomToSetMediaDialect customToSetMediaaDialect() {

        return new CustomToSetMediaDialect();
    }
}
