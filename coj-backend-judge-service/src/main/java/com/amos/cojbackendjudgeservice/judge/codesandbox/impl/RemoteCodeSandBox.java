package com.amos.cojbackendjudgeservice.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.amos.cojbackendcommon.common.ErrorCode;
import com.amos.cojbackendcommon.exception.BusinessException;
import com.amos.cojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.amos.cojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.amos.cojbackendmodel.codesandbox.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * 远程代码沙箱（实际调用接口的沙箱）
 */
public class RemoteCodeSandBox implements CodeSandBox {

    // 定义鉴权请求头和秘钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";

    @Override
    public ExecuteCodeResponse exectueCode(ExecuteCodeRequest exceteCodeRequest) {
        System.out.println("远程代码沙箱");

        String url = "http://localhost:8090/executeCode";
        String json = JSONUtil.toJsonStr(exceteCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .body(json)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .execute()
                .body();
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "远程代码沙箱调用失败" + responseStr);
        }

        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}
