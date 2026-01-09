package com.eys.service.sys.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eys.service.config.WechatConfig;
import com.eys.common.constant.RoleType;
import com.eys.common.exception.BizException;
import com.eys.common.result.ResultCode;
import com.eys.mapper.sys.SysUserMapper;
import com.eys.model.entity.sys.SysUser;
import com.eys.model.vo.auth.LoginVO;
import com.eys.service.sys.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统用户 Service 实现
 *
 * @author EYS
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final WechatConfig wechatConfig;

    @Override
    public LoginVO adminLogin(String username, String password) {
        // 查询用户
        SysUser user = getByUsername(username);
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }

        // 检查是否为管理员
        if (!RoleType.ADMIN.getCode().equals(user.getRoleType())) {
            throw new BizException(ResultCode.FORBIDDEN, "非管理员账号");
        }

        // 检查是否启用
        if (user.getIsEnabled() == null || user.getIsEnabled() != 1) {
            throw new BizException(ResultCode.USER_DISABLED);
        }

        // 验证密码
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new BizException(ResultCode.PASSWORD_ERROR);
        }

        // 登录
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        log.info("管理员登录成功: userId={}, username={}", user.getId(), username);

        return LoginVO.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .roleType(user.getRoleType())
                .token(token)
                .build();
    }

    @Override
    public LoginVO wxLogin(String code) {
        // 检查配置是否完整
        if (wechatConfig.getAppId() == null || wechatConfig.getAppId().isBlank() ||
                wechatConfig.getAppSecret() == null || wechatConfig.getAppSecret().isBlank()) {
            log.warn("微信登录配置不完整，使用模拟登录");
            // 开发环境：模拟登录，使用 code 作为 openid
            return mockWxLogin(code);
        }

        // 调用微信接口获取 openid
        String openid = getOpenidFromWechat(code);
        if (openid == null || openid.isBlank()) {
            throw new BizException(ResultCode.WX_LOGIN_FAILED, "获取 openid 失败");
        }

        return loginOrRegisterByOpenid(openid);
    }

    /**
     * 调用微信 jscode2session 接口获取 openid
     */
    private String getOpenidFromWechat(String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("appid", wechatConfig.getAppId());
        params.put("secret", wechatConfig.getAppSecret());
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");

        try {
            String response = HttpUtil.get(wechatConfig.getLoginUrl(), params);
            log.info("微信登录响应: {}", response);

            JSONObject json = JSON.parseObject(response);
            if (json.containsKey("errcode") && json.getIntValue("errcode") != 0) {
                log.error("微信登录失败: errcode={}, errmsg={}",
                        json.getIntValue("errcode"), json.getString("errmsg"));
                throw new BizException(ResultCode.WX_LOGIN_FAILED, json.getString("errmsg"));
            }

            return json.getString("openid");
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用微信接口异常: {}", e.getMessage());
            throw new BizException(ResultCode.WX_LOGIN_FAILED, "微信服务异常");
        }
    }

    /**
     * 根据 openid 登录或注册
     */
    private LoginVO loginOrRegisterByOpenid(String openid) {
        SysUser user = getByOpenid(openid);

        if (user == null) {
            // 自动注册新用户
            user = new SysUser();
            user.setOpenid(openid);
            user.setNickname("玩家" + openid.substring(openid.length() - 6));
            user.setRoleType(RoleType.PLAYER.getCode());
            user.setIsEnabled(1);
            save(user);
            log.info("微信用户自动注册: userId={}, openid={}", user.getId(), openid);
        }

        // 检查是否启用
        if (user.getIsEnabled() == null || user.getIsEnabled() != 1) {
            throw new BizException(ResultCode.USER_DISABLED);
        }

        // 登录
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        log.info("微信登录成功: userId={}, openid={}", user.getId(), openid);

        return LoginVO.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .roleType(user.getRoleType())
                .token(token)
                .build();
    }

    /**
     * 开发环境模拟微信登录（配置未填写时使用）
     */
    private LoginVO mockWxLogin(String code) {
        // 使用 code 模拟 openid
        String mockOpenid = "mock_" + code;
        log.warn("使用模拟微信登录: mockOpenid={}", mockOpenid);
        return loginOrRegisterByOpenid(mockOpenid);
    }

    @Override
    public SysUser getByOpenid(String openid) {
        return getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getOpenid, openid));
    }

    @Override
    public SysUser getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
    }
}
