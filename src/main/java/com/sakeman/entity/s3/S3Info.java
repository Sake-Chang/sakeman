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
//    @Value("${bucket.name.upload}")
//    @Value("sakeman-user-upload")
    @Value("sakeman-s3-img/sakeman-user-upload")
    private String bucketName;

}
