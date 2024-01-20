package www.raven.jc.service;

import www.raven.jc.dto.UserInfoDTO;
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
     * save msg
     *
     * @param data    data
     * @param message message
     * @param roomId  room id
     */
    void saveRoomMsg(UserInfoDTO data, MessageDTO message, String roomId);

    /**
     * restore history
     *
     * @param roomId room id
     * @return {@link List}<{@link MessageVO}>
     */
    List<MessageVO> restoreHistory(Integer roomId);
}
