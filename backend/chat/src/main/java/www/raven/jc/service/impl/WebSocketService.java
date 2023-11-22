package www.raven.jc.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * web socket service
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@Component
@Slf4j
@ServerEndpoint("/websocket/{userId}")
public class WebSocketService {


    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     **/
    private Session session;

    /**concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    *虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
    */
    private static CopyOnWriteArraySet<WebSocketService> webSockets =new CopyOnWriteArraySet<>();
    /**
     * 用来存在线连接数
     */
    private static final Map<String,Session> sessionPool = new HashMap<String,Session>();

    /**
     * 链接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value="userId")String userId) {
            this.session = session;
            webSockets.add(this);
            sessionPool.put(userId, session);
            log.info("【websocket消息】有新的连接，总数为:"+webSockets.size());
    }

    /**
     * on close
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose() {
            webSockets.remove(this);
            log.info("【websocket消息】连接断开，总数为:"+webSockets.size());
    }

    /**
     * on message
     * 收到客户端消息后调用的方法
     *
     * @param message message
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("【websocket消息】收到客户端消息:"+message);
    }

    /**
     * on error 发送错误时的处理
     *
     * @param session session
     * @param error   error
     */
    @OnError
    public void onError(Session session, Throwable error) {

        log.error("用户错误,原因:"+error.getMessage());
        error.printStackTrace();
    }


    /**
     * send all message
     *
     * @param message message
     */
    public void sendAllMessage(String message) {
        log.info("【websocket消息】广播消息:"+message);
        for(WebSocketService webSocketService : webSockets) {
                if(webSocketService.session.isOpen()) {
                    webSocketService.session.getAsyncRemote().sendText(message);
                }
        }
    }

    /**
     * send one message
     *
     * @param userId  user id
     * @param message message
     */
    public void sendOneMessage(String userId, String message) {
        Session session = sessionPool.get(userId);
        if (session != null&&session.isOpen()) {
            try {
                log.info("【websocket消息】 单点消息:"+message);
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * send more message
     *
     * @param userIds user ids
     * @param message message
     */
    public void sendMoreMessage(String[] userIds, String message) {
        for(String userId:userIds) {
            Session session = sessionPool.get(userId);
            if (session != null&&session.isOpen()) {
                    log.info("【websocket消息】 单点消息:"+message);
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

