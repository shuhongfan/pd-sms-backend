package com.itheima.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.sms.dto.ConfigDTO;
import com.itheima.sms.entity.ConfigEntity;
import com.itheima.sms.mapper.ConfigMapper;
import com.itheima.sms.model.ServerTopic;
import com.itheima.sms.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通道配置表
 */
@Service
@Slf4j
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, ConfigEntity> implements ConfigService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public ConfigEntity getByName(String name) {
        LambdaUpdateWrapper<ConfigEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ConfigEntity::getName, name);
        return this.getOne(wrapper);
    }

    @Override
    public void getNewLevel(ConfigDTO entity) {
        LambdaUpdateWrapper<ConfigEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ConfigEntity::getIsEnable, 1);
        wrapper.eq(ConfigEntity::getIsActive, 1);
        wrapper.orderByDesc(ConfigEntity::getLevel);
        wrapper.last("limit 1");
        ConfigEntity configEntity = this.getOne(wrapper);
        if(configEntity == null){
            entity.setLevel(1);
        }else {
            entity.setLevel(configEntity.getLevel() + 1);
        }
    }

    @Override
    public void sendUpdateMessage() {
        // TODO 发送消息，通知短信发送服务更新内存中的通道优先级

        Map map = redisTemplate.opsForHash().entries("SERVER_ID_HASH");
        log.info("所有的短信发送服务实例：" + map);
        long currentTimeMillis = System.currentTimeMillis();

        for (Object key : map.keySet()) {
            Object value = map.get(key);
            long parseLong = Long.parseLong(value.toString());
            if(currentTimeMillis - parseLong < (1000 * 60 * 5)){
                //删除redis中缓存的可用通道，因为通道优先级发生变化，redis中缓存的可用通道需要重新加载
                redisTemplate.delete("listForConnect");

                //说明当前这个实例状态正常
                ServerTopic serverTopic = ServerTopic.builder().option(ServerTopic.INIT_CONNECT).value(key.toString()).build();
                //发送消息
                redisTemplate.convertAndSend("TOPIC_HIGH_SERVER",serverTopic.toString());
                return;
            }
        }
    }
}
