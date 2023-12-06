package www.raven.jc.websocket;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import www.raven.jc.dto.TokenDTO;
import www.raven.jc.util.JwtUtil;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * notification handler
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
@Component
@Slf4j
@ServerEndpoint("/websocket")
public class NotificationHandler {
    /**
     * 用来存在线连接数
     */
    private static final Map<String, Session> SESSION_POOL = new HashMap<>();
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     * 虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
     */
    private static CopyOnWriteArraySet<NotificationHandler> webSockets = new CopyOnWriteArraySet<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     **/
    private Session session;
    /**
     * user id
     */
    private Integer userId;


    /**
     * 链接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "token") String token, @PathParam(value = "roomId") String roomId) {
        log.info("【websocket消息】有新的连接，token为:" + token);
        TokenDTO verify = JwtUtil.verify(token, "爱你老妈");
        session.getUserProperties().put("userDto", verify);
        this.userId = verify.getUserId();
        this.session = session;
        webSockets.add(this);
        SESSION_POOL.put(token, session);
        log.info("【websocket消息】有新的连接，总数为:" + webSockets.size());
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
        try {

        } catch (Exception e) {
            log.error("map转json异常");
        }
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


    /**
     * send all message
     *
     * @param message message
     */
    public void sendMessageToIds(String message, Map<Integer, Integer> ids) {
        log.info("【websocket消息】广播消息:" + message);
        for (NotificationHandler webSocketService : webSockets) {
            if (webSocketService.session.isOpen() && ids.containsKey(webSocketService.userId)) {
                webSocketService.session.getAsyncRemote().sendText(message);
            }
        }
    }
//
//
//    /**
//     * send one message
//     *
//     * @param token   user id
//     * @param message message
//     */
//    public void sendOneMessage(String token, String message) {
//        Session session = SESSION_POOL.get(token);
//        if (session != null && session.isOpen()) {
//            try {
//                log.info("【websocket消息】 单点消息:" + message);
//                session.getAsyncRemote().sendText(message);
//            } catch (Exception e) {
//                log.error(e.getMessage());
//            }
//        }
//    }
//
//
//    /**
//     * send more message
//     *
//     * @param tokens  user ids
//     * @param message message
//     */
//    public void sendMoreMessage(String[] tokens, String message) {
//        for (String userId : tokens) {
//            Session session = SESSION_POOL.get(userId);
//            if (session != null && session.isOpen()) {
//                log.info("【websocket消息】 单点消息:" + message);
//                session.getAsyncRemote().sendText(message);
//            }
//        }
//    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}