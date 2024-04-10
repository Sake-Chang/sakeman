package com.sakeman.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageValidationService {

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String ALLOWED_FORMAT_REGEX = "\\.(jpg|jpeg|png|gif)$";

    public boolean isValidFormat(MultipartFile file, BindingResult res) {
        boolean ans = true;

        String originalName = file.getOriginalFilename().toLowerCase();
        String ext = originalName.substring(originalName.lastIndexOf("."));

        if (!ext.matches(ALLOWED_FORMAT_REGEX)) {
            FieldError fieldError = new FieldError(res.getObjectName(), "fileContents", "ファイル形式は、jpg・png・gifのどれかでお願いします");
            res.addError(fieldError);
            ans = false;
        }
        return ans;
    }

    public boolean isValidSize(MultipartFile file, BindingResult res) {
        boolean ans = true;

        if (file.getSize() > MAX_SIZE) {
            FieldError fieldError = new FieldError(res.getObjectName(), "fileContents", "ファイルが大きすぎます(最大5MB)");
            res.addError(fieldError);
            ans = false;
        }
        return ans;
    }

}
