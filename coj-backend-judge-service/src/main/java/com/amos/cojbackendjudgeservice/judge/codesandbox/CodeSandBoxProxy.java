package com.amos.cojbackendjudgeservice.judge.codesandbox;

import com.amos.cojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.amos.cojbackendmodel.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CodeSandBoxProxy implements CodeSandBox {

    private CodeSandBox codeSandBox;

    public CodeSandBoxProxy(CodeSandBox codeSandBox) {
        this.codeSandBox = codeSandBox;
    }

    @Override
    public ExecuteCodeResponse exectueCode(ExecuteCodeRequest exceteCodeRequest) {
        log.info("代码沙箱的请求信息：{}", exceteCodeRequest);
        ExecuteCodeResponse executeCodeResponse = codeSandBox.exectueCode(exceteCodeRequest);
        log.info("代码沙箱的响应信息：{}", executeCodeResponse);
        return executeCodeResponse;
    }
}
