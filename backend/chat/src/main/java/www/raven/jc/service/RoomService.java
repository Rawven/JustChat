package www.raven.jc.service;

import www.raven.jc.entity.model.RoomModel;
import www.raven.jc.entity.vo.RealRoomVO;
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
     * @param size size
     * @return {@link List}<{@link RoomVO}>
     */
    RealRoomVO initUserMainPage(Integer page, Integer size);

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
    RealRoomVO queryLikedRoomList(String column, String text, int page);

    /**
     * query username room list*
     *
     * @param column column
     * @param text   text
     * @param page   page
     * @return {@link List}<{@link RoomVO}>
     */
    RealRoomVO queryUserNameRoomList(String column, String text, int page);


    /**
     * apply to join room
     *
     * @param roomId room id
     */
    void applyToJoinRoom(Integer roomId);

    /**
     * agree apply
     *
     * @param roomId room id
     * @param userId user id
     */
    void agreeApply(Integer roomId, Integer userId);


}
