package com.itheima.test;

import com.itheima.sms.SmsManageApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = SmsManageApplication.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    //@Test
    public void test1(){
        for (int i = 0; i < 10; i++) {
            redisTemplate.convertAndSend("MYTOPIC","this is a message ");
        }
    }

}
