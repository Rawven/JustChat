package www.raven.jc.service;

import java.util.List;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.entity.model.LatestGroupMsgModel;
import www.raven.jc.entity.model.PageGroupMsgModel;
import www.raven.jc.entity.vo.MessageVO;

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
     * @param user    user
     */
    void saveRoomMsg(UserInfoDTO user, MessageDTO message, Integer roomId);

    /**
     * save friend msg
     *
     * @param message  message
     * @param friendId friend id
     * @param user     user
     */
    void saveFriendMsg(MessageDTO message, UserInfoDTO user, Integer friendId);


}
