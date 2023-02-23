package com.itheima.test;

import com.itheima.sms.SmsApiApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = SmsApiApplication.class)
public class RedisListTest {
    @Autowired
    private RedisTemplate redisTemplate;

    //@Test
    public void testPush(){
        for (int i = 0; i < 10; i++) {
            redisTemplate.opsForList().leftPush("itcast","msg" + i);
        }
    }

    //@Test
    public void testPop(){
        for (int i = 0; i < 11; i++) {
            Object itcast = redisTemplate.opsForList().rightPop("itcast");
            System.out.println("消费消息："+itcast);
        }
    }

}
