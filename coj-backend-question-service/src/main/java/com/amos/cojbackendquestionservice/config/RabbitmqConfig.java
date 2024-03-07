package com.amos.cojbackendquestionservice.config;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {
    public static final String COJ_QUEUE = "coj_queue";
    public static final String COJ_DIRECT_EXCHANGE = "coj_direct_exchange";
    public static final String ROUTINGKEY_COJ="coj_routingKey";

    //声明交换机
    @Bean(COJ_DIRECT_EXCHANGE)
    public Exchange EXCHANGE_TOPICS_INFORM(){
        //durable(true) 持久化，mq重启之后交换机还在
        return ExchangeBuilder.directExchange(COJ_DIRECT_EXCHANGE).durable(false).build();
    }

    //声明队列
    @Bean(COJ_QUEUE)
    public Queue QUEUE_INFORM_EMAIL(){
        return new Queue(COJ_QUEUE);
    }

    //ROUTINGKEY_EMAIL队列绑定交换机，指定routingKey
    @Bean
    public Binding BINDING_QUEUE_INFORM_EMAIL(@Qualifier(COJ_QUEUE) Queue queue,
                                              @Qualifier(COJ_DIRECT_EXCHANGE) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_COJ).noargs();
    }
}
