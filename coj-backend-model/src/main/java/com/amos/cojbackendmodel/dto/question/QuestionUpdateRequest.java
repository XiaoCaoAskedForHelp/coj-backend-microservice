package com.amos.cojbackendmodel.dto.question;

import com.amos.cojbackendmodel.dto.questionsubmit.JudgeConfig;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新请求(管理员)
 */
@Data
public class QuestionUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 答案
     */
    private String answer;

    /**
     * 判题用例（json 数组）
     */
    private List<JudgeCase> judgeCase;

    /**
     * 判题配置（json 对象）
     */
    private JudgeConfig judgeConfig;

    private static final long serialVersionUID = 1L;
}