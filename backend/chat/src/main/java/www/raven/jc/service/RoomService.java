package www.raven.jc.service;

import www.raven.jc.entity.model.RoomModel;
import www.raven.jc.entity.vo.RoomVO;

import java.util.List;

/**
 * room service
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
public interface RoomService {

    /**
     * create room
     *
     * @param roomModel room model
     */
    void createRoom(RoomModel roomModel);

    /**
     * query room page
     *
     * @param page page
     * @return {@link List}<{@link RoomVO}>
     */
    List<RoomVO> queryRoomPage(Integer page);

    /**
     * query require room list
     *
     * @param text text
     * @return {@link List}<{@link RoomVO}>
     */
    List<RoomVO> queryRequireRoomList(String text);
}
