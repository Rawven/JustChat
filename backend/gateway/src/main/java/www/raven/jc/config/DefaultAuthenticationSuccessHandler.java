package www.raven.jc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import www.raven.jc.result.CommonResult;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ShiLei
 * @version 1.0.0
 * @date 2021/3/11 15:00
 * @description 登录成功处理
 */
@Component
public class DefaultAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
    @Value("${Raven.key}")
    private String key;


    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        return Mono.defer(() -> Mono.just(webFilterExchange.getExchange().getResponse()).flatMap(response -> {
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            // 生成JWT token
            Map<String, Object> map = new HashMap<>(2);
            SecurityUserDetails userDetails = (SecurityUserDetails) authentication.getPrincipal();
            map.put("userId", userDetails.getUserId());
            map.put("username", userDetails.getUsername());
            map.put("roles",userDetails.getAuthorities());
            String token = JwtUtil.createToken(map,key);
            String refreshToken = JwtUtil.createToken(map,key);
            Map<String, Object> tokenMap = new HashMap<>(2);
            tokenMap.put("token", token);
            tokenMap.put("refreshToken", refreshToken);
            DataBuffer dataBuffer = dataBufferFactory.wrap(JsonUtil.objToJson(CommonResult.operateSuccess("认证成功",tokenMap)).getBytes());
            return response.writeWith(Mono.just(dataBuffer));
        }));
    }
}