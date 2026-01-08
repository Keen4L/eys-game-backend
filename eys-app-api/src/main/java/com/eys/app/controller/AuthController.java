package com.eys.app.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.eys.common.result.R;
import com.eys.model.dto.auth.WxLoginDTO;
import com.eys.model.vo.auth.LoginVO;
import com.eys.service.sys.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 小程序端认证控制器
 *
 * @author EYS
 */
@Tag(name = "小程序认证", description = "微信登录/登出接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;

    /**
     * 微信登录
     */
    @Operation(summary = "微信登录", description = "使用微信code登录小程序")
    @PostMapping("/wxLogin")
    public R<LoginVO> wxLogin(@Valid @RequestBody WxLoginDTO dto) {
        LoginVO loginVO = sysUserService.wxLogin(dto.getCode());
        return R.ok(loginVO);
    }

    /**
     * 登出
     */
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public R<Void> logout() {
        StpUtil.logout();
        return R.ok();
    }

    /**
     * 获取当前登录用户信息
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public R<LoginVO> info() {
        Long userId = StpUtil.getLoginIdAsLong();
        var user = sysUserService.getById(userId);
        if (user == null) {
            return R.fail("用户不存在");
        }
        return R.ok(LoginVO.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .roleType(user.getRoleType())
                .token(StpUtil.getTokenValue())
                .build());
    }
}
