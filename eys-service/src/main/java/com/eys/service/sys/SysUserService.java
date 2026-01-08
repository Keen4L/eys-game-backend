package com.eys.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eys.model.entity.sys.SysUser;
import com.eys.model.vo.auth.LoginVO;

/**
 * 系统用户 Service 接口
 *
 * @author EYS
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 管理员登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录信息
     */
    LoginVO adminLogin(String username, String password);

    /**
     * 微信小程序登录
     *
     * @param code 微信登录凭证
     * @return 登录信息
     */
    LoginVO wxLogin(String code);

    /**
     * 根据 OpenID 获取用户
     *
     * @param openid 微信OpenID
     * @return 用户信息
     */
    SysUser getByOpenid(String openid);

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getByUsername(String username);
}
