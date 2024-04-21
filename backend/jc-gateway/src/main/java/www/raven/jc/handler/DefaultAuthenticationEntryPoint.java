package www.raven.jc.handler;

import java.nio.charset.Charset;
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

/**
 * default authentication entry point
 * <p>
 * 未认证处理
 *
 * @author 刘家辉
 * @date 2023/11/28
 */
@Component
public class DefaultAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange,
        AuthenticationException ex) {
        return Mono.defer(() -> Mono.just(exchange.getResponse())).flatMap(response -> {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            String result = JsonUtil.objToJson(CommonResult.operateFailure("未认证"));
            DataBuffer buffer = dataBufferFactory.wrap(result.getBytes(
                Charset.defaultCharset()));
            return response.writeWith(Mono.just(buffer));
        });
    }
}