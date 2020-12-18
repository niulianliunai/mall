package com.cl.security.repository;

import com.cl.security.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

/**
*
* @author chenlong
* @date 2020/12/7
*/
public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
