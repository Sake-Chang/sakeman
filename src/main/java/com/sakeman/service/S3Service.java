package com.sakeman.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.sakeman.entity.s3.S3Info;

import io.awspring.cloud.core.io.s3.PathMatchingSimpleStorageResourcePatternResolver;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class S3Service {

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private ResourcePatternResolver resourcePatternResolver;
    @Autowired
    private S3Info s3Info;

    @Autowired
    public void setupResolver(ApplicationContext applicationContext, AmazonS3 amazonS3) {
        this.resourcePatternResolver = new PathMatchingSimpleStorageResourcePatternResolver(amazonS3, applicationContext);
    }

    /** ファイルリスト取得 */
    public List<Resource> fileList(String fileName) {
        if (fileName == null) {
            fileName = "";
        }
        List<Resource> resourceList = null;
        try {
            Resource[] resourceArray = resourcePatternResolver
                                        .getResources("s3://sakeman-s3-img/**/*" + fileName + "*.*");
            resourceList = Arrays.asList(resourceArray);
        } catch (IOException e) {
            log.error("S3FileListError", e);
        }
        return resourceList;
    }


    /** ファイルアップロード */
    public void upload(MultipartFile fileContents, String newFileName) {
        Resource resource = this.resourceLoader
                                .getResource("s3://" + s3Info.getBucketName() + "/" + newFileName);
        WritableResource writableResource = (WritableResource)resource;

        //try (InputStream inputStream = fileContents.getInputStream();
        try (
             OutputStream outputStream = writableResource.getOutputStream()) {
            outputStream.write(fileContents.getBytes());
            //IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            log.error("S3FileUploadError", e);
        }
    }


    /** ファイルダウンロード */
    public InputStream download(String fileName) throws IOException {
        Resource resource = this.resourceLoader
                                .getResource("s3://sakeman-user-upload/" + fileName);
        return resource.getInputStream();
    }


}
