package www.raven.jc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.constant.ApplyStatusConstant;
import www.raven.jc.constant.ImImMqConstant;
import www.raven.jc.constant.MessageConstant;
import www.raven.jc.constant.OfflineMessagesConstant;
import www.raven.jc.dao.FriendChatDAO;
import www.raven.jc.dao.MessageDAO;
import www.raven.jc.dao.RoomDAO;
import www.raven.jc.dao.UserRoomDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.entity.po.Message;
import www.raven.jc.entity.po.UserRoom;
import www.raven.jc.event.SaveMsgEvent;
import www.raven.jc.service.ChatService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MessageUtil;
import www.raven.jc.util.MqUtil;
import www.raven.jc.ws.WebsocketService;

import java.util.Date;
import java.util.List;

/**
 * chat service impl
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {


    @Autowired
    private MessageDAO messageDAO;
    @Autowired
    private UserRoomDAO userRoomDAO;
    @Autowired
    private RoomDAO roomDAO;
    @Autowired
    private FriendChatDAO friendChatDAO;
    @Autowired
    private UserRpcService userRpcService;
    @Autowired
    private StreamBridge streamBridge;
    @Autowired
    private RedissonClient redissonClient;

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void saveRoomMsg(UserInfoDTO user, MessageDTO message, Integer roomId) {
        long timeStamp = message.getTime();
        String text = message.getText();
        Message realMsg = new Message()
                .setContent(text)
                .setTimestamp(new Date(timeStamp))
                .setSenderId(user.getUserId())
                .setType(MessageConstant.ROOM)
                .setReceiverId(String.valueOf(roomId));
        List<Integer> userIds = userRoomDAO.getBaseMapper().selectList(
                        new QueryWrapper<UserRoom>().eq("room_id", roomId).
                                eq("status", ApplyStatusConstant.APPLY_STATUS_AGREE)).
                stream().map(UserRoom::getUserId).toList();

        //对离线用户进行离线信息保存
        userIds.forEach(
                id -> {
                    if (WebsocketService.SESSION_POOL.get(id) == null || !WebsocketService.SESSION_POOL.get(id).isOpen()) {
                        RScoredSortedSet<Object> scoredSortedSet = redissonClient.getScoredSortedSet(OfflineMessagesConstant.PREFIX + id.toString());
                        log.info("离线消息保存:{}", JsonUtil.objToJson(realMsg));
                        scoredSortedSet.add(timeStamp, realMsg);
                    }
                }
        );
        //异步入历史消息数据库
        streamBridge.send("producer-out-1", MqUtil.createMsg(JsonUtil.objToJson(new SaveMsgEvent().setMessage(realMsg).setType("room")), ImImMqConstant.TAGS_SAVE_HISTORY_MSG));
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void saveFriendMsg(MessageDTO message, UserInfoDTO user, Integer friendId) {
        String fixId = MessageUtil.concatenateIds(user.getUserId(), friendId);
        Message realMsg = new Message().setContent(message.getText())
                .setTimestamp(new Date(message.getTime()))
                .setSenderId(user.getUserId())
                .setType(MessageConstant.FRIEND)
                .setReceiverId(fixId);

        //对离线用户进行离线信息保存
        if (WebsocketService.SESSION_POOL.get(friendId) == null || !WebsocketService.SESSION_POOL.get(friendId).isOpen()) {
            RScoredSortedSet<Object> scoredSortedSet = redissonClient.getScoredSortedSet(OfflineMessagesConstant.PREFIX + user.getUserId());
            scoredSortedSet.add(message.getTime(), realMsg);
        }
        //异步入历史消息库
        streamBridge.send("producer-out-1", MqUtil.createMsg(JsonUtil.objToJson(new SaveMsgEvent().setMessage(realMsg).setType("friend")), ImImMqConstant.TAGS_SAVE_HISTORY_MSG));
    }

}
