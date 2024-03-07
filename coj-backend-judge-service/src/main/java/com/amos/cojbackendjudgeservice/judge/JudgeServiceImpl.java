package com.amos.cojbackendjudgeservice.judge;

import cn.hutool.json.JSONUtil;
import com.amos.cojbackendcommon.common.ErrorCode;
import com.amos.cojbackendcommon.exception.BusinessException;
import com.amos.cojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.amos.cojbackendjudgeservice.judge.codesandbox.CodeSandBoxFactory;
import com.amos.cojbackendjudgeservice.judge.codesandbox.CodeSandBoxProxy;
import com.amos.cojbackendjudgeservice.judge.strategy.JudgeContext;
import com.amos.cojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.amos.cojbackendmodel.codesandbox.ExecuteCodeResponse;
import com.amos.cojbackendmodel.codesandbox.JudgeInfo;
import com.amos.cojbackendmodel.dto.question.JudgeCase;
import com.amos.cojbackendmodel.entity.Question;
import com.amos.cojbackendmodel.entity.QuestionSubmit;
import com.amos.cojbackendmodel.enums.QuestionSubmitStatusEnum;
import com.amos.cojbackendserviceclient.service.QuestionFeignClient;
import com.amos.cojbackendserviceclient.service.QuestionSubmitFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionSubmitFeignClient questionSubmitFeignClient;

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        //1)传入题目的提交d,获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionSubmitFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目信息不存在");
        }
        // 如果不为等待状态，不进行判题
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中，请稍后再试");
        }
        // 更新状态为判题中
        questionSubmit.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitFeignClient.updateQuestionSubmitById(questionSubmit);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新题目提交状态失败");
        }
        //调用沙箱，获取到执行结果
        CodeSandBox codeSandBox = CodeSandBoxFactory.getCodeSandBox(type);
        codeSandBox = new CodeSandBoxProxy(codeSandBox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.exectueCode(executeCodeRequest);
        if (executeCodeResponse == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码沙箱执行失败");
        }
        //根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setOutputList(executeCodeResponse.getOutputList());
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);

        // 选择判题策略
        JudgeInfo judgeInfo = judgeManager.doJudgeInfo(judgeContext);
        // 修改数据库中的判题结果
        questionSubmit.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitFeignClient.updateQuestionSubmitById(questionSubmit);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新题目提交状态失败");
        }
        // todo 不理解为什么还要查询一次
        QuestionSubmit questionSubmitResult = questionSubmitFeignClient.getQuestionSubmitById(questionSubmitId);
        System.out.println("题目提交结果：" + questionSubmitResult);
        return questionSubmitResult;
    }
}
