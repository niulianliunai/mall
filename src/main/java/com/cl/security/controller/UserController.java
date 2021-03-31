package com.cl.security.controller;

import com.cl.security.common.result.CommonResult;
import com.cl.security.entity.Permission;
import com.cl.security.entity.User;
import com.cl.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
*
* @author chenlong
* @date 2020/12/8
*/
@RestController
@RequestMapping("user")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService ;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

//    /**
//     * 注册
//     * @param userParam param
//     * @return User
//     */
//    @PostMapping("register")
//    public CommonResult<User> register(User userParam) {
//        User user = userService.savaUser(userParam);
//        if (user == null) {
//            return CommonResult.failed();
//        }
//        return CommonResult.success(user);
//    }
    @PostMapping("login")
    public CommonResult login(User userParam) {
        String token = userService.login(userParam.getUsername(), userParam.getPassword());
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token",token);
        tokenMap.put("tokenHead",tokenHead);
        return CommonResult.success(tokenMap);
    }
    @GetMapping("permission")
    public CommonResult<Set<Permission>> getPermissionList(HttpServletRequest request) {
        Long userId = userService.getUserIdFromRequest(request);
        Set<Permission> permissions = userService.listPermission(userId);
        return CommonResult.success(permissions);
    }

    @GetMapping("info")
    public CommonResult<User> getUserInfo(String token) {
        return CommonResult.success(userService.getUserByToken(token));
    }
    @GetMapping("test")
    public CommonResult test() {
        return CommonResult.success("123");
    }
}
