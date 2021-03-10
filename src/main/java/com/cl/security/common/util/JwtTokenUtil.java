package com.cl.security.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
*
* @author chenlong
* @date 2020/12/7
*/
@Component
public class JwtTokenUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger((JwtTokenUtil.class));
    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private  Long expiration;

    private static String SECRET;
    private static Long EXPIRATION;

    /**
     * 从配置文件读取secret和expiration到内存中
     */
    @PostConstruct
    public void init() {
        SECRET = secret;
        EXPIRATION = expiration;
    }

    /**
     * 把用户信息存到负载里
     * @param userDetails
     * @return
     */
    public static String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }
    /**
     * 根据负载生成token
     * @param claims 负载
     * @return token
     */
    private static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    /**
     * 验证token是否有效
     * @param token token
     * @param userDetails userDetails
     * @return token是否有效
     */
    public static boolean validateToken(String token, UserDetails userDetails) {
        String username = getUserNameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token) ;
    }

    /**
     * 判断token是否过期
     * @param token token
     * @return token是否过期
     */
    public static boolean isTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        return expiredDate.before(new Date());
    }

    /**
     * 从token中获取过期时间
     * @param token token
     * @return token过期时间
     */
    public static Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 从token中获取用户名
     * @param token token
     * @return username
     */
    public static String getUserNameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }


    /**
     * 从token中获取负载
     * @param token token
     * @return claims
     */
    private static Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            LOGGER.info("JWT格式验证失败：{}", token);
        }
        return claims;
    }
    /**
     * 刷新token
     * @param token old token
     * @return new token
     */
    public static String refreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    /**
     * 判断token是否可以刷新
     * @param token token
     * @return 是否可以刷新
     */
    public static boolean canRefresh(String token) {
        return !isTokenExpired(token);
    }

    /**
     * 生成token的过期时间
     */
    private static Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + EXPIRATION * 1000);
    }
}
