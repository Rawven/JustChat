package www.raven.jc.filter;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import www.raven.jc.constant.Filter;
import www.raven.jc.dto.TokenDTO;
import www.raven.jc.util.JwtUtil;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * token filter
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@Component
@Slf4j
public class ToolFilter implements WebFilter {
    @Value("${Raven.key}")
    private String key;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (Arrays.stream(Filter.WHITE_PATH).noneMatch(whitePath -> whitePath.equals(path))) {
            List<String> tokens = request.getHeaders().get(Filter.TOKEN);
            log.info("token: " + tokens);
            if (tokens == null || tokens.isEmpty()) {
                return unauthorized(exchange);
            }
            TokenDTO dto = JwtUtil.verify(tokens.get(0), key);
            Assert.isTrue(Objects.equals(tokens.get(0), redissonClient.getBucket("token:" + dto.getUserId()).get()), "Invalid token");
            ServerHttpRequest realRequest = request.mutate().header("userId", dto.getUserId().toString()).build();
            return chain.filter(exchange.mutate().request(realRequest).build());
        }
        return chain.filter(exchange);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        DataBuffer buffer = response.bufferFactory().wrap("Invalid token".getBytes(StandardCharsets.UTF_8));
        log.info("token验证失败");
        return response.writeWith(Mono.just(buffer));

    }
}
