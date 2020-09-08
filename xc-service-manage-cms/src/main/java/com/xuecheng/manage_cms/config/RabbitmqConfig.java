package com.xuecheng.manage_cms.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置文件
 * @author 1159588554@qq.com
 * @date 2020/6/16 14:51
 */
@Configuration
public class RabbitmqConfig {
    /**
     * 定义交换机
     */
    public static final String EX_ROUTING_CMS_POSTPAGE="ex_routing_cms_postpage";

    /**
     * 交换机使用direct类型
     * @return 交换机
     */
    @Bean
    public Exchange exchange(){
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }
}
