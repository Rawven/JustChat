package www.raven.jc.ws;

import jakarta.websocket.Session;
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

    public static void sendFriendMessage(MessageDTO dto) {
        String message = JsonUtil.objToJson(dto);
        int userId = dto.getUserInfo().getUserId();
        int friendId = dto.getBelongId();
        Session mySession = WebsocketService.SESSION_POOL.get(userId);
        if (mySession != null && mySession.isOpen()) {
            mySession.getAsyncRemote().sendText(message);
        }
        Session friendSession = WebsocketService.SESSION_POOL.get(friendId);
        if (friendSession != null && friendSession.isOpen()) {
            friendSession.getAsyncRemote().sendText(message);
        }
    }

    @Override
    public void onMessage(MessageDTO message, Session session) {
        UserInfoDTO data = userRpcService.getSingleInfo(message.getUserInfo().getUserId()).getData();
        String wsInstanceTopic = redissonClient.getBucket("ws:" + message.getUserInfo().getUserId()).get().toString();
        MqUtil.sendMsg(rocketMQTemplate, ImImMqConstant.TAGS_SEND_MESSAGE, wsInstanceTopic, MqUtil.createMsg(JsonUtil.objToJson(message)));
        chatService.saveFriendMsg(message, data, message.getBelongId());
    }
}
