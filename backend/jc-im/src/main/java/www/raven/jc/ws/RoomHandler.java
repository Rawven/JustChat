package www.raven.jc.ws;

import jakarta.websocket.Session;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.constant.ImImMqConstant;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.service.MessageService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;

/**
 * web socket service
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@Slf4j
@Component
public class RoomHandler implements BaseHandler {
    @Autowired
    private MessageService chatService;

    @Autowired
    private UserRpcService userRpcService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RedissonClient redissonClient;

    public static void sendRoomMessage(MessageDTO dto) {
        String message = JsonUtil.objToJson(dto);
        Integer roomId = dto.getBelongId();
        log.info("----WebSocket 广播消息:{}", message);

        Map<Integer, Integer> map = WebsocketService.GROUP_SESSION_POOL.get(roomId);
        if (map != null) {
            map.forEach((k, v) -> {
                Session session = WebsocketService.SESSION_POOL.get(k);
                if (session != null && session.isOpen()) {
                    session.getAsyncRemote().sendText(message);
                }
            });
        }
    }

    @Override
    public void onMessage(MessageDTO message, Session session) {
        UserInfoDTO data = userRpcService.getSingleInfo(message.getUserInfo().getUserId()).getData();
        String wsInstanceTopic = redissonClient.getBucket("ws:" + message.getUserInfo().getUserId()).get().toString();
        MqUtil.sendMsg(rocketMQTemplate, ImImMqConstant.TAGS_SEND_MESSAGE, wsInstanceTopic, MqUtil.createMsg(JsonUtil.objToJson(message)));
        chatService.saveRoomMsg(data, message, message.getBelongId());
    }

}

