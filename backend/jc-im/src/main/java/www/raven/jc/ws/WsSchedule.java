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
        for (WebsocketService websocketService : WebsocketService.webSockets) {
            if ((currentTime - websocketService.getLastActivityTime()) > EXPIRATION_TIME) {
                closeRoomExpiredConnection(websocketService);
            }
        }
    }

    private void closeRoomExpiredConnection(WebsocketService websocket) {
        Session session = websocket.getSession();
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (Exception e) {
                log.error("关闭过期连接失败");
            }
        }
        WebsocketService.webSockets.remove(websocket);
        log.info("WebSocket连接过期，用户id为{},总数为:{}", websocket.getUserId(), WebsocketService.webSockets.size());
    }
}
