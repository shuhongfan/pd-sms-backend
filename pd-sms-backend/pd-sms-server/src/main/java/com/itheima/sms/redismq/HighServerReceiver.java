package com.itheima.sms.redismq;

import com.itheima.sms.factory.SmsConnectLoader;
import com.itheima.sms.model.ServerTopic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

/**
 * Redis发布订阅----订阅者，通过Redis的发布订阅模式监听通道相关消息
 */
@Component
@Slf4j
public class HighServerReceiver implements MessageListener {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SmsConnectLoader smsConnectLoader;

    /**
     * 消息监听
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        //TODO 消息监听，根据消息内容调用smsConnectLoader进行通道初始化或者通道更新

        //将消息体进行反序列化，得到json字符串
        String jsonMsg = redisTemplate.getDefaultSerializer().deserialize(message.getBody()).toString();
        //将json字符串封装成ServerTopic对象
        ServerTopic serverTopic = ServerTopic.load(jsonMsg);

        switch (serverTopic.getOption()){
            case ServerTopic.INIT_CONNECT://初始化通道
                smsConnectLoader.initConnect();
                break;
            case ServerTopic.USE_NEW_CONNECT://更新通道
                smsConnectLoader.changeNewConnect();
            default:
                break;
        }

    }
}
