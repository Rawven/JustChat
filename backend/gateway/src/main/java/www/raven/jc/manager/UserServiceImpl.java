package www.raven.jc.manager;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import www.raven.jc.dto.TokenDTO;
import www.raven.jc.util.JwtUtil;

import java.util.Arrays;
import java.util.List;

/**
 * user de
 *
 * @author 刘家辉
 * @date 2023/11/27
 */
@Service
public class UserServiceImpl implements ReactiveUserDetailsService {
    @Autowired
    private RedissonClient redissonClient;


    @Override
    public Mono<UserDetails> findByUsername(String username) {
        RBucket<Object> bucket = redissonClient.getBucket("token:" + username);
        TokenDTO dto = JwtUtil.verify(bucket.get().toString(), "爱你老妈");
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList( dto.getRole().toArray(new String[0]));
        return Mono.just(new User(dto.getUserId().toString(), null, authorityList));
    }
}
