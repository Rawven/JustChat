package www.raven.jc.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * request filter
 *
 * @author 刘家辉
 * @date 2023/12/01
 */
@Slf4j
@Component
public class RequestFilter implements GlobalFilter , Ordered {
    @Override
    public int getOrder() {
        // -2 is response filter, request filter should be called before that
        return -3;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("收到一次请求");
        log.info("Request Method: " + request.getMethodValue());
        log.info("Request URI: " + request.getURI());
        log.info("Request Headers: " + request.getHeaders());
        log.info("Request Query Params: " + request.getQueryParams());
        return chain.filter(exchange);
    }
}
