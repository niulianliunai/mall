package com.cl.security.repository;

import com.cl.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
*
* @author chenlong
* @date 2020/12/7
*/
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
}
