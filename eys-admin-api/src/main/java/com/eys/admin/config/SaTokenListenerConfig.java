package com.eys.admin.config;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.SaLoginModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Sa-Token 监听器
 * 用于记录登录/登出日志
 *
 * @author EYS
 */
@Slf4j
@Component
public class SaTokenListenerConfig implements SaTokenListener {

    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel) {
        log.info("用户登录: loginType={}, loginId={}", loginType, loginId);
    }

    @Override
    public void doLogout(String loginType, Object loginId, String tokenValue) {
        log.info("用户登出: loginType={}, loginId={}", loginType, loginId);
    }

    @Override
    public void doKickout(String loginType, Object loginId, String tokenValue) {
        log.info("用户被踢下线: loginType={}, loginId={}", loginType, loginId);
    }

    @Override
    public void doReplaced(String loginType, Object loginId, String tokenValue) {
        log.info("用户被顶下线: loginType={}, loginId={}", loginType, loginId);
    }

    @Override
    public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {
        log.info("用户被封禁: loginType={}, loginId={}, service={}, level={}, disableTime={}",
                loginType, loginId, service, level, disableTime);
    }

    @Override
    public void doUntieDisable(String loginType, Object loginId, String service) {
        log.info("用户解封: loginType={}, loginId={}, service={}", loginType, loginId, service);
    }

    @Override
    public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {
        // 二级认证开启
    }

    @Override
    public void doCloseSafe(String loginType, String tokenValue, String service) {
        // 二级认证关闭
    }

    @Override
    public void doCreateSession(String id) {
        // Session 创建
    }

    @Override
    public void doLogoutSession(String id) {
        // Session 注销
    }

    @Override
    public void doRenewTimeout(String tokenValue, Object loginId, long timeout) {
        // Token 续期
    }
}
