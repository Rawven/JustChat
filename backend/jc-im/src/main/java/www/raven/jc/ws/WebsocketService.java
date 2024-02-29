package www.raven.jc.ws;

import java.util.HashMap;
import java.util.List;
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
import org.springframework.stereotype.Component;
import www.raven.jc.api.UserDubbo;
import www.raven.jc.dto.TokenDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.JwtUtil;

/**
 * notification handler
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
@Component
@Slf4j
@ServerEndpoint("/ws/{token}")
@Data
public class WebsocketService  {
    /**
     * 用来存在线连接数
     */
    public static final Map<Integer, Session> SESSION_POOL = new HashMap<>();
    /**
     * 用来存私聊关系
     */
    public static final Map<Integer, Map<Integer, Session>> FRIEND_SESSION_POOL = new HashMap<>();
    /**
     * 用来存群聊关系
     */
    public static final Map<Integer, Map<Integer, Session>> GROUP_SESSION_POOL = new HashMap<>();
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     * 虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
     */
    public static CopyOnWriteArraySet<WebsocketService> webSockets = new CopyOnWriteArraySet<>();
    /**
     * user id
     * 对应是哪个用户
     */
    protected Integer userId;

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     **/
    protected Session session;

    protected long lastActivityTime = System.currentTimeMillis();

    private static UserDubbo userDubbo;
    private static PrivateHandler privateHandler;
    private static RoomHandler roomHandler;
    private BaseHandler baseHandler;



    /**
     * 链接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "token") String token) {
        TokenDTO verify = JwtUtil.parseToken(token, "爱你老妈");
        session.getUserProperties().put("userDto", verify);
        this.userId = verify.getUserId();
        this.session = session;
        Session sessionExisted = SESSION_POOL.get(verify.getUserId());
        if (sessionExisted != null) {
            try {
                sessionExisted.close();
            } catch (Exception e) {
                log.error("关闭已存在的session失败");
            }
        }
        webSockets.add(this);
        SESSION_POOL.put(verify.getUserId(), session);
        log.info("ws: 有新的连接,用户id为{},总数为:{}", verify.getUserId(), webSockets.size());
    }

    /**
     * on close
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        Integer userId1 = this.userId;
        webSockets.remove(this);
        log.info("ws:用户id {} 连接断开，总数为:{}", userId1, webSockets.size());
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
        switch (messageDTO.getType()){
            case "friend":
               setBaseHandler(privateHandler);
                break;
            case "room":
                setBaseHandler(roomHandler);
                break;
            default:
                log.error("未知信息");
        }
        this.baseHandler.onMessage(messageDTO,this.session);
    }

    /**
     * on error 发送错误时的处理
     *
     * @param session session
     * @param error   error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("ws:发生错误");
        log.error("Error occurred on connection, Session ID: " + session.getId());
        log.error("Details: " + error.getMessage());
        log.error("Stack trace: {}", (Object) error.getStackTrace());
    }

    /**
     * send all message
     * 遍历方法
     *
     * @param message message
     */
    public void sendAllMessage(String message) {
        log.info("【websocket消息】广播消息:" + message);
        for (WebsocketService handler : webSockets) {
            if (handler.session.isOpen()) {
                handler.session.getAsyncRemote().sendText(message);
            }
        }
    }

    /**
     * send all message
     *
     * @param message message
     */
    public void sendBatchMessage(String message, List<Integer> ids) {
        log.info("ws:广播消息:" + message);
        for (Integer id : ids) {
            Session session = SESSION_POOL.get(id);
            if (session != null && session.isOpen()) {
                try {
                    session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    /**
     * send one message
     * send one message
     *
     * @param message message
     * @param id      id
     */
    public void sendOneMessage(Integer id, String message) {
        Session session = SESSION_POOL.get(id);
        if (session != null && session.isOpen()) {
            try {
                log.info("【websocket消息】 单点消息:" + message);
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                log.error(e.getMessage());
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