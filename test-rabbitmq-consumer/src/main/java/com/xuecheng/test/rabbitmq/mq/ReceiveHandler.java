package com.xuecheng.test.rabbitmq.mq;

import com.rabbitmq.client.Channel;
import com.xuecheng.test.rabbitmq.config.RabbitmqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author 1159588554@qq.com
 * @date 2020/6/9 15:52
 * 使用@RabbitListener注解监听队列。
 */
@Component
public class ReceiveHandler {
    /**
     * 监听信息队列
     */
    @RabbitListener(queues={RabbitmqConfig.QUEUE_INFORM_SMS})
    public void receiveSms(String msg, Message message, Channel channel){
        System.out.println(msg);
    }
    /**
     * 监听信息队列
     */
    @RabbitListener(queues={RabbitmqConfig.QUEUE_INFORM_EMAIL})
    public void receiveEmail(String msg, Message message, Channel channel){
        System.out.println(msg);
    }
}
