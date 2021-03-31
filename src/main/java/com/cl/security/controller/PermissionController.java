package com.cl.security.controller;

import com.cl.security.common.result.CommonResult;
import com.cl.security.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("permission")
@CrossOrigin
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @GetMapping("list")
    public CommonResult listPermission() {
        return permissionService.listPermission();
    }
}
