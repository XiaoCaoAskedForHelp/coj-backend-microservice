package com.amos.cojbackendjudgeservice.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.amos.cojbackendmodel.codesandbox.JudgeInfo;
import com.amos.cojbackendmodel.dto.question.JudgeCase;
import com.amos.cojbackendmodel.dto.questionsubmit.JudgeConfig;
import com.amos.cojbackendmodel.entity.Question;
import com.amos.cojbackendmodel.enums.JudgeInfoMessageEnum;

import java.util.List;
import java.util.Optional;

/**
 * Java程序的判题策略
 */
public class JavaLanguageJudgeStrategy implements JudgeStrategy {
    @Override
    public JudgeInfo doJudgeInfo(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        Long memory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L);
        Long time = Optional.ofNullable(judgeInfo.getTime()).orElse(0L);
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();

        JudgeInfo result = new JudgeInfo();
        result.setMemory(memory);
        result.setTime(time);

        //1.先判断沙箱执行的结果输出的数量是否和预期输出数量相等
        //2.依次判断每一项输出和预期输出是否相等
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        if (outputList.size() != judgeCaseList.size()) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            result.setMessage(judgeInfoMessageEnum.getValue());
            return result;
        }
        for (int i = 0; i < outputList.size(); i++) {
            String output = outputList.get(i);
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (!output.equals(judgeCase.getOutput())) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                result.setMessage(judgeInfoMessageEnum.getValue());
                return result;
            }
        }
        // 判断题目限制
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        long needTimeLimit = judgeConfig.getTimeLimit();
        long needMemoryLimit = judgeConfig.getMemoryLimit();
        if (memory > needMemoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            result.setMessage(judgeInfoMessageEnum.getValue());
            return result;
        }
        // Java程序本身需要额外执行10s
        long JAVA_EXTRA_TIME = 10000L;
        if (time - JAVA_EXTRA_TIME > needTimeLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            result.setMessage(judgeInfoMessageEnum.getValue());
            return result;
        }
        result.setMessage(judgeInfoMessageEnum.getValue());
        return result;
    }
}
