package com.itheima.sms.factory;

import com.alibaba.fastjson.JSON;
import com.itheima.sms.config.RedisLock;
import com.itheima.sms.entity.ConfigEntity;
import com.itheima.sms.entity.SmsConfig;
import com.itheima.sms.model.ServerTopic;
import com.itheima.sms.service.ConfigService;
import com.itheima.sms.service.impl.SignatureServiceImpl;
import com.itheima.sms.service.impl.TemplateServiceImpl;
import com.itheima.pinda.utils.SpringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 通道实例加载器
 * 执行时间：
 * 1、项目启动时
 * 2、通道重新排序时
 */
@Component
@Slf4j
@Order(value = 101)
public class SmsConnectLoader implements CommandLineRunner {

    private static final List<Object> CONNECT_LIST = new ArrayList<>();

    private static String BUILD_NEW_CONNECT_TOKEN = null;

    private static List<ConfigEntity> FUTURE_CONFIG_LIST;

    @Autowired
    private ConfigService configService;

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void run(String... args) {
        initConnect();
    }

    /**
     * 根据通道配置，初始化每个通道的bean对象
     */
    @SneakyThrows
    public void initConnect() {
        //TODO 根据通道配置，初始化每个通道的bean对象

        //1、查询数据库获得通道列表
        List<ConfigEntity> configs = configService.listForConnect();
        log.info("查询到可用通道：{}",configs);

        List beanList = new ArrayList();
        //2、遍历通道列表，通过反射创建每个通道的Bean对象（例如AliyunSmsService、MengWangSmsService等）
        configs.forEach(config -> {
            try {
                //封装Bean对象所需的SmsConfig配置对象
                SmsConfig smsConfig = new SmsConfig();
                smsConfig.setId(config.getId());
                smsConfig.setDomain(config.getDomain().trim());
                smsConfig.setName(config.getName().trim());
                smsConfig.setPlatform(config.getPlatform().trim());
                smsConfig.setAccessKeyId(config.getAccessKeyId().trim());
                smsConfig.setAccessKeySecret(config.getAccessKeySecret().trim());
                if (StringUtils.isNotBlank(config.getOther())) {
                    LinkedHashMap linkedHashMap = JSON.parseObject(config.getOther(), LinkedHashMap.class);
                    smsConfig.setOtherConfig(linkedHashMap);
                }

                //动态拼接要创建的bean实例的全类名
                String className = "com.itheima.sms.sms." + config.getPlatform().trim() + "SmsService";
                log.info("准备通过反射动态创建：{}",className);

                Class<?> aClass = Class.forName(className);
                //获得类的构造方法对象
                Constructor<?> constructor = aClass.getConstructor(SmsConfig.class);
                //创建bean对象
                Object beanService = constructor.newInstance(smsConfig);

                //bean对象中的signatureService和templateService属性需要进行赋值
                SignatureServiceImpl signatureService = SpringUtils.getBean(SignatureServiceImpl.class);
                TemplateServiceImpl templateService = SpringUtils.getBean(TemplateServiceImpl.class);

                //根据反射获得类中声明的属性对象
                Field signatureServiceField = aClass.getSuperclass().getDeclaredField("signatureService");
                Field templateServiceField = aClass.getSuperclass().getDeclaredField("templateService");
                //设置可以操作当前属性值
                signatureServiceField.setAccessible(true);
                templateServiceField.setAccessible(true);

                //为bean对象设置属性值
                signatureServiceField.set(beanService,signatureService);
                templateServiceField.set(beanService,templateService);

                beanList.add(beanService);
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //3、将每个通道的Bean对象保存到CONNECT_LIST集合中
        if(!CONNECT_LIST.isEmpty()){
            CONNECT_LIST.clear();
        }
        CONNECT_LIST.addAll(beanList);
        log.info("将初始化的通道加载到集合中：{}",CONNECT_LIST);

        //解锁
        if (StringUtils.isNotBlank(BUILD_NEW_CONNECT_TOKEN)) {
            redisLock.unlock("buildNewConnect", BUILD_NEW_CONNECT_TOKEN);
        }
    }

    public <T> T getConnectByLevel(Integer level) {
        return (T) CONNECT_LIST.get(level - 1);
    }

    public boolean checkConnectLevel(Integer level) {
        return CONNECT_LIST.size() <= level;
    }

    /**
     * 通道调整：
     * 通道初始化：构建新的通道配置
     * 只能有一台机器执行，所以需要加锁
     */
    public void buildNewConnect() {
        // 一小时内有效
        String token = redisLock.tryLock("buildNewConnect", 1000 * 60 * 60 * 1);
        log.info("buildNewConnect token:{}", token);
        if (StringUtils.isNotBlank(token)) {
            List<ConfigEntity> list = configService.listForNewConnect();
            FUTURE_CONFIG_LIST = list;
            redisTemplate.opsForValue().set("NEW_CONNECT_SERVER", ServerRegister.SERVER_ID);
            BUILD_NEW_CONNECT_TOKEN = token;
        }
        // 获取不到锁 证明已经有服务在计算或者计算结果未得到使用
    }

    /**
     * 通道调整：
     * 发布订阅消息，通知其他服务：应用新的通道
     */
    public void changeNewConnectMessage() {
        redisTemplate.convertAndSend("TOPIC_HIGH_SERVER", ServerTopic.builder().option(ServerTopic.USE_NEW_CONNECT).value(ServerRegister.SERVER_ID).build().toString());
    }

    /**
     * 通道调整
     * 发布订阅消息，通知其他服务：初始化新通道
     */
    public void changeNewConnect() {
        // 初始化通道
        Object newConnectServer = redisTemplate.opsForValue().get("NEW_CONNECT_SERVER");

        /**
         * 为了通道调整发布的消息中，带有server id
         * 确保只有此server id的服务执行当前代码
         */
        if (null != newConnectServer && ServerRegister.SERVER_ID.equals(newConnectServer) &&
                !CollectionUtils.isEmpty(FUTURE_CONFIG_LIST)) {
            // 配置列表不为空则执行数据库操作 并清空缓存
            boolean result = configService.updateBatchById(FUTURE_CONFIG_LIST);
            log.info("批量修改配置级别:{}", result);
            FUTURE_CONFIG_LIST.clear();
            redisTemplate.convertAndSend("TOPIC_HIGH_SERVER", ServerTopic.builder().option(ServerTopic.INIT_CONNECT).value(ServerRegister.SERVER_ID).build().toString());
        }
    }
}
