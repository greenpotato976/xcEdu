package com.xuecheng.test.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author 1159588554@qq.com
 * @date 2020/6/9 14:27
 *
 * RabbitMQ配置类
 * 配置Exchange，Queue，以及绑定交换机
 */
@Configuration
public class RabbitmqConfig {
    public static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    public static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    public static final String EXCHANGE_TOPICS_INFORM="exchange_topics_inform";

    /**
     * 配置交换机
     * ExchangeBuilder提供了fanout、direct、topic、header交换机类型的配置
     * @return 交换机
     */
    @Bean(EXCHANGE_TOPICS_INFORM)
    public Exchange exchangeTopicsInform(){
        //durable(true)持久化，消息队列重启后交换机仍然存在
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();
    }

    /**
     *配置队列
     * @return 短信消息队列
     */
    @Bean(QUEUE_INFORM_SMS)
    public Queue queueInformSms(){
        return new Queue(QUEUE_INFORM_SMS);
    }

    /**
     * 配置队列
     * @return email消息队列
     */
    @Bean(QUEUE_INFORM_EMAIL)
    public Queue queueInformEmail(){
        return new Queue(QUEUE_INFORM_EMAIL);
    }

    /**
     * 绑定短信消息队列
     * @return 绑定对象
     */
    @Bean
    public Binding bindingQueueInformSms(@Qualifier(QUEUE_INFORM_SMS) Queue queue,@Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("inform.#.sms.#").noargs();
    }

    /**
     * 绑定邮箱消息队列
     * @param queue 队列
     * @param exchange 交换机
     * @return 绑定对象
     */
    @Bean
    public Binding bindingQueueInformEmail(@Qualifier(QUEUE_INFORM_EMAIL) Queue queue,@Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("inform.#.email.#").noargs();
    }
}
