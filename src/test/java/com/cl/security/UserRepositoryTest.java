package com.cl.security;

import com.cl.security.repository.UserRepository;
import com.cl.security.entity.User;
import com.cl.security.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Test
    public void testSave() {
        User user = new User();
//        user.setNick("张三");
//        user.setUsername("zhangsan");
//        user.setPassword("123456");
        Specification<User> specification = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("loginName"), "admin");
            }
        };
        System.out.println(userRepository.findAll(specification));
    }
}