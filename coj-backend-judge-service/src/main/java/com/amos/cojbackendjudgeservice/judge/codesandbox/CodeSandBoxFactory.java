package com.amos.cojbackendjudgeservice.judge.codesandbox;


import com.amos.cojbackendjudgeservice.judge.codesandbox.impl.ExampleCodeSandBox;
import com.amos.cojbackendjudgeservice.judge.codesandbox.impl.RemoteCodeSandBox;
import com.amos.cojbackendjudgeservice.judge.codesandbox.impl.ThridPartyCodeSandBox;

/**
 * 代码沙箱工厂（根据字符串参数创建指定的代码沙箱实例）
 */
public class CodeSandBoxFactory {
    public static CodeSandBox getCodeSandBox(String type) {
        switch (type) {
            case "remote":
                return new RemoteCodeSandBox();
            case "thirdParty":
                return new ThridPartyCodeSandBox();
            case "example":
                return new ExampleCodeSandBox();
            default:
                return new ExampleCodeSandBox();
        }
    }
}
