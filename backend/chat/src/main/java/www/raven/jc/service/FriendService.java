package www.raven.jc.service;

import www.raven.jc.entity.vo.MessageVO;
import www.raven.jc.entity.vo.UserFriendVO;

import java.util.List;

/**
 * friend service
 *
 * @author 刘家辉
 * @date 2024/01/21
 */
public interface FriendService {
    /**
     * init user friend page
     *
     * @return {@link List}<{@link UserFriendVO}>
     */
    List<UserFriendVO> initUserFriendPage();

    /**
     * restore friend history
     *
     * @param friendId friend id
     * @return {@link List}<{@link MessageVO}>
     */
    List<MessageVO> restoreFriendHistory(Integer friendId);
}
