package www.raven.jc.service;

import www.raven.jc.entity.vo.NoticeVO;

import java.util.List;

/**
 * notice service
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
public interface NoticeService {
    /**
     * load notice
     *
     * @return {@link List}<{@link NoticeVO}>
     */
    List<NoticeVO> loadNotice();

    /**
     * add room apply
     *
     * @param founderId founder id
     * @param payload   payload
     */
     void addRoomApply(int founderId,Object payload);

    /**
     * add friend apply
     *
     * @param friendId friend id
     * @param message  message
     */
    void addFriendApply(Integer friendId,String message);

    /**
     * done notification
     *
     * @param id id
     */
    void doneNotification(Integer id);
    /**
     * delete new room msg notice
     *
     * @param roomId room id
     */
    void deleteNewRoomMsgNotice(Integer roomId);
}
