package www.raven.jc.schdule;

import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import www.raven.jc.constant.OfflineMessagesConstant;
import www.raven.jc.ws.WebsocketService;

/**
 * ws schedule
 *
 * @author 刘家辉
 * @date 2024/01/21
 */
@Slf4j
@Component
public class ImSchedule {

    //默认WebSocket连接过期时间为1小时
    private static final long WS_EXPIRATION_TIME = 60 * 60 * 1000;

    //默认离线消息过期时间为7天
    private static final long OFFLINE_MESSAGE_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 定期清理不活跃的WebSocket连接
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void checkRoomWs() {
        long currentTime = System.currentTimeMillis();
        for (WebsocketService websocketService : WebsocketService.webSockets) {
            if ((currentTime - websocketService.getLastActivityTime()) > WS_EXPIRATION_TIME) {
                closeRoomExpiredConnection(websocketService);
            }
        }
    }

    @Scheduled(fixedRate = 60 * 1000 * 60)
    public void checkOfflineMessage() {
        long currentTime = System.currentTimeMillis();
        //遍历删除所有用户的过期离线消息
        for (String key : redissonClient.getKeys().getKeysByPattern(OfflineMessagesConstant.PREFIX_MATCH)) {
            //获取用户id
            RScoredSortedSet<String> set = redissonClient.getScoredSortedSet(key);
            //删除七天前的离线消息
            set.removeRangeByScore(0, true, currentTime - OFFLINE_MESSAGE_EXPIRATION_TIME, true);
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
