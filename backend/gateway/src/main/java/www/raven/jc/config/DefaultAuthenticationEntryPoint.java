package www.raven.jc.config;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import www.raven.jc.result.CommonResult;
import www.raven.jc.util.JsonUtil;

import static java.nio.charset.Charset.*;

/**
 * default authentication entry point
 *
 * @author 刘家辉
 * @date 2023/11/25
 */
@Component
public class DefaultAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        return Mono.defer(() -> Mono.just(exchange.getResponse())).flatMap(response -> {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            String result = JsonUtil.objToJson(CommonResult.operateFailWithMessage("认证失败"));
            DataBuffer buffer = dataBufferFactory.wrap(result.getBytes(
                    defaultCharset()));
            return response.writeWith(Mono.just(buffer));
        });
    }
}