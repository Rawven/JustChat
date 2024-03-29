package www.raven.jc.ws;

import java.util.Map;
import javax.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.dto.TokenDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.service.ChatService;

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
    private ChatService chatService;

    @Autowired
    private UserRpcService userRpcService;

    public static void sendRoomMessage(String message, Integer roomId) {
        log.info("----WebSocket 广播消息:" + message);

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
        TokenDTO tokenDTO = (TokenDTO) (session.getUserProperties().get("userDto"));
        UserInfoDTO data = userRpcService.getSingleInfo(tokenDTO.getUserId()).getData();
        try {
            //这里直接遍历更快
            sendRoomMessage(HandlerUtil.combineMessage(message, data), message.getBelongId());
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
        chatService.saveRoomMsg(data, message, message.getBelongId());
    }

}

