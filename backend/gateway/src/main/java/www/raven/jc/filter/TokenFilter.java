package www.raven.jc.filter;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import www.raven.jc.constant.Filter;
import www.raven.jc.util.JwtUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * token filter
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@Configuration
@Component
@Slf4j
public class TokenFilter implements GlobalFilter, Ordered {
    @Value("${Raven.key}")
    private String key;
    @Autowired
    private RedissonClient redissonClient;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log(request);
        String path = request.getURI().getPath();
        if(Arrays.stream(Filter.WHITE_PATH).noneMatch(whitePath -> whitePath.equals(path))){
            List<String> tokens = request.getHeaders().get(Filter.TOKEN);
            if(tokens == null || tokens.isEmpty()){
                return exchange.getResponse().setComplete();
            }
            String userId = JwtUtil.verify(tokens.get(0),key);
            Assert.isTrue(Objects.equals(tokens.get(0), redissonClient.getBucket("token:"+userId).get()),"Invalid token");
            log.info("token:"+tokens.get(0));
            ServerHttpRequest realRequest = request.mutate().header("userId", userId).build();
            log.info("userId:"+realRequest.getHeaders().get("userId").get(0));
            return chain.filter(exchange.mutate().request(realRequest).build());
        }
        return chain.filter(exchange);
    }
    public void log(ServerHttpRequest request){
        log.info("出现一次请求");
        // 打印请求方法和URL
        log.info("Request method: " + request.getMethod());
        log.info("Request URL: " + request.getURI());

        // 打印请求头
        HttpHeaders headers = request.getHeaders();
        headers.forEach((headerName, headerValues) -> {
            String headerValuesJoined = String.join(", ", headerValues);
            log.info("Header: " + headerName + " = " + headerValuesJoined);
        });

        // 打印查询参数
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        queryParams.forEach((paramName, paramValues) -> {
            String paramValuesJoined = String.join(", ", paramValues);
            log.info("Query param: " + paramName + " = " + paramValuesJoined);
        });

    }


    @Override
    public int getOrder() {
        return 0;
    }
}
