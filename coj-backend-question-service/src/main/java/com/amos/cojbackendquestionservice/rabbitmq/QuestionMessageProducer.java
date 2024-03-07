package com.amos.cojbackendquestionservice.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class QuestionMessageProducer {
    @Resource
    RabbitTemplate rabbitTemplate;

    public void sendMessage(String exchange, String routingKey, String message) {
        /**
         * 参数：
         * 1、交换机名称
         * 2、routingKey
         * 3、消息内容
         */
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
