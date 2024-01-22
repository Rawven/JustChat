package www.raven.jc.service;

import www.raven.jc.entity.vo.NoticeVO;
import www.raven.jc.event.JoinRoomApplyEvent;

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
    void addRoomApply(int founderId, JoinRoomApplyEvent payload);

    /**
     * add friend apply
     * add friend apply
     * add friend apply
     *
     * @param friendName friend name
     */
    void addFriendApply(String friendName);

    /**
     * done notification
     *
     * @param id id
     */
    void deleteNotification(Integer id);

}
