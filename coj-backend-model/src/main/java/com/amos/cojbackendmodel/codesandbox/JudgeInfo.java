package com.amos.cojbackendmodel.codesandbox;

import lombok.Data;

/**
 * 判题信息
 */
@Data
public class JudgeInfo {
    /**
     * 程序执行信息
     */
    private String message;
    /**
     * 内存消耗（kb）
     */
    private Long memory;
    /**
     * 消耗时间（ms）
     */
    private Long time;
}
