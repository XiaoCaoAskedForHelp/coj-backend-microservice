package com.amos.cojbackendserviceclient.service;

import com.amos.cojbackendmodel.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author amosc
 * @description 针对表【question_submit(题目提交)】的数据库操作Service
 * @createDate 2024-02-14 17:47:15
 */
@FeignClient(value = "coj-backend-question-service", path = "/api/question/inner", contextId = "questionSubmit")
public interface QuestionSubmitFeignClient {
    //questionSubmitService.getById
    //questionSubmitService.updateById

    @GetMapping("/submit/get/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") Long questionSubmitId);

    @PostMapping("/submit/update")
    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit);
}
