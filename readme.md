# SpringSecurity + JWT



## 框架介绍

### SpringSecurity

> SpringSecurity是一个强大的可高度定制的认证和授权框架，对于Spring应用来说它是一套Web安全标准。SpringSecurity注重于为Java应用提供认证和授权功能，像所有的Spring项目一样，它对自定义需求具有强大的扩展性。

### JWT

> JWT是JSON WEB TOKEN的缩写，它是基于 RFC 7519 标准定义的一种可以安全传输的的JSON对象，由于使用了数字签名，所以是可信任和安全的。

### JWT的组成

+ JWT的格式: header.payload.signature
+ header 用于存放签名的生成算法
+ payload 用于存放用户名、token的生成时间和过期时间
+ signature 是以header和payload生成的签名，被篡改后失效
+ https://jwt.io/ 可从该网站解析JWT字符串

### JWT实现认证和授权的原理

* 用户调用登录接口，登录成功后获取token
* 之后用户每次请求都在http的header中添加Authorization，值为token
* 服务端通过对header中Authorization的解码及数字签名校验来获取其中的用户信息，从而实现认证和授权

### Hutool

> Hutool是一个丰富的Java开源工具包,它帮助我们简化每一行代码，减少每一个方法，mall项目采用了此工具包。



## 建表

+ user：用户表 
+ role： 角色表
+ permission： 权限表
+ user_role：用户角色表
+ role_permission: 角色权限表



## 环境搭建

### SpringBoot项目

#### pom.xml

```
<!--SpringSecurity依赖配置-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<!--Hutool Java工具包-->
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>4.5.7</version>
</dependency>
<!--JWT(Json Web Token)登录支持-->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.0</version>
</dependency>
```



#### SecurityConfig

（核心配置类）

```
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userService;
    @Autowired
    CustomAccessDeniedHandler customAccessDeniedHandler;
    @Autowired
    CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @Autowired
    CustomAccessDecisionManager customAccessDecisionManager;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement()
                // 禁用session （因为用jwt来鉴权和认证，不需要session）
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS)
                .permitAll()
                // 其他所有请求都要鉴权
                .anyRequest()
                .authenticated();
        // 禁用缓存
        http.headers().cacheControl();
        // 添加自定义jwt过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        // 添加自定义未授权和未登录结果返回
        http.exceptionHandling()
                .accessDeniedHandler(customAccessDeniedHandler)
                .authenticationEntryPoint(customAuthenticationEntryPoint);
        // 配置自定义动态请求拦截器 （用请求路径和用户权限配对）
        http.authorizeRequests()
        .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                o.setAccessDecisionManager(customAccessDecisionManager);
                return o;
            }
        });
    }

    /**
     * 配置userDetailsService和passwordEncoder
     * @param auth auth
     * @throws Exception e
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     *  取出用户权限
     * @return userDetailService
     */
    @Override
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userService.getUserByUsername(username);
            if (user != null) {
                Set<Permission> permissions = userService.getPermissionList(user.getId());
                return new UserDetailsImpl(user, new ArrayList<>(permissions));
            }
            throw new UsernameNotFoundException("用户名或密码错误");
        };
    }

    /**
     *  自定义拦截器
     * @return jwtAuthenticationTokenFilter
     */
    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }
}
```



#### UserDetailsImpl

（security需要的用户详情）

```
public class UserDetailsImpl implements UserDetails {
    private User user;
    private List<Permission> permissionList;
    public UserDetailsImpl(User user, List<Permission> permissionList) {
        this.user = user;
        this.permissionList = permissionList;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissionList.stream()
                .filter(permission -> permission.getValue() != null)
                .map(permission -> new SimpleGrantedAuthority(permission.getValue()))
                .collect(Collectors.toList());
    }


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
```



#### JwtAuthenticationTokenFilter

(自定义过滤器 检验是否已登录，token是否有效)

```
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
```

#### CustomAuthenticationEntryPoint

（未登录或token失效时访问接口）

```
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");
        httpServletResponse.getWriter().println(JSONUtil.parse(CommonResult.unauthorized(e.getMessage()))   );
        httpServletResponse.getWriter().flush();
    }
}
```



#### CustomAccseeDecisionManager

（自定义接口拦截器 动态权限）

```
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
```



#### CustomAccessDeniedHandler

（当接口没访问权限时的操作）

```
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");
        httpServletResponse.getWriter().println(JSONUtil.parse(CommonResult.forbidden(e.getMessage())));
        httpServletResponse.getWriter().flush();
    }
}
```





#### JwtTokenUtil

（Jwt工具类）

```
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
```



#### CommonResult

(通用返回对象)

```
@Data
public class CommonResult<T> {
    private long code;
    private String message;
    private T data;
    protected CommonResult() {

    }
    protected CommonResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 请求成功返回结果
     * @param data data
     * @param <T> type
     * @return success
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(), data);
    }

    /**
     * 自定义消息的请求成功返回结果
     * @param data data
     * @param message message
     * @param <T> type
     * @return success
     */
    public static <T> CommonResult<T> success(T data, String message) {
        return new CommonResult<T>(ResultCodeEnum.SUCCESS.getCode(), message, data);
    }

    /**
     * 为不同的请求失败的方法定义的通用方法
     * @param resultCodeEnum 错误码
     * @param <T> type
     * @return failed
     */
    public static <T> CommonResult<T> failed(ResultCodeEnum resultCodeEnum) {
        return new CommonResult<T>(resultCodeEnum.getCode(), resultCodeEnum.getMessage(), null);
    }

    /**
     * 自定义消息的返回结果
     * @param message message
     * @param <T> type
     * @return failed
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<T>(ResultCodeEnum.FAILED.getCode(), message, null);
    }

    /**
     * 请求失败的返回结果
     * @param <T> type
     * @return failed
     */
    public static <T> CommonResult<T> failed() {
        return failed(ResultCodeEnum.FAILED);
    }

    /**
     * 参数验证失败的返回结果
     * @param <T> type
     * @return failed
     */
    public static <T> CommonResult<T> validateFailed() {
        return failed(ResultCodeEnum.VALIDATE_FAILED);
    }

    /**
     * 自定义消息的参数验证失败的返回结果
     * @param message message
     * @param <T> type
     * @return failed
     */
    public static <T> CommonResult<T> validateFailed(String message) {
        return new CommonResult<T>(ResultCodeEnum.VALIDATE_FAILED.getCode(), message, null);
    }

    /**
     * 未登录返回结果
     * @param data data
     * @param <T> data
     * @return 未登录
     */
    public static <T> CommonResult<T> unauthorized(T data) {
        return new CommonResult<T>(ResultCodeEnum.UNAUTHORIZED.getCode(), ResultCodeEnum.UNAUTHORIZED.getMessage(), data);
    }

    /**
     * 未授权返回结果
     * @param data data
     * @param <T> type
     * @return 未授权
     */
    public static <T> CommonResult<T> forbidden(T data) {
        return new CommonResult<T>(ResultCodeEnum.FORBIDDEN.getCode(), ResultCodeEnum.FORBIDDEN.getMessage(), data);
    }
}
```



#### UserService

```
@Service
public class UserService{
    private final static Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Autowired
    private UserRepository userRepository;

    public User getUserByUsername(String username) {
        Specification<User> specification = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("username"), username);
            }
        };
        Optional<User> user = userRepository.findOne(specification);
        return user.orElse(null);

    }
    public String login(String username, String password) {
        String token = null;
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!passwordEncoder.matches(password,userDetails.getPassword())){
                throw new BadCredentialsException("密码不正确");
            }
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            token = JwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            LOGGER.warn("登录异常：{}" + e.getMessage());
        }
        return token;
    }

    public User savaUser(User userParam) {
        User user = new User();
        BeanUtils.copyProperties(userParam, user);
        Specification<User> specification =
                (Specification<User>) (root, criteriaQuery, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("username"), userParam.getUsername());

        if (!userRepository.findAll(specification).isEmpty()) {
            return null;
        }
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);

        return userRepository.save(user);
    }
    public Set<Permission> getPermissionList(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        Set<Permission> permissions = new HashSet<>();
        userOptional.ifPresent(user -> user.getRoles().forEach(role ->
                permissions.addAll(role.getPermissions())
        ));
        return permissions;
    }

    public Long getUserIdFromRequest(HttpServletRequest httpServletRequest)  {
        // 获取http请求头中的token
        String authHeader = httpServletRequest.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            String authToken = authHeader.substring(this.tokenHead.length());
            String username = JwtTokenUtil.getUserNameFromToken(authToken);
            Specification<User> specification = (Specification<User>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("username"), username);
            List<User> users = userRepository.findAll(specification);
            if (users.size() > 0) {
                return users.get(0).getId();
            }
        }
        return null;
    }

    public User getUserByToken(String token) {
        String username = JwtTokenUtil.getUserNameFromToken(token);
        return getUserByUsername(username);
    }

}
```
