package www.raven.jc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.config.ImProperty;
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
import www.raven.jc.entity.po.Room;
import www.raven.jc.entity.po.UserRoom;
import www.raven.jc.entity.vo.MessageVO;
import www.raven.jc.event.RoomApplyEvent;
import www.raven.jc.event.SaveMsgEvent;
import www.raven.jc.result.RpcResult;
import www.raven.jc.service.MessageService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MessageUtil;
import www.raven.jc.util.MqUtil;
import www.raven.jc.util.RequestUtil;
import www.raven.jc.ws.WebsocketService;

/**
 * chat async service impl
 *
 * @author 刘家辉
 * @date 2024/02/23
 */
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {
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
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private ImProperty imProperty;

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Async
    @Override
    public void saveRoomMsg(MessageDTO message,
        Integer roomId) {

        UserInfoDTO user = userRpcService.getSingleInfo(message.getUserInfo().getUserId()).getData();
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
        MqUtil.sendMsg(rocketMQTemplate, ImImMqConstant.TAGS_SAVE_HISTORY_MSG, imProperty.getInTopic(), JsonUtil.objToJson(new SaveMsgEvent().setMessage(realMsg).setType("room")));
    }

    @Override
    public void saveFriendMsg(MessageDTO message,
        Integer friendId) {

        UserInfoDTO user = userRpcService.getSingleInfo(message.getUserInfo().getUserId()).getData();
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
        MqUtil.sendMsg(rocketMQTemplate, ImImMqConstant.TAGS_SAVE_HISTORY_MSG, imProperty.getInTopic(), JsonUtil.objToJson(new SaveMsgEvent().setMessage(realMsg).setType("friend")));
    }

    @Override
    public List<MessageVO> getLatestOffline() {
        int userId = RequestUtil.getUserId(request);
        RScoredSortedSet<Message> scoredSortedSet = redissonClient.getScoredSortedSet(OfflineMessagesConstant.PREFIX + userId);
        //防止消息过多
        Collection<Message> messages = scoredSortedSet.valueRange(0, 199);
        List<Integer> ids = messages.stream().map(Message::getSenderId).toList();
        RpcResult<List<UserInfoDTO>> batchInfo = userRpcService.getBatchInfo(ids);
        Map<Integer, UserInfoDTO> map = batchInfo.getData().stream().collect(Collectors.toMap(UserInfoDTO::getUserId, Function.identity()));
        //获取所有id=roomId的消息
        List<MessageVO> messageVos = new ArrayList<>();
        for (Message message : messages) {
            UserInfoDTO user = map.get(message.getSenderId());
            messageVos.add(new MessageVO(message, user));
        }
        scoredSortedSet.removeAll(messages);
        return messageVos;
    }

    @Override
    public void sendNotice(Integer roomId, Integer userId) {
        Room room = roomDAO.getBaseMapper().selectById(roomId);
        Integer founderId = room.getFounderId();
        //通知user模块 插入一条申请记录
        MqUtil.sendMsg(rocketMQTemplate, ImImMqConstant.TAGS_CHAT_ROOM_APPLY, imProperty.getInTopic(), JsonUtil.objToJson(new RoomApplyEvent(userId, founderId, roomId)));
    }
}
