package www.raven.jc.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.Session;


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
        for (RoomChatHandler roomChatHandler : RoomChatHandler.webSockets) {
            if ((currentTime - roomChatHandler.getLastActivityTime()) > EXPIRATION_TIME) {
                closeRoomExpiredConnection(roomChatHandler);
            }
        }
    }

    @Scheduled(fixedRate = 60000)
    public void checkFriendWs() {
        long currentTime = System.currentTimeMillis();
        for (FriendChatHandler friendChatHandler : FriendChatHandler.webSockets) {
            if ((currentTime - friendChatHandler.getLastActivityTime()) > EXPIRATION_TIME) {
                closeFriendExpiredConnection(friendChatHandler);
            }
        }
    }

    private void closeFriendExpiredConnection(FriendChatHandler friendChatHandler) {
        Session session = friendChatHandler.getSession();
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (Exception e) {
                log.error("关闭过期连接失败");
            }
        }
        FriendChatHandler.webSockets.remove(friendChatHandler);
        log.info("WebSocket连接过期，用户id为{},总数为:{}", friendChatHandler.getUserId(), FriendChatHandler.webSockets.size());
    }

    private void closeRoomExpiredConnection(RoomChatHandler roomChatHandler) {
        Session session = roomChatHandler.getSession();
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (Exception e) {
                log.error("关闭过期连接失败");
            }
        }
        RoomChatHandler.webSockets.remove(roomChatHandler);
        log.info("WebSocket连接过期，用户id为{},总数为:{}", roomChatHandler.getUserId(), RoomChatHandler.webSockets.size());
    }
}
