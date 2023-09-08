package com.sakeman.entity.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.ToString;

@Component
@Getter
@ToString
public class S3Info {
//    @Value("${bucket.name}")
    @Value("sakeman-s3-img")
    private String bucketName;

}
