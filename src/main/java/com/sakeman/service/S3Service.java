package com.sakeman.service;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.sakeman.entity.s3.S3Info;

import io.awspring.cloud.core.io.s3.PathMatchingSimpleStorageResourcePatternResolver;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class S3Service {

    @Autowired
    private ResourceLoader resourceLoder;
    @Autowired
    private ResourcePatternResolver resourcePatternResolver;
    @Autowired
    private S3Info s3Info;

    @Autowired
    public void setupResolver(ApplicationContext applicationContext, AmazonS3 amazonS3) {
        this.resourcePatternResolver = new PathMatchingSimpleStorageResourcePatternResolver(amazonS3, applicationContext);
    }

    /** ファイルアップロード */
    public void upload (MultipartFile fileContents) {
        Resource resource = this.resourceLoder
                                //.getResource("https://sake-man.com/" + fileContents.getOriginalFilename());
                                .getResource("s3://" + s3Info.getBucketName() + "/" + fileContents.getOriginalFilename());
        WritableResource writableResource = (WritableResource)resource;

        try (OutputStream outputStream = writableResource.getOutputStream()) {
            outputStream.write(fileContents.getBytes());
        } catch (IOException e) {
            log.error("S3FileUploadError", e);
        }
    }

}
