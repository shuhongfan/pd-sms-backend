package com.itheima.test;

import com.itheima.sms.SmsApiApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = SmsApiApplication.class)
public class RedisListTest {
   // @Autowired
    private RedisTemplate redisTemplate;

    //生产消息
   // @Test
    public void testPush(){
        for (int i = 0; i < 100; i++) {
            redisTemplate.opsForList().leftPush("mylist","msg" + i);
        }
    }

    //消费消息
    //@Test
    public void testPop(){
        while (true){
            Object mylist = redisTemplate.opsForList().rightPop("mylist",5l, TimeUnit.SECONDS);
            if(mylist != null){
                System.out.println("消费消息："+mylist);
            }
        }
    }
}
