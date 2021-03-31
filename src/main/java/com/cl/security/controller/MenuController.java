package com.cl.security.controller;

import cn.hutool.json.JSONUtil;
import com.cl.security.common.result.CommonResult;
import com.cl.security.entity.Menu;
import com.cl.security.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("menu")
@CrossOrigin
public class MenuController {
    @Autowired
    private MenuService menuService;

    @PostMapping("insert")
    public CommonResult insertMenu(Menu menu) {
        return menuService.insert(menu);
    }
    @PostMapping("update")
    public CommonResult updateMenu( String menuList) {
        List<Menu> i = JSONUtil.toList(JSONUtil.parseArray(menuList),Menu.class);
        return menuService.batchUpdate(i);
    }
    @GetMapping("list")
    public CommonResult listMenu() {
        return menuService.listMenu();
    }
    @GetMapping("get")
    public CommonResult getMenu(Integer type) {
        return menuService.getMenu(type);
    }

}
