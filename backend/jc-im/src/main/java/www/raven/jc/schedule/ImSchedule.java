package www.raven.jc.schedule;

import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import www.raven.jc.constant.OfflineMessagesConstant;
import www.raven.jc.ws.WebsocketService;

import static www.raven.jc.ws.WebsocketService.HEARTBEAT;

/**
 * ws schedule
 *
 * @author 刘家辉
 * @date 2024/01/21
 */
@Slf4j
@Component
public class ImSchedule {
    /**
     * 默认WebSocket连接过期时间为1小时
     */
    private static final long WS_EXPIRATION_TIME = 60 * 60 * 1000;

    /**
     * 默认离线消息过期时间为7天
     */
    private static final long OFFLINE_MESSAGE_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 执行心跳机制
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void checkRoomWs() throws IOException {
        log.info(">>>>>>>>>>> xxl-job--心跳机制运作中");
        CopyOnWriteArraySet<WebsocketService> sockets = WebsocketService.webSockets;
        Map<Session, Integer> map = WebsocketService.HEARTBEAT_MAP;
        //遍历所有的WebSocket连接
        for (WebsocketService socket : sockets) {
            Session session = socket.getSession();
            if (session == null) {
                continue;
            }
            //断开心跳数超过3次的连接
            if (map.get(session) >= 3) {
                session.close();
                WebsocketService.webSockets.remove(socket);
                WebsocketService.HEARTBEAT_MAP.remove(session);
            }
        }
        //发出心跳
        for (WebsocketService socket : sockets) {
            Session session = socket.getSession();
            if (session == null) {
                continue;
            }
            session.getAsyncRemote().sendText(HEARTBEAT);
            map.put(session, map.get(session) + 1);
        }
    }

    /**
     * 定期清理过期的离线消息
     */
    @XxlJob(value = "deleteOfflineMessageHandler")
    public void checkOfflineMessage() {
        log.info(">>>>>>>>>>> xxl-job--清理过期的离线消息");
        long currentTime = System.currentTimeMillis();
        //遍历删除所有用户的过期离线消息
        for (String key : redissonClient.getKeys().getKeysByPattern(OfflineMessagesConstant.PREFIX_MATCH)) {
            //获取用户id
            RScoredSortedSet<String> set = redissonClient.getScoredSortedSet(key);
            //删除七天前的离线消息
            set.removeRangeByScore(0, true, currentTime - OFFLINE_MESSAGE_EXPIRATION_TIME, true);
        }
    }

}
