package www.raven.jc.ws;

import javax.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.dto.TokenDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.service.ChatService;
import www.raven.jc.util.JsonUtil;

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
    private ChatService chatService;
    @Autowired
    private UserRpcService userRpcService;

    public static void sendFriendMessage(String message, int userId, int friendId) {
        log.info("----WebSocket 广播消息:" + message);
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
        TokenDTO tokenDTO = (TokenDTO) (session.getUserProperties().get("userDto"));
        UserInfoDTO data = userRpcService.getSingleInfo(tokenDTO.getUserId()).getData();
        sendFriendMessage(JsonUtil.objToJson(message), data.getUserId(), message.getUserInfoDTO().getUserId());
        chatService.saveFriendMsg(message, data, message.getUserInfoDTO().getUserId());
    }
}
