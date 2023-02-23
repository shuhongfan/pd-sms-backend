package com.itheima.test;

import com.itheima.sms.SmsManageApplication;
import com.itheima.sms.sms.dto.SmsParamsDTO;
import com.itheima.sms.sms.service.SmsSendService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmsManageApplication.class)
public class SdkTest {
    @Autowired
    private SmsSendService smsSendService;

    /**
     * 通过SDK方式调用短信接收服务
     */
    @Test
    public void testSend(){
        SmsParamsDTO dto = new SmsParamsDTO();

        dto.setMobile("13812345678");
        dto.setSignature("DXQM_000000001");
        dto.setTemplate("DXMB_000000001");
        Map<String, String> map = new HashMap<>();
        map.put("code","1234");
        dto.setParams(map);
        //dto.setSendTime("2020-12-18 10:00");
        //dto.setTimestamp(System.currentTimeMillis() +"");
        smsSendService.sendSms(dto);


    }

}
