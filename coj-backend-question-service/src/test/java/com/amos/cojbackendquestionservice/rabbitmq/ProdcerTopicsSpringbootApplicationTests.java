package com.amos.cojbackendquestionservice.rabbitmq;

import com.amos.cojbackendquestionservice.config.RabbitmqConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ProdcerTopicsSpringbootApplicationTests {
    @Resource
    QuestionMessageProducer questionMessageProducer;

    @Test
    public void Producer_topics_springbootTest() {

        //使用rabbitTemplate发送消息
        String message = "send email message to user";
        /**
         * 参数：
         * 1、交换机名称
         * 2、routingKey
         * 3、消息内容
         */
        questionMessageProducer.sendMessage(RabbitmqConfig.COJ_DIRECT_EXCHANGE, RabbitmqConfig.ROUTINGKEY_COJ, message);

    }


}