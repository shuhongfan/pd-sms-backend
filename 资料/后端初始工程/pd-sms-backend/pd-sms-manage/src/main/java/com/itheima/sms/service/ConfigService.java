package com.itheima.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.sms.dto.ConfigDTO;
import com.itheima.sms.entity.ConfigEntity;

/**
 * 配置表
 */
public interface ConfigService extends IService<ConfigEntity> {

    ConfigEntity getByName(String name);

    void getNewLevel(ConfigDTO entity);

    void sendUpdateMessage();
}
