package www.raven.jc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import www.raven.jc.config.JwtProperty;
import www.raven.jc.dto.TokenDTO;
import www.raven.jc.event.SseNotificationHandler;
import www.raven.jc.util.JwtUtil;

/**
 * sse controller
 *
 * @author 刘家辉
 * @date 2024/02/26
 */
@RestController
@RequestMapping("/sse")
public class SseController {
    @Autowired
    private JwtProperty jwtProperty;
    /**
     * 用于创建连接
     */
    @GetMapping("/connect/{token}")
    public SseEmitter connect(@PathVariable String token) {
        TokenDTO dto = JwtUtil.parseToken(token, jwtProperty.key);
        return SseNotificationHandler.connect(dto.getUserId());
    }
}
