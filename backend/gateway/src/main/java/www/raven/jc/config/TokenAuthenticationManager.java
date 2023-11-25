package www.raven.jc.config;

import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;


/**
 * token authentication manager
 *
 * @author 刘家辉
 * @date 2023/11/25
 */
@Component
@Primary
public class TokenAuthenticationManager implements ReactiveAuthenticationManager {

    @Override
    @SuppressWarnings("unchecked")
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .map(auth -> Jwt.parseJwtRsa256(auth.getPrincipal().toString()))
                .map(claims -> {
                    Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
                    return new UsernamePasswordAuthenticationToken(
                            claims.get,
                            null,
                            roles
                    );
                });
    }
}