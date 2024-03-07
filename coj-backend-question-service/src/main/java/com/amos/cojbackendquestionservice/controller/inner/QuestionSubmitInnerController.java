package com.amos.cojbackendquestionservice.controller.inner;

import com.amos.cojbackendmodel.entity.QuestionSubmit;
import com.amos.cojbackendquestionservice.service.QuestionSubmitService;
import com.amos.cojbackendserviceclient.service.QuestionSubmitFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/inner")
public class QuestionSubmitInnerController implements QuestionSubmitFeignClient {
    @Resource
    private QuestionSubmitService questionSubmitService;

    @Override
    @GetMapping("/submit/get/id")
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") Long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }

    @Override
    @PostMapping("/submit/update")
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }
}
