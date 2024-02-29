package www.raven.jc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import www.raven.jc.constant.ChatUserMqConstant;
import www.raven.jc.dao.RoomDAO;
import www.raven.jc.entity.po.Room;
import www.raven.jc.event.model.RoomApplyEvent;
import www.raven.jc.service.AsyncService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;

/**
 * chat async service impl
 *
 * @author 刘家辉
 * @date 2024/02/23
 */
@Service
public class AsyncServiceImpl implements AsyncService {
    @Autowired
    private RoomDAO roomDAO;
    @Autowired
    private StreamBridge streamBridge;

    @Override
    public void sendNotice(Integer roomId, Integer userId) {
        Room room = roomDAO.getBaseMapper().selectById(roomId);
        Integer founderId = room.getFounderId();
        //通知user模块 插入一条申请记录
        streamBridge.send("producer-out-1", MqUtil.createMsg(JsonUtil.objToJson(new RoomApplyEvent(userId, founderId, roomId)), ChatUserMqConstant.TAGS_CHAT_ROOM_APPLY));
    }

    @Override
    public void updateRoomMap(Integer userId, String sessionId) {

    }

    @Override
    public void updateFriendMap(Integer userId, String sessionId) {

    }
}
