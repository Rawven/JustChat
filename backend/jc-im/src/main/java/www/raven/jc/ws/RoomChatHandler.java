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
 * web socket service
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@Slf4j
@Component
public class RoomChatHandler extends BaseHandler {

    private static UserDubbo userDubbo;
    private static ChatService chatService;

    @Autowired
    public void setUserDubbo(UserDubbo userDubbo) {
        RoomChatHandler.userDubbo = userDubbo;
    }

    @Autowired
    public void setChatService(ChatService chatService) {
        RoomChatHandler.chatService = chatService;
    }


    public void onMessage(MessageDTO messageDTO,UserInfoDTO data,Integer roomId) {
        try {
            //这里直接遍历更快
            sendRoomMessage(HandlerUtil.combineMessage(messageDTO, data),roomId);
        } catch (Exception e) {
            log.error("map转json异常");
        }
        chatService.saveRoomMsg(data.getUserId(), messageDTO, roomId);
    }

    public void sendRoomMessage(String message,Integer roomId) {
        log.info("----WebSocket 广播消息:" + message);
        WebsocketHandler.GROUP_SESSION_POOL.get(roomId).forEach((k, v) -> {
            if (v.isOpen()) {
                v.getAsyncRemote().sendText(message);
            }
        });
    }

}

