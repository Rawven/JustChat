package www.raven.jc.ws;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.raven.jc.api.UserDubbo;
import www.raven.jc.dto.TokenDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.service.ChatService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.JwtUtil;

/**
 * friend chat handler
 *
 * @author 刘家辉
 * @date 2024/01/21
 */
@Slf4j
@Component
public class PrivateHandler  {
    private static UserDubbo userDubbo;

    private static ChatService chatService;



    public void onMessage(MessageDTO messageDTO,UserInfoDTO data,Integer friendId) {
        sendFriendMessage(HandlerUtil.combineMessage(messageDTO, data),data.getUserId(),friendId);
        chatService.saveFriendMsg(messageDTO, data.getUserId(), friendId);
    }

    public void sendFriendMessage(String message,int userId,int friendId) {
        log.info("----WebSocket 广播消息:" + message);
        Session mySession = WebsocketHandler.SESSION_POOL.get(userId);
        if (mySession != null && mySession.isOpen()) {
            mySession.getAsyncRemote().sendText(message);
        }
        Map<Integer, Session> integerSessionMap = WebsocketHandler.FRIEND_SESSION_POOL.get(userId);
            if (integerSessionMap.containsKey(friendId)) {
                Session session = integerSessionMap.get(friendId);
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(message);
                }
            }
    }
}
