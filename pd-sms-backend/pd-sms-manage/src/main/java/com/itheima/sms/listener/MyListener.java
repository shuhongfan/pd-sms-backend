package com.itheima.sms.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * 自定义消息监听器，用于监听Redis频道中的消息
 */
@Component
@Slf4j
public class MyListener implements MessageListener {
    /**
     * 监听方法
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("接收到消息：" + message);
    }
}
