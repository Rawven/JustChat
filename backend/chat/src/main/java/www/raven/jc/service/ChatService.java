package www.raven.jc.service;

import www.raven.jc.dto.UserInfoDTO;
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
     * save msg
     *
     * @param data    data
     * @param message message
     * @throws Exception exception
     */
    void saveMsg(UserInfoDTO data, String message) throws Exception;

    /**
     * restore history
     *
     * @return {@link List}<{@link MessageVO}>
     */
    List<MessageVO> restoreHistory();
}
