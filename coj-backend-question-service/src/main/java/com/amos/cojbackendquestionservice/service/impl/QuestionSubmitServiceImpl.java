package com.amos.cojbackendquestionservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.amos.cojbackendcommon.common.ErrorCode;
import com.amos.cojbackendcommon.constant.CommonConstant;
import com.amos.cojbackendcommon.exception.BusinessException;
import com.amos.cojbackendcommon.utils.SqlUtils;
import com.amos.cojbackendmodel.dto.questionsubmit.QuestionSubmitAddRequest;
import com.amos.cojbackendmodel.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.amos.cojbackendmodel.entity.Question;
import com.amos.cojbackendmodel.entity.QuestionSubmit;
import com.amos.cojbackendmodel.entity.User;
import com.amos.cojbackendmodel.enums.QuestionSubmitLanguageEnum;
import com.amos.cojbackendmodel.enums.QuestionSubmitStatusEnum;
import com.amos.cojbackendmodel.vo.QuestionSubmitVO;
import com.amos.cojbackendmodel.vo.UserVO;
import com.amos.cojbackendquestionservice.config.RabbitmqConfig;
import com.amos.cojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.amos.cojbackendquestionservice.rabbitmq.QuestionMessageProducer;
import com.amos.cojbackendquestionservice.service.QuestionService;
import com.amos.cojbackendquestionservice.service.QuestionSubmitService;
import com.amos.cojbackendserviceclient.service.JudgeFeignClient;
import com.amos.cojbackendserviceclient.service.UserFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author amosc
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2024-02-14 17:47:15
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    @Lazy
    private JudgeFeignClient judgeFeignClient;

    @Resource
    private QuestionMessageProducer questionMessageProducer;

    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 校验编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言不合法");
        }
        Long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已题目提交
        long userId = loginUser.getId();
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage(language);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setJudgeInfo("{}");
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setUserId(userId);
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        Long questionSubmitId = questionSubmit.getId();
        // 发送消息到队列
        questionMessageProducer.sendMessage(RabbitmqConfig.COJ_DIRECT_EXCHANGE, RabbitmqConfig.ROUTINGKEY_COJ, String.valueOf(questionSubmitId));
        // 异步执行判题
//        CompletableFuture.runAsync(() -> {
//             judgeFeignClient.doJudge(questionSubmitId);
//        });
        return questionSubmit.getId();
    }

    /**
     * 获取查询包装类
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();
        // 拼接查询条件
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, HttpServletRequest request) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 1. 关联查询用户信息
        Long userId = questionSubmit.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userFeignClient.getById(userId);
        }
        UserVO userVO = userFeignClient.getUserVO(user);
        questionSubmitVO.setUserVO(userVO);

        // 脱敏，仅自己和管理员能够查询提交的代码
        User loginUser = userFeignClient.getLoginUser(request);
        if (loginUser == null || !loginUser.getId().equals(userId) && !userFeignClient.isAdmin(user)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, HttpServletRequest request) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollUtil.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionSubmitList.stream().map(QuestionSubmit::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userFeignClient.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(questionSubmit -> {
            QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
            Long userId = questionSubmit.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionSubmitVO.setUserVO(userFeignClient.getUserVO(user));
            return questionSubmitVO;
        }).collect(Collectors.toList());

        // 脱敏，仅自己和管理员能够查询提交的代码
        User loginUser = userFeignClient.getLoginUser(request);
        if (loginUser != null && !userFeignClient.isAdmin(loginUser)) {
            questionSubmitVOList.forEach(questionSubmitVO -> {
                if (!loginUser.getId().equals(questionSubmitVO.getUserId())) {
                    questionSubmitVO.setCode(null);
                }
            });
        }

        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

}




