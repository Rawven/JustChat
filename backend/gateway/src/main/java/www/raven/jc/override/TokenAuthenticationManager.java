package www.raven.jc.override;


import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author ShiLei
 * @version 1.0.0
 * @date 2021/3/11 13:23
 * @description token 认证处理
 */
@Component
@Primary
public class TokenAuthenticationManager implements ReactiveAuthenticationManager {

    @Override
    @SuppressWarnings("unchecked")
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication);
    }
}
