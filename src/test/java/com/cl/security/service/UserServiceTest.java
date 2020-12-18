package com.cl.security.service;

import com.cl.security.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserServiceImpl userService;

    @Test
    void getPermissionList() {
        System.out.println(userService.getPermissionList(1l));
    }
    @Test
    void getUserByUsername() {
        System.out.println(userService.getUserByUsername("zhan"));
    }

}