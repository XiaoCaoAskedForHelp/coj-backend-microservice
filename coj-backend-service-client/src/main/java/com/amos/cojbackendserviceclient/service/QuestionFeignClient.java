package com.amos.cojbackendserviceclient.service;

import com.amos.cojbackendmodel.entity.Question;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
* @author amosc
* @description 针对表【question(题目)】的数据库操作Service
* @createDate 2024-02-14 17:46:13
*/
@FeignClient(value = "coj-backend-question-service",path = "/api/question/inner",contextId = "question")
public interface QuestionFeignClient {

    // questionService.getById

    @GetMapping("/get/id")
    Question getQuestionById(@RequestParam("questionId") Long questionId);
}
