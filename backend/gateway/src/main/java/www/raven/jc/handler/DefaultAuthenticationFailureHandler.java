package www.raven.jc.handler;

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

/**
 * @author ShiLei
 * @version 1.0.0
 * @date 2021/3/11 15:14
 * @description 登录失败处理
 */
@Component
public class DefaultAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        return Mono.defer(() -> Mono.just(webFilterExchange.getExchange()
                .getResponse()).flatMap(response -> {
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            CommonResult<Void> resultVO = CommonResult.operateFailWithMessage("登录失败");
            // 账号不存在
            if (exception instanceof UsernameNotFoundException) {
                resultVO = CommonResult.operateFailWithMessage("账号不存在");
                // 用户名或密码错误
            } else if (exception instanceof BadCredentialsException) {
                resultVO = CommonResult.operateFailWithMessage("用户名或密码错误");
                // 账号已过期
            } else if (exception instanceof AccountExpiredException) {
                resultVO = CommonResult.operateFailWithMessage("账号已过期");
                // 账号已被锁定
            } else if (exception instanceof LockedException) {
                resultVO = CommonResult.operateFailWithMessage("账号已被锁定");
                // 用户凭证已失效
            } else if (exception instanceof CredentialsExpiredException) {
                resultVO = CommonResult.operateFailWithMessage("用户凭证已失效");
                // 账号已被禁用
            } else if (exception instanceof DisabledException) {
                resultVO = CommonResult.operateFailWithMessage("账号已被禁用");
            }
            DataBuffer dataBuffer = dataBufferFactory.wrap(JsonUtil.objToJson(resultVO).getBytes());
            return response.writeWith(Mono.just(dataBuffer));
        }));
    }
}