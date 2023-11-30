package www.raven.jc.override;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import www.raven.jc.constant.JwtConstant;
import www.raven.jc.dto.TokenDTO;
import www.raven.jc.util.JwtUtil;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * default security context repository
 * <p>
 * 存储认证授权的相关信息
 *
 * @author 刘家辉
 * @date 2023/11/28
 */
@Component
@Slf4j
public class DefaultSecurityContextRepository implements ServerSecurityContextRepository {

    @Value("${Raven.key}")
    private String key;
    @Resource
    private TokenAuthenticationManager tokenAuthenticationManager;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        List<String> tokens = request.getHeaders().get(JwtConstant.TOKEN);
        if (tokens == null || tokens.isEmpty()) {
            return Mono.empty();
        }
        TokenDTO dto = JwtUtil.verify(tokens.get(0), key);
        Assert.isTrue(Objects.equals(tokens.get(0), redissonClient.getBucket("token:" + dto.getUserId()).get()), "Invalid token");
        request.mutate().header("userId", dto.getUserId().toString()).build();
        if (dto.getExpireTime() < System.currentTimeMillis()) {
            HashMap<String, Object> map = new HashMap<>(3);
            map.put("userId", dto.getUserId());
            map.put("role", dto.getRole());
            String token = JwtUtil.createToken(map, key);
            redissonClient.getBucket("token:" + dto.getUserId()).set(token);
            exchange.getResponse().getHeaders().set(JwtConstant.REFRESH, token);

        }
        Authentication auth = new UsernamePasswordAuthenticationToken(dto.getUserId(), null,  AuthorityUtils.createAuthorityList(dto.getRole().toArray(new String[0])));
        return tokenAuthenticationManager.authenticate(
                auth
        ).map(SecurityContextImpl::new);
    }
}
