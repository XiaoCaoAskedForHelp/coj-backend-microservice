package com.amos.cojbackendjudgeservice.judge.codesandbox.impl;

import com.amos.cojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.amos.cojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.amos.cojbackendmodel.codesandbox.ExecuteCodeResponse;

/**
 * 第三方代码沙箱（调用网上现成的代码沙箱）
 */
public class ThridPartyCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse exectueCode(ExecuteCodeRequest exceteCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
