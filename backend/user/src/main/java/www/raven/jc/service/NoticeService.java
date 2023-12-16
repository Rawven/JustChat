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
