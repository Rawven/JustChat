package www.raven.jc.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;
import www.raven.jc.override.DefaultSecurityContextRepository;
import www.raven.jc.override.TokenAuthenticationManager;
import www.raven.jc.handler.DefaultAccessDeniedHandler;
import www.raven.jc.handler.DefaultAuthenticationEntryPoint;
import www.raven.jc.handler.DefaultAuthenticationFailureHandler;
import www.raven.jc.manager.UserServiceImpl;

import java.util.LinkedList;
import java.util.List;

/**
 * security config
 *
 * @author 刘家辉
 * @date 2023/11/26
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private TokenAuthenticationManager tokenAuthenticationManager;
    @Autowired
    private DefaultAccessDeniedHandler defaultAccessDeniedHandler;
    @Autowired
    private DefaultAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private DefaultSecurityContextRepository defaultSecurityContextRepository;
    @Autowired
    private DefaultAuthenticationFailureHandler authenticationFailureHandler;
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //1.允许任何来源
        configuration.setAllowedOriginPatterns(List.of("http://localhost:5173"));
        //2.允许任何请求头
        configuration.addAllowedHeader(CorsConfiguration.ALL);
        //3.允许任何方法
        configuration.addAllowedMethod(CorsConfiguration.ALL);
        //4.允许凭证
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public   PasswordEncoder passwordEncoder(){
         return new BCryptPasswordEncoder();
    }
        @Bean
        public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            http
                    .authenticationManager(reactiveAuthenticationManager())
                    .securityContextRepository(defaultSecurityContextRepository)
                    .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(exchanges -> exchanges
                            .pathMatchers("/auth/**").permitAll()
                            .pathMatchers("/chat/**","/info/**").hasRole("USER")
                            .anyExchange().authenticated()
                    )
                    .formLogin()
                    // 自定义处理
                    .authenticationFailureHandler(authenticationFailureHandler)
                    .and()
                    .exceptionHandling()
                    .accessDeniedHandler(defaultAccessDeniedHandler)
                    .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint)




            ;
            // your other configurations
            return http.build();
        }

    @Bean
    ReactiveAuthenticationManager reactiveAuthenticationManager() {
        LinkedList<ReactiveAuthenticationManager> managers = new LinkedList<>();
        managers.add(authentication -> {
            // 其他登陆方式 (比如手机号验证码登陆) 可在此设置不得抛出异常或者 Mono.error
            return Mono.empty();
        });
        // 必须放最后不然会优先使用用户名密码校验但是用户名密码不对时此 AuthenticationManager 会调用 Mono.error 造成后面的 AuthenticationManager 不生效
        managers.add(new UserDetailsRepositoryReactiveAuthenticationManager(userServiceImpl));
        managers.add(tokenAuthenticationManager);
        return new DelegatingReactiveAuthenticationManager(managers);
    }

}
