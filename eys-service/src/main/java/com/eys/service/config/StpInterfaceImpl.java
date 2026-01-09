package com.eys.service.config;

import cn.dev33.satoken.stp.StpInterface;
import com.eys.common.constant.RoleType;
import com.eys.model.entity.sys.SysUser;
import com.eys.service.sys.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 权限实现
 * 实现 StpInterface 接口，提供角色和权限查询
 *
 * @author EYS
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final SysUserService sysUserService;

    /**
     * 返回用户的权限列表
     * 目前暂不使用细粒度权限，返回空列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return new ArrayList<>();
    }

    /**
     * 返回用户的角色列表
     * 根据 SysUser.roleType 返回角色标识
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> roles = new ArrayList<>();

        try {
            Long userId = Long.parseLong(loginId.toString());
            SysUser user = sysUserService.getById(userId);

            if (user != null && user.getRoleType() != null) {
                // 管理员
                if (RoleType.ADMIN.getCode().equals(user.getRoleType())) {
                    roles.add("admin");
                    roles.add("dm"); // 管理员同时拥有 DM 角色
                }
                // DM
                else if (RoleType.DM.getCode().equals(user.getRoleType())) {
                    roles.add("dm");
                }
                // 普通玩家
                else {
                    roles.add("player");
                }
            }
        } catch (Exception e) {
            // 解析失败返回空列表
        }

        return roles;
    }
}
