package www.raven.jc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import www.raven.jc.result.CommonResult;
import www.raven.jc.util.JsonUtil;

import java.util.Map;

/**
 * default authentication failure handler
 *
 * @author 刘家辉
 * @date 2023/11/25
 */
@Component
@Slf4j
public class DefaultAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        return Mono.defer(() -> Mono.just(webFilterExchange.getExchange()
                .getResponse()).flatMap(response -> {
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            CommonResult<Void> result = CommonResult.operateFailWithMessage("fail");
            // 账号不存在
            if (exception instanceof UsernameNotFoundException) {
            result = CommonResult.operateFailWithMessage("账号不存在");
                // 用户名或密码错误
            } else if (exception instanceof BadCredentialsException) {
                result = CommonResult.operateFailWithMessage("用户名或密码错误");
                // 账号已过期
            } else if (exception instanceof AccountExpiredException) {
                result = CommonResult.operateFailWithMessage("账号已过期");
                // 账号已被锁定
            } else if (exception instanceof LockedException) {
                result = CommonResult.operateFailWithMessage("账号已被锁定");
                // 用户凭证已失效
            } else if (exception instanceof CredentialsExpiredException) {
                result = CommonResult.operateFailWithMessage("用户凭证已失效");
                // 账号已被禁用
            } else if (exception instanceof DisabledException) {
                result = CommonResult.operateFailWithMessage("账号已被禁用");
            }
            DataBuffer dataBuffer = dataBufferFactory.wrap(JsonUtil.objToJson(result).getBytes());
                return response.writeWith(Mono.just(dataBuffer));
        }));
    }
}
