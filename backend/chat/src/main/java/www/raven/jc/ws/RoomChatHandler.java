package www.raven.jc.ws;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.raven.jc.api.UserDubbo;
import www.raven.jc.dto.TokenDTO;
import www.raven.jc.dto.UserInfoDTO;
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
 * web socket service
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@Component
@Slf4j
@Data
@ServerEndpoint("/ws/room/{token}/{roomId}")
public class RoomChatHandler extends BaseHandler {

    /**
     * 用来存在线连接数
     */
    private static final Map<Integer, Map<Integer, Session>> SESSION_POOL = new HashMap<>();
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     * 虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
     */
    public static CopyOnWriteArraySet<RoomChatHandler> webSockets = new CopyOnWriteArraySet<>();

    private static UserDubbo userDubbo;
    private static ChatService chatService;
    /**
     * room id
     * 对应是在哪个聊天室
     */
    private Integer roomId;

    @DubboReference(interfaceClass = UserDubbo.class, version = "1.0.0", timeout = 15000)
    public void setUserDubbo(UserDubbo userDubbo) {
        RoomChatHandler.userDubbo = userDubbo;
    }
    @Autowired
    public void setChatService(ChatService chatService) {
        RoomChatHandler.chatService = chatService;
    }

    /**
     * 链接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "token") String token, @PathParam(value = "roomId") String roomId) {
        TokenDTO dto = JwtUtil.verify(token, "爱你老妈");
        session.getUserProperties().put("userDto", dto);
        this.session = session;
        this.roomId = Integer.valueOf(roomId);
        this.userId = dto.getUserId();
        if (!SESSION_POOL.containsKey(this.roomId)) {
            SESSION_POOL.put(this.roomId, new HashMap<>());
        }
        Session sessionExisted = SESSION_POOL.get(this.roomId).get(this.userId);
        if (sessionExisted != null) {
            try {
                sessionExisted.close();
            } catch (Exception e) {
                log.error("关闭已存在的session失败");
            }
        }
        webSockets.add(this);
        SESSION_POOL.get(this.roomId).put(dto.getUserId(), session);
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
        log.info("----WebSocket收到客户端发来的消息:" + message);
        lastActivityTime = System.currentTimeMillis();
        MessageDTO messageDTO = JsonUtil.jsonToObj(message, MessageDTO.class);
        TokenDTO tokenDTO = (TokenDTO) (session.getUserProperties().get("userDto"));
        UserInfoDTO data = userDubbo.getSingleInfo(tokenDTO.getUserId()).getData();
        try {
            //这里直接遍历更快
            sendRoomMessage(HandlerUtil.combineMessage(messageDTO, data));
        } catch (Exception e) {
            log.error("map转json异常");
        }
        chatService.saveRoomMsg(data.getUserId(), messageDTO, this.roomId);
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


    public void sendRoomMessage(String message) {
        log.info("----WebSocket 广播消息:" + message);
        SESSION_POOL.get(this.roomId).forEach((k, v) -> {
            if ( v.isOpen()) {
                v.getAsyncRemote().sendText(message);
            }
        });
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}

