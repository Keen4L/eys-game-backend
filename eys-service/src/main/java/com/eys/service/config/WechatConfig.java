package com.eys.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置
 * 仅用于小程序端微信登录
 *
 * @author EYS
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WechatConfig {

    /**
     * 小程序 AppID
     */
    private String appId = "";

    /**
     * 小程序 AppSecret
     */
    private String appSecret = "";

    /**
     * 微信登录接口地址
     */
    private String loginUrl = "https://api.weixin.qq.com/sns/jscode2session";
}
