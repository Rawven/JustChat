package www.raven.jc.service;

import java.util.List;
import org.springframework.scheduling.annotation.Async;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.entity.po.MessageAck;
import www.raven.jc.entity.vo.MessageVO;

/**
 * chat async service
 *
 * @author 刘家辉
 * @date 2024/02/23
 */
public interface MessageService {
    /**
     * send message
     *
     * @param roomId room id
     * @param userId user id
     */
    @Async
    void sendNotice(Integer roomId, Integer userId);

    /**
     * save room msg
     * save msg
     *
     * @param message message
     * @param roomId  room id
     */
    @Async
    void saveRoomMsg(MessageDTO message, Integer roomId);

    /**
     * save friend msg
     *
     * @param message  message
     * @param friendId friend id
     */
    @Async
    void saveFriendMsg(MessageDTO message, Integer friendId);

    /**
     * get latest offline
     *
     * @return list
     */
    List<MessageVO> getLatestOffline();

    /**
     * get message ack
     *
     * @return {@link List }<{@link MessageAck }>
     */
    List<MessageAck> getDoneMessageAck();

}
