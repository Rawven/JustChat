package www.raven.jc.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.Session;

import static www.raven.jc.websocket.ChatHandler.webSockets;

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
     *设置过期时间30分钟
      */
    private static final long EXPIRATION_TIME = 30* 60 * 1000;

    /**每隔1分钟执行一次检查
     */
    @Scheduled(fixedRate = 60000)
    public void checkExpiredConnections() {
        long currentTime = System.currentTimeMillis();
        for (ChatHandler chatHandler : webSockets) {
            if ((currentTime - chatHandler.getLastActivityTime()) > EXPIRATION_TIME) {
                closeExpiredConnection(chatHandler);
            }
        }
    }

    private void closeExpiredConnection(ChatHandler chatHandler) {
        Session session = chatHandler.getSession();
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (Exception e) {
                log.error("关闭过期连接失败");
            }
        }
        webSockets.remove(chatHandler);
        log.info("WebSocket连接过期，用户id为{},总数为:{}", chatHandler.getUserId(), webSockets.size());
    }
}
