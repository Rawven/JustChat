package www.raven.jc.filter;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import www.raven.jc.constant.Filter;
import www.raven.jc.dto.TokenDTO;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.JwtUtil;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author ShiLei
 * @version 1.0.0
 * @date 2021/3/11 16:27
 * @description 存储认证授权的相关信息
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
        String path = request.getURI().getPath();
        List<String> tokens = request.getHeaders().get(Filter.TOKEN);
        log.info("token: " + tokens);
        TokenDTO dto = JwtUtil.verify(tokens.get(0), key);
        Assert.isTrue(Objects.equals(tokens.get(0), redissonClient.getBucket("token:" + dto.getUserId()).get()), "Invalid token");
        // Create an Authentication object
        log.info(JsonUtil.objToJson(dto));
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(dto.getRole());
        log.info(authorityList.toString());
        Authentication auth = new UsernamePasswordAuthenticationToken(dto.getUserId(), null, AuthorityUtils.createAuthorityList(dto.getRole()));
        //       ServerHttpRequest realRequest = request.mutate().header("userId", dto.getUserId().toString()).build();
        return tokenAuthenticationManager.authenticate(
                auth
        ).map(SecurityContextImpl::new);

    }
}
