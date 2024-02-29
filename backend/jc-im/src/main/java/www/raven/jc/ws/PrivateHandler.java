package www.raven.jc.ws;

import java.util.Map;
import javax.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.raven.jc.api.UserDubbo;
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
public class PrivateHandler implements BaseHandler  {
    @Autowired
    private  ChatService chatService;
    @Autowired
    private  UserDubbo userDubbo;

    @Override
    public void onMessage(MessageDTO message,Session session) {
        TokenDTO tokenDTO = (TokenDTO) (session.getUserProperties().get("userDto"));
        UserInfoDTO data = userDubbo.getSingleInfo(tokenDTO.getUserId()).getData();
        sendFriendMessage(HandlerUtil.combineMessage(message, data),data.getUserId(), message.getId());
        chatService.saveFriendMsg(message, data, message.getId());
    }

    public static void sendFriendMessage(String message,int userId,int friendId) {
        log.info("----WebSocket 广播消息:" + message);
        Session mySession = WebsocketService.SESSION_POOL.get(userId);
        if (mySession != null && mySession.isOpen()) {
            mySession.getAsyncRemote().sendText(message);
        }
        Map<Integer, Session> integerSessionMap = WebsocketService.FRIEND_SESSION_POOL.get(userId);
            if (integerSessionMap.containsKey(friendId)) {
                Session session = integerSessionMap.get(friendId);
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(message);
                }
            }
    }
}
