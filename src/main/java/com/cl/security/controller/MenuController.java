package com.cl.security.controller;

import com.cl.security.common.result.CommonResult;
import com.cl.security.entity.Menu;
import com.cl.security.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("menu")
@CrossOrigin
public class MenuController {
    @Autowired
    private MenuService menuService;

    @GetMapping("list")
    public CommonResult listMenu() {
        return CommonResult.success(menuService.listMenu());
    }

}
