package www.raven.jc.ws;

import javax.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ws schedule
 *
 * @author 刘家辉
 * @date 2024/01/21
 */
@Slf4j
@Component
public class WsSchedule {
    /**
     * 设置过期时间20分钟
     */
    private static final long EXPIRATION_TIME = 20 * 60 * 1000;

    /**
     * 每隔1分钟执行一次检查
     */
    @Scheduled(fixedRate = 60000)
    public void checkRoomWs() {
        long currentTime = System.currentTimeMillis();
        for (WebsocketHandler websocketHandler : WebsocketHandler.webSockets) {
            if ((currentTime - websocketHandler.getLastActivityTime()) > EXPIRATION_TIME) {
                closeRoomExpiredConnection(websocketHandler);
            }
        }
    }

    private void closeRoomExpiredConnection(WebsocketHandler websocket) {
        Session session = websocket.getSession();
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (Exception e) {
                log.error("关闭过期连接失败");
            }
        }
        WebsocketHandler.webSockets.remove(websocket);
        log.info("WebSocket连接过期，用户id为{},总数为:{}", websocket.getUserId(), WebsocketHandler.webSockets.size());
    }
}
