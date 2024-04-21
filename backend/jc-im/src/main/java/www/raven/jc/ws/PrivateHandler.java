package www.raven.jc.ws;

import jakarta.websocket.Session;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.service.MessageService;

/**
 * friend chat handler
 *
 * @author 刘家辉
 * @date 2024/01/21
 */
@Slf4j
@Component
public class PrivateHandler implements BaseHandler {
    @Autowired
    private MessageService chatService;
    @Autowired
    private UserRpcService userRpcService;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void onMessage(MessageDTO message, Session session) {
        List<Integer> ids = List.of(message.getBelongId(), message.getUserInfo().getUserId());
        send(redissonClient, ids, message, rocketMQTemplate);
        chatService.saveFriendMsg(message, message.getBelongId());
    }
}
