package com.sakeman.global;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.sakeman.form.SearchbarForm;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("searchbarForm")
    public SearchbarForm searchForm() {
        return new SearchbarForm(); // 新しいフォームオブジェクトを作成して返す
    }

}
