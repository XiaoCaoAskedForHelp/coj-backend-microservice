package com.amos.cojbackendjudgeservice.judge.strategy;


import com.amos.cojbackendmodel.codesandbox.JudgeInfo;

/**
 * 判题策略
 */
public interface JudgeStrategy {
    JudgeInfo doJudgeInfo(JudgeContext judgeContext);
}
