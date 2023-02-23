package com.itheima.test;

import com.itheima.sms.SmsServerApplication;
import com.itheima.sms.entity.ConfigEntity;
import com.itheima.sms.service.ConfigService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmsServerApplication.class)
public class Test {
    @Autowired
    private ConfigService configService;

    @org.junit.Test
    public void test1(){
        List<ConfigEntity> configEntities = configService.listForConnect();
        System.out.println(configEntities);
    }

    @org.junit.Test
    public void test2(){
        List<ConfigEntity> configEntities = configService.listForNewConnect();
        System.out.println(configEntities);
    }
}
