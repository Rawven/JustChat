package www.raven.jc.service;

import www.raven.jc.entity.vo.UserFriendVO;

import java.util.List;

/**
 * friend service
 *
 * @author 刘家辉
 * @date 2024/01/21
 */
public interface FriendService {
    //TODO work
    void getFriendMsg();


    /**
     * init user friend page
     *
     * @return {@link List}<{@link UserFriendVO}>
     */
    List<UserFriendVO> initUserFriendPage();

}
