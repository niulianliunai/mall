package com.cl.security.service;

import com.cl.security.entity.Permission;
import com.cl.security.entity.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Set;

public interface UserService {
    /**
     * 通过username获取user
     *
     * @param username username
     * @return user
     */
    public User getUserByUsername(String username);

    /**
     * 登录成功后返回jwt
     *
     * @param username username
     * @param password password
     * @return jwt
     */
    public String login(String username, String password);

    /**
     * 注册
     *
     * @param userParam user
     * @return user
     */
    public User savaUser(User userParam);

    /**
     * 获取用户权限列表
     *
     * @param id id
     * @return permissionList
     */
    public Set<Permission> getPermissionList(Long id);

    /**
     * 通过token获取用户id
     *
     * @param httpServletRequest req
     * @return id
     * @throws ServletException e
     * @throws IOException      e
     */
    public Long getUserIdFromRequest(HttpServletRequest httpServletRequest);
}