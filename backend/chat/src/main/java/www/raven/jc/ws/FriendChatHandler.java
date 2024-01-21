package www.raven.jc.ws;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.raven.jc.api.UserDubbo;
import www.raven.jc.dto.TokenDTO;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.service.ChatService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.JwtUtil;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * friend chat handler
 *
 * @author 刘家辉
 * @date 2024/01/21
 */
@EqualsAndHashCode(callSuper = true)
@Component
@Slf4j
@Data
@ServerEndpoint("/ws/friend/{token}/{friendId}")
public class FriendChatHandler extends BaseHandler {


    /**
     * 用来存在线连接数
     */
    private static final Map<Integer, Map<Integer, Session>> SESSION_POOL = new HashMap<>();
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     * 虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
     */
    public static CopyOnWriteArraySet<FriendChatHandler> webSockets = new CopyOnWriteArraySet<>();

    private static UserDubbo userDubbo;

    private static ChatService chatService;
    /**
     * friend id
     */
    private Integer friendId;

    @DubboReference(interfaceClass = UserDubbo.class, version = "1.0.0", timeout = 15000)
    public void setUserDubbo(UserDubbo userDubbo) {
        FriendChatHandler.userDubbo = userDubbo;
    }


    @Autowired
    public void setChatService(ChatService chatService) {
        FriendChatHandler.chatService = chatService;
    }


    /**
     * 链接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "token") String token, @PathParam(value = "friendId") String friendId) {
        TokenDTO dto = JwtUtil.verify(token, "爱你老妈");
        session.getUserProperties().put("userDto", dto);
        this.session = session;
        this.friendId = Integer.valueOf(friendId);
        if (!SESSION_POOL.containsKey(this.userId)) {
            SESSION_POOL.put(this.userId, new HashMap<>());
        }
        Session sessionExisted = SESSION_POOL.get(userId).get(this.friendId);
        if (sessionExisted != null) {
            try {
                sessionExisted.close();
            } catch (Exception e) {
                log.error("----WebSocket 关闭已存在的session失败");
            }
        }
        webSockets.add(this);
        SESSION_POOL.get(userId).put(this.friendId, session);
        log.info("----WebSocket  有新的连接,用户id为{},总数为:{}", dto.getUserId(), webSockets.size());
    }

    /**
     * on close
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSockets.remove(this);
        log.info("----WebSocket 连接断开，总数为:" + webSockets.size());
    }

    /**
     * on message
     * 收到客户端消息后调用的方法
     *
     * @param message message
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("----WebSocket 收到客户端发来的消息:" + message);
        lastActivityTime = System.currentTimeMillis();
        MessageDTO messageDTO = JsonUtil.jsonToObj(message, MessageDTO.class);
        TokenDTO tokenDTO = (TokenDTO) (session.getUserProperties().get("userDto"));
        sendFriendMessage(message);
        chatService.saveFriendMsg(messageDTO, tokenDTO.getUserId(), friendId);
    }

    /**
     * on error 发送错误时的处理
     *
     * @param session session
     * @param error   error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("----WebSocket Error occurred on connection, Session ID: " + session.getId());
        log.error("----WebSocket Details: " + error.getMessage());
        log.error("----WebSocket Stack trace: {}", (Object) error.getStackTrace());
    }


    public void sendFriendMessage(String message) {
        log.info("----WebSocket 广播消息:" + message);
        Session friendSession = SESSION_POOL.get(friendId).get(userId);
        if (friendSession.isOpen()) {
            friendSession.getAsyncRemote().sendText(message);
        }
    }

}
