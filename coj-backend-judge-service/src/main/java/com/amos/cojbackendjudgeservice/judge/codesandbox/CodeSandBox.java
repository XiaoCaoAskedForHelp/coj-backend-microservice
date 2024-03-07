package com.amos.cojbackendjudgeservice.judge.codesandbox;

import com.amos.cojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.amos.cojbackendmodel.codesandbox.ExecuteCodeResponse;

/**
 * 代码沙箱
 */
public interface CodeSandBox {
    /**
     * 执行代码
     *
     * @param exceteCodeRequest
     * @return
     */
    ExecuteCodeResponse exectueCode(ExecuteCodeRequest exceteCodeRequest);
}
