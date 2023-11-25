package www.raven.jc.config;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * user details service impl
 *
 * @author 刘家辉
 * @date 2023/11/25
 */
@Service
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        SecurityUserDetails securityUserDetails = new SecurityUserDetails(
                "user",
                passwordEncoder.encode("user"),
                true, true, true, true, new ArrayList<>(),
                1L
        );
        return Mono.just(securityUserDetails);
    }
}