package com.cl.security.service;

import com.cl.security.common.result.CommonResult;
import com.cl.security.entity.Menu;
import com.cl.security.repository.MenuRepository;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.transformation.SortedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class MenuService {
    @Autowired
    private MenuRepository menuRepository;

    public CommonResult insert(Menu menu) {
        if (menu.getParentId() != null) {
            menu.setParent(menuRepository.findById(menu.getParentId()).get());
        }
        Menu res = menuRepository.save(menu);
        return CommonResult.success(res);
    }
    public CommonResult batchUpdate(List<Menu> menuList) {
        menuList.stream().filter(menu ->  menu.getParentId() != null).forEach(menu -> {
            menu.setParent(menuRepository.findById(menu.getParentId()).get());
        });
        List<Menu> res = menuRepository.saveAll(menuList);
        return CommonResult.success(res);
    }

    public CommonResult listMenu() {
        Specification<Menu> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.where(
                    criteriaBuilder.isNull(root.get("parent")),
                    criteriaBuilder.notEqual(root.get("redirect"),"/menu")
            );
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("sort")));
            return criteriaQuery.getRestriction();
        };
        List<Menu> menuList = menuRepository.findAll(specification);
        return CommonResult.success(menuList);
    }

    public CommonResult getMenu(Integer type) {
        Specification<Menu> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.isNull(root.get("parent")),
                            criteriaBuilder.equal(root.get("type"), type)
                    )
            );
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("sort")));
            return criteriaQuery.getRestriction();
        };
        List<Menu> menuList = menuRepository.findAll(specification);
        return CommonResult.success(menuList);
    }

}
