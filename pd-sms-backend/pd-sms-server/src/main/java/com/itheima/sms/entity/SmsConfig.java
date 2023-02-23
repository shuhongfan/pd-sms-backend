/**
 * Copyright (c) 2019 联智合创 All rights reserved.
 * <p>
 * http://www.witlinked.com
 * <p>
 * 版权所有，侵权必究！
 */

package com.itheima.sms.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * 短信配置信息
 *
 * @author
 */
@Data
@ApiModel(description = "短信配置信息")
public class SmsConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String domain;
    private String name;

    @ApiModelProperty(value = "平台 ali：阿里云  mengwang：梦网")
    private String platform;

    @ApiModelProperty(value = "AccessKeyId")
    private String accessKeyId;

    @ApiModelProperty(value = "AccessKeySecret")
    private String accessKeySecret;

    private LinkedHashMap<String, String> otherConfig = new LinkedHashMap<>();

    public String get(String key) {
        return otherConfig.get(key);
    }

    public void set(String key, String value) {
        this.otherConfig.put(key, value);
    }

}
