package com.sakeman.form;

import lombok.Data;

@Data
public class SearchbarForm {
    String inputKeyword;

    public String[] getKeywords() {
        if (inputKeyword == null) {
            return new String[] {};
        }
        return inputKeyword.split("[ 　]+"); // スペースと全角スペースで分割
    }
}
