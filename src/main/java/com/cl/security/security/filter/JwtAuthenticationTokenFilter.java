package com.cl.security.security.filter;

import com.cl.security.common.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
* Jwt登录授权过滤器
* @author chenlong
* @date 2020/12/8
*/
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);
    @Autowired
    private UserDetailsService userDetailsService;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value(("${jwt.tokenHead}"))
    private String tokenHead;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        // 获取http请求头中的Authorization
        String authHeader = httpServletRequest.getHeader(this.tokenHeader);
        // 判断tokenHead是否存在
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            // 获取token
            String authToken = authHeader.substring(this.tokenHead.length());
            // 从token中获取用户名
            String username = JwtTokenUtil.getUserNameFromToken(authToken);
            LOGGER.info("check username: {}", username);
            // 判断token中是否有用户名、security上下文是否存在用户（是否已登录）
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 通过用户名获取用户
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                // 判断token是否被篡改、用户是否存在
                if (JwtTokenUtil.validateToken(authToken, userDetails)) {
                    // 在security上下文中加入用户 （登录）
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    LOGGER.info("authenticated user:{}", username);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
