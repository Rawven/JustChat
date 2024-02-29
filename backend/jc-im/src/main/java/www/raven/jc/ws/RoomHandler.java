package www.raven.jc.ws;

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
 * web socket service
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@Slf4j
@Component
public class RoomHandler implements BaseHandler {
    @Autowired
    private  ChatService chatService;

    @Autowired
    private  UserDubbo userDubbo;

    @Override
    public void onMessage(MessageDTO message, Session session) {
        TokenDTO tokenDTO = (TokenDTO) (session.getUserProperties().get("userDto"));
        UserInfoDTO data = userDubbo.getSingleInfo(tokenDTO.getUserId()).getData();
        try {
            //这里直接遍历更快
            sendRoomMessage(HandlerUtil.combineMessage(message, data), message.getId());
        } catch (Exception e) {
            log.error("map转json异常");
        }
        chatService.saveRoomMsg(data.getUserId(), message, message.getId());
    }

    public void sendRoomMessage(String message,Integer roomId) {
        log.info("----WebSocket 广播消息:" + message);
        WebsocketService.GROUP_SESSION_POOL.get(roomId).forEach((k, v) -> {
            if (v.isOpen()) {
                v.getAsyncRemote().sendText(message);
            }
        });
    }

}

