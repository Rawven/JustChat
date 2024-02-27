package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.api.UserDubbo;
import www.raven.jc.constant.ApplyStatusConstant;
import www.raven.jc.constant.ChatUserMqConstant;
import www.raven.jc.constant.MessageConstant;
import www.raven.jc.constant.OfflineMessagesConstant;
import www.raven.jc.dao.FriendChatDAO;
import www.raven.jc.dao.MessageDAO;
import www.raven.jc.dao.RoomDAO;
import www.raven.jc.dao.UserRoomDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.entity.model.LatestGroupMsgModel;
import www.raven.jc.entity.model.PageGroupMsgModel;
import www.raven.jc.entity.po.FriendChat;
import www.raven.jc.entity.po.Message;
import www.raven.jc.entity.po.Room;
import www.raven.jc.entity.po.UserRoom;
import www.raven.jc.entity.vo.MessageVO;
import www.raven.jc.event.model.FriendMsgEvent;
import www.raven.jc.event.model.RoomMsgEvent;
import www.raven.jc.result.RpcResult;
import www.raven.jc.service.ChatService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MongoUtil;
import www.raven.jc.util.MqUtil;
import www.raven.jc.ws.RoomChatHandler;

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
    private UserDubbo userDubbo;
    @Autowired
    private StreamBridge streamBridge;
    @Autowired
    private RedissonClient redissonClient;


    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void saveRoomMsg(Integer userId, MessageDTO message, Integer roomId) {
        long timeStamp = message.getTime();
        String text = message.getText();
        Message realMsg = new Message().setContent(text)
            .setTimestamp(new Date(timeStamp))
            .setSenderId(userId)
            .setType(MessageConstant.ROOM)
            .setReceiverId(String.valueOf(roomId));
        List<Integer> userIds = userRoomDAO.getBaseMapper().selectList(
            new QueryWrapper<UserRoom>().eq("room_id", roomId).
                eq("status", ApplyStatusConstant.APPLY_STATUS_AGREE)).
            stream().map(UserRoom::getUserId).collect(Collectors.toList());
        //离线消息

        Map<Integer, Map<Integer, Session>> pool = RoomChatHandler.SESSION_POOL;

        RScoredSortedSet<Object> scoredSortedSet = redissonClient.getScoredSortedSet(OfflineMessagesConstant.PREFIX + userId);
        scoredSortedSet.expire(OfflineMessagesConstant.EXPIRE_DAYS);
        scoredSortedSet.add(message.getTime(), JsonUtil.objToJson(realMsg));

        //保存进入历史消息db
        messageDAO.getBaseMapper().save(realMsg);
        //更新聊天室的最后一条消息
        Assert.isTrue(roomDAO.getBaseMapper().updateById(new Room().setRoomId(roomId).setLastMsgId(realMsg.getMessageId().toString())) > 0, "更新失败");
        RoomMsgEvent roomMsgEvent = new RoomMsgEvent(userId, roomId, userIds, JsonUtil.objToJson(realMsg));
        //通知user模块有新消息
        streamBridge.send("producer-out-0", MqUtil.createMsg(JsonUtil.objToJson(roomMsgEvent), ChatUserMqConstant.TAGS_CHAT_ROOM_MSG_RECORD));
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void saveFriendMsg(MessageDTO message, Integer userId, Integer friendId) {
        String fixId = MongoUtil.concatenateIds(userId, friendId);
        Message realMsg = new Message().setContent(message.getText())
            .setTimestamp(new Date(message.getTime()))
            .setSenderId(userId)
            .setType(MessageConstant.FRIEND)
            .setReceiverId(fixId);
        //保存消息
       messageDAO.getBaseMapper().save(realMsg);
        //保存最后一条消息
        FriendChat friendChat = new FriendChat().setFixId(fixId)
            .setLastMsgId(realMsg.getMessageId().toString());
        Assert.isTrue(friendChatDAO.save(friendChat), "插入失败");
        FriendMsgEvent friendMsgEvent = new FriendMsgEvent(userId, friendId, JsonUtil.objToJson(realMsg));
        //通知user模块有新消息
        streamBridge.send("producer-out-0", MqUtil.createMsg(JsonUtil.objToJson(friendMsgEvent), ChatUserMqConstant.TAGS_CHAT_FRIEND_MSG_RECORD));
    }

    @Override
    public List<MessageVO> getLatestGroupMsg(LatestGroupMsgModel model) {
        return null;
    }

    @Override
    public List<MessageVO> getGroupMsgPages(PageGroupMsgModel model) {
        PageRequest pageRequest = PageRequest.of(model.getPage(), model.getSize());
        List<Message> messages = new ArrayList<>(messageDAO.getMsgWithPagination(String.valueOf(model.getRoomId()),"room", pageRequest).getContent());
        //给messages排序 从小到大
        messages.sort(Comparator.comparingLong(o -> o.getTimestamp().getTime()));
        List<Integer> userIds = messages.stream().map(Message::getSenderId).collect(Collectors.toList());
        RpcResult<List<UserInfoDTO>> allInfo = userDubbo.getBatchInfo(userIds);
        Assert.isTrue(allInfo.isSuccess(), "user模块调用失败");
        List<UserInfoDTO> data = allInfo.getData();
        Map<Integer, UserInfoDTO> userInfoMap = data.stream()
            .collect(Collectors.toMap(UserInfoDTO::getUserId, Function.identity()));
        return messages.stream().map(
            message -> {
                MessageVO messageVO = new MessageVO();
                messageVO.setText(message.getContent());
                messageVO.setTime(message.getTimestamp());
                // 从 Map 中查找对应的 UserInfoDTO 对象
                UserInfoDTO userInfoDTO = userInfoMap.get(message.getSenderId());
                if (userInfoDTO != null) {
                    messageVO.setUser(userInfoDTO.getUsername());
                    messageVO.setProfile(userInfoDTO.getProfile());
                }
                return messageVO;
            }
        ).collect(Collectors.toList());
    }

}
