package www.raven.jc.service;

import www.raven.jc.entity.model.RoomModel;
import www.raven.jc.entity.vo.AllRoomVO;
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
    List<RoomVO> queryAllRoomPage(Integer page);

    /**
     * query liked room list
     * query liked room list
     * query require room list
     *
     * @param text   text
     * @param column column
     * @param page   page
     * @return {@link List}<{@link RoomVO}>
     */
    List<RoomVO> queryLikedRoomList(String column, String text, int page);

    /**
     * query username room list*
     *
     * @param column column
     * @param text   text
     * @param page   page
     * @return {@link List}<{@link RoomVO}>
     */
    List<RoomVO> queryUserNameRoomList(String column, String text, int page);


}
