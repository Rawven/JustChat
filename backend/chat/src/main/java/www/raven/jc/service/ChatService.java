package www.raven.jc.service;

import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.entity.vo.MessageVO;

import java.util.List;

/**
 * chat service
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
public interface ChatService {

    /**
     * save room msg
     * save msg
     *
     * @param message message
     * @param roomId  room id
     * @param userId  user id
     */
    void saveRoomMsg(Integer userId, MessageDTO message, Integer roomId);

    /**
     * restore history
     *
     * @param roomId room id
     * @return {@link List}<{@link MessageVO}>
     */
    List<MessageVO> restoreRoomHistory(Integer roomId);

    /**
     * save friend msg
     *
     * @param message  message
     * @param userId   user id
     * @param friendId friend id
     */
    void saveFriendMsg(MessageDTO message,Integer userId,Integer friendId);
}
