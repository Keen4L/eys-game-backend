package com.eys.service.sys.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eys.common.constant.RoleType;
import com.eys.common.exception.BizException;
import com.eys.common.result.ResultCode;
import com.eys.mapper.sys.SysUserMapper;
import com.eys.model.entity.sys.SysUser;
import com.eys.model.vo.auth.LoginVO;
import com.eys.service.sys.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 系统用户 Service 实现
 *
 * @author EYS
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

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
        // TODO: 调用微信接口获取 openid
        // 这里需要实现微信登录逻辑
        throw new BizException("微信登录功能待实现");
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
