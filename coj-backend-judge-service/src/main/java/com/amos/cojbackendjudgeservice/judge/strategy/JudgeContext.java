package com.amos.cojbackendjudgeservice.judge.strategy;

import com.amos.cojbackendmodel.codesandbox.JudgeInfo;
import com.amos.cojbackendmodel.dto.question.JudgeCase;
import com.amos.cojbackendmodel.entity.Question;
import com.amos.cojbackendmodel.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定于在策略中传递的参数）
 */
@Data
public class JudgeContext {
    JudgeInfo judgeInfo;

    private List<JudgeCase> judgeCaseList;

    private List<String> outputList;

    private Question question;

    private QuestionSubmit questionSubmit;

}
