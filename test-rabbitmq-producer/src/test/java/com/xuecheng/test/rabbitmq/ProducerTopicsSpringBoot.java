package com.xuecheng.test.rabbitmq;

import com.xuecheng.test.rabbitmq.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 1159588554@qq.com
 * @date 2020/6/9 15:36
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProducerTopicsSpringBoot {
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 1、交换机名称
     * 2、routingKey
     * 3、消息内容
     */
    @Test
    public void testMessage(){
        String message = "test01";
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM,"inform.email",message);
    }
}
