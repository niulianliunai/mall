package com.cl.security.security.custom;

import com.cl.security.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

/** 动态权限配置
 * @author chenlong
 * @date 2020/12/14
 */
@Component
public class CustomAccessDecisionManager implements AccessDecisionManager {
    @Value("${security.open.source}")
    private String[] openSource;
    @Autowired
    PermissionService permissionService;
    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        // 获取请求url
        String requestUrl = ((FilterInvocation) o).getRequestUrl();
        // 从url获取需要的权限， 如果包含问号就去掉
        String needPermission = requestUrl.replace("/",":").substring(1, !requestUrl.contains("?") ? requestUrl.length() : requestUrl.indexOf("?"));

        if (permissionService.isPublic(needPermission)) {
            return;
        }
        if (Arrays.stream(openSource).anyMatch(needPermission::contains)){
            return;
        }
        // 获取用户的权限列表
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean havePermission = authorities.stream()
                .anyMatch(authority ->
                            needPermission.equals(authority.getAuthority()));

        if (havePermission) {
            // 放行
            return;
        }
        throw new AccessDeniedException("权限不足，无法访问");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
