package com.cl.security.service;

import com.cl.security.common.result.CommonResult;
import com.cl.security.entity.Permission;
import com.cl.security.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Optional;

@Service
public class PermissionService {
    @Autowired
    PermissionRepository permissionRepository;

    public CommonResult listPermission() {
        return CommonResult.success(permissionRepository.findAll());
    }
    public boolean isExist(String path) {
        Specification<Permission> specification = new Specification<Permission>() {
            @Override
            public Predicate toPredicate(Root<Permission> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("path"), path);
            }
        };
        Optional<Permission> permission  = permissionRepository.findOne(specification);
        return permission.isPresent();
    }
}
