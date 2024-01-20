package www.raven.jc.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.raven.jc.dto.TokenDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.api.UserDubbo;
import www.raven.jc.service.ChatService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.JwtUtil;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.List;
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
@ServerEndpoint("/websocket/{token}/{roomId}")
public class ChatHandler {
    /**
     * 用来存在线连接数
     */
    private static final Map<String, Session> SESSION_POOL = new HashMap<>();
    private static UserDubbo userDubbo;
    private static ChatService chatService;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     * 虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
     */
    private static CopyOnWriteArraySet<ChatHandler> webSockets = new CopyOnWriteArraySet<>();
    /**
     * room id
     * 对应是在哪个聊天室
     */
    private String roomId;
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     **/
    private Session session;

    @Autowired
    public void setAccountService(UserDubbo accountDubbo) {
        ChatHandler.userDubbo = accountDubbo;
    }

    @Autowired
    public void setChatService(ChatService chatService) {
        ChatHandler.chatService = chatService;
    }

    /**
     * 链接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "token") String token, @PathParam(value = "roomId") String roomId) {
        TokenDTO dto = JwtUtil.verify(token, "爱你老妈");
        session.getUserProperties().put("userDto",dto);
        this.session = session;
        this.roomId = roomId;
        Session sessionExisted = SESSION_POOL.get(token);
        if (sessionExisted != null) {
            try {
                sessionExisted.close();
            } catch (Exception e) {
                log.error("关闭已存在的session失败");
            }
        }
        webSockets.add(this);
        SESSION_POOL.put(token, session);
        log.info("websocket: 有新的连接,用户id为{},总数为:{}" , dto.getUserId(),webSockets.size());
    }

    /**
     * on close
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSockets.remove(this);
        log.info("【websocket消息】连接断开，总数为:" + webSockets.size());
    }

    /**
     * on message
     * 收到客户端消息后调用的方法
     *
     * @param message message
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("【websocket消息】收到客户端发来的消息:" + message);
        MessageDTO messageDTO = JsonUtil.jsonToObj(message, MessageDTO.class);
        TokenDTO tokenDTO = (TokenDTO) (session.getUserProperties().get("userDto"));
        UserInfoDTO data = userDubbo.getSingleInfo(tokenDTO.getUserId()).getData();
        Map<Object, Object> map = new HashMap<>(2);
        map.put("userInfo", data);
        map.put("message", messageDTO);
        try {
            //这里直接遍历更快
            sendRoomMessage(JsonUtil.mapToJson(map));
        } catch (Exception e) {
            log.error("map转json异常");
        }
        chatService.saveMsg(data, messageDTO, this.roomId);
    }

    /**
     * on error 发送错误时的处理
     *
     * @param session session
     * @param error   error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("Error occurred on connection, Session ID: " + session.getId());
        log.error("Details: " + error.getMessage());
        log.error("Stack trace: {}", (Object) error.getStackTrace());
    }



    public void sendRoomMessage(String message) {
        log.info("【websocket消息】广播消息:" + message);
        for (ChatHandler chatHandler : webSockets) {
            if (chatHandler.session.isOpen() && chatHandler.roomId.equals(this.roomId)) {
                chatHandler.session.getAsyncRemote().sendText(message);
            }
        }
    }

    /**
     * send one message
     *
     * @param token   user id
     * @param message message
     */
    public void sendOneMessage(String token, String message) {
        Session session = SESSION_POOL.get(token);
        if (session != null && session.isOpen()) {
            try {
                log.info("【websocket消息】 单点消息:" + message);
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }


    /**
     * send more message
     *
     * @param tokens  user ids
     * @param message message
     */
    public void sendMoreMessage(List<String> tokens, String message) {
        for (String userId : tokens) {
            Session session = SESSION_POOL.get(userId);
            if (session != null && session.isOpen()) {
                log.info("【websocket消息】 单点消息:" + message);
                session.getAsyncRemote().sendText(message);
            }
        }
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

