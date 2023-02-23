package com.itheima.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.sms.entity.PlatformEntity;
import com.itheima.sms.mapper.PlatformMapper;
import com.itheima.sms.service.PlatformService;
import org.springframework.stereotype.Service;

/**
 * 接入平台
 *
 * @author 传智播客
 *
 */
@Service
public class PlatformServiceImpl extends ServiceImpl<PlatformMapper, PlatformEntity> implements PlatformService {

    @Override
    public PlatformEntity getByName(String name) {
        LambdaUpdateWrapper<PlatformEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PlatformEntity::getName, name);
        return this.getOne(wrapper);
    }
}
