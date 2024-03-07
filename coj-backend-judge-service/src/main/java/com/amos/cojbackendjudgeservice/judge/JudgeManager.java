package com.amos.cojbackendjudgeservice.judge;

import com.amos.cojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.amos.cojbackendjudgeservice.judge.strategy.JavaLanguageJudgeStrategy;
import com.amos.cojbackendjudgeservice.judge.strategy.JudgeContext;
import com.amos.cojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.amos.cojbackendmodel.codesandbox.JudgeInfo;
import com.amos.cojbackendmodel.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理简化调用
 */
@Service
public class JudgeManager {

    /**
     * 判题（算是一种将策略选择抽离的方式）
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudgeInfo(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudgeInfo(judgeContext);
    }
}
