package com.itheima.sms.redismq;

import com.itheima.sms.factory.SmsFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Redis队列消费者，监听消息队列TOPIC_GENERAL_SMS，普通优先级的短信，如营销短信
 */
@Component
@Slf4j
public class GeneralSmsListener extends Thread {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SmsFactory smsFactory;

    private String queueKey = "TOPIC_GENERAL_SMS";

    @Value("${spring.redis.queue.pop.timeout}")
    private Long popTimeout = 8000L;

    private ListOperations listOps;

    @PostConstruct
    private void init() {
        listOps = redisTemplate.opsForList();
        this.start();
    }

    @Override
    public void run() {
        //TODO 监听TOPIC_GENERAL_SMS队列，如果有消息则调用短信发送工厂发送实时短信
        log.info("监听队列：{}中的短信消息",queueKey);

        //持续监听，所以需要使用死循环
        while (true){
            String message = (String) listOps.rightPop(queueKey, popTimeout, TimeUnit.MILLISECONDS);
            if(message != null){
                //队列中有消息，需要发送短信
                log.info("队列：{}中收到短信发送消息：{}",queueKey,message);
                //调用短信发送工厂发送短信
                smsFactory.send(message);
            }
        }

    }
}
