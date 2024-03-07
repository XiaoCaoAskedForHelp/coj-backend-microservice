package com.amos.cojbackendjudgeservice.rabbitmq;

import com.amos.cojbackendjudgeservice.config.RabbitmqConfig;
import com.amos.cojbackendjudgeservice.judge.JudgeService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

// 使用@RabbitListener注解的方法可以接收多种类型的参数，常见的包括：
//消息体：方法可以直接接收消息的内容。Spring会根据配置的消息转换器（MessageConverter）将消息体转换成适当的类型。例如，如果消息是JSON格式，可以将其自动转换为Java对象。
//Message：如果方法参数类型为org.springframework.amqp.core.Message，则可以接收到整个消息的详情，包括消息体、消息属性（headers）等。
//Channel：当ackMode设置为MANUAL时，通常需要com.rabbitmq.client.Channel参数来调用basicAck、basicNack或basicReject方法，以进行消息的手动确认。
//@Payload：用于标记参数是消息的负载（body）。与@Headers或@Header注解结合使用，可以同时接收消息内容和特定的消息头信息。
//@Headers 或 @Header：用于接收消息的全部头信息（一个Map）或特定的头信息。
//其他自定义参数：基于配置的消息转换器，还可以接收其他自定义类型的参数。
@Component
@Slf4j
public class QuestionMessageConsumer {

    @Resource
    private JudgeService judgeService;

    @SneakyThrows
    @RabbitListener(queues = {RabbitmqConfig.COJ_QUEUE}, ackMode = "MANUAL")
    public void receive_email(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receive message is: {}", message);
//        System.out.println("message: " + message);
//        System.out.println("channel: " + channel);
        if (message == null) {
            return;
        }
        long questionSubmitId = Long.parseLong(message);
        try {
            judgeService.doJudge(questionSubmitId);
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            // 执行失败
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
