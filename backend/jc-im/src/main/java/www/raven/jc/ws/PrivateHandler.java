package www.raven.jc.ws;


import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.raven.jc.api.UserRpcService;
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
        sendFriendMessage(JsonUtil.objToJson(message), data.getUserId(), message.getBelongId());
        chatService.saveFriendMsg(message, data, message.getBelongId());
    }
}
