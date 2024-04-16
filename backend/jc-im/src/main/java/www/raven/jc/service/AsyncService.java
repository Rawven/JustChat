package www.raven.jc.service;

import org.springframework.scheduling.annotation.Async;
import www.raven.jc.entity.po.Room;

import java.util.List;

/**
 * chat async service
 *
 * @author 刘家辉
 * @date 2024/02/23
 */
public interface AsyncService {
    /**
     * send message
     *
     * @param roomId room id
     * @param userId user id
     */
    @Async
    void sendNotice(Integer roomId, Integer userId);

    /**
     * update session id
     *
     * @param userId user id
     * @param list   list
     */
    @Async
    void updateRoomMap(Integer userId, List<Room> list);

    /**
     * update session id
     *
     * @param userId    user id
     * @param sessionId session id
     */
    @Async
    void updateFriendMap(Integer userId, String sessionId);
}
