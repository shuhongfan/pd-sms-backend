package com.itheima.sms.config;
import com.itheima.sms.listener.MyListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * 订阅发布模式的容器配置
 */
@Configuration
@AutoConfigureAfter({MyListener.class})
public class SubscriberConfig {

    @Autowired
    private MyListener myListener;

    /**
     * 创建消息监听容器
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisMessageListenerContainer getRedisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);

        //可以添加多个监听订阅频道
        //当前监听的是通道：MYTOPIC
        redisMessageListenerContainer.addMessageListener(new MessageListenerAdapter(myListener), new PatternTopic("MYTOPIC"));

        return redisMessageListenerContainer;
    }
}