package www.raven.jc.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import www.raven.jc.filter.JwtFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

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
    private JwtFilter jwtFilter;
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
            http.cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(exchanges -> exchanges
                            .pathMatchers("/account/auth/**").permitAll()
                            .pathMatchers("/chat/**","/account/info/**").hasRole("USER")
                            .anyExchange().authenticated()
                    )
                    .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            ;
            // your other configurations
            return http.build();
        }
}
