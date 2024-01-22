package www.raven.jc.service.consumer;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import www.raven.jc.constant.MqConstant;
import www.raven.jc.constant.NoticeConstant;
import www.raven.jc.dao.FriendDAO;
import www.raven.jc.dao.NoticeDAO;
import www.raven.jc.dao.UserDAO;
import www.raven.jc.entity.po.Notification;
import www.raven.jc.entity.po.User;
import www.raven.jc.event.Event;
import www.raven.jc.event.FriendMsgEvent;
import www.raven.jc.event.JoinRoomApplyEvent;
import www.raven.jc.event.RoomMsgEvent;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.ws.NotificationHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static www.raven.jc.constant.MqConstant.*;

/**
 * message consumer
 *
 * @author 刘家辉
 * @date 2023/12/08
 */
@Service
@Slf4j
public class MessageConsumer {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private NotificationHandler notificationHandler;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private FriendDAO friendDAO;
    @Autowired
    private NoticeDAO noticeDAO;

    @Bean
    public Consumer<Message<Event>> eventChatToUser() {
        return msg -> {
            //判断是否重复消息
            Object id = msg.getHeaders().get(MqConstant.HEADER_KEYS);
            if (id == null || redissonClient.getBucket(id.toString()).isExists()) {
                log.info("--RocketMq 重复或非法的消息，不处理");
                return;
            }
            String tags = Objects.requireNonNull(msg.getHeaders().get(MqConstant.HEADER_TAGS)).toString();
            //判断消息类型
            if (TAGS_ROOM_APPLY.equals(tags)) {
                eventUserJoinRoomApply(msg);
            } else if (MqConstant.TAGS_ROOM_MSG_RECORD.equals(tags)) {
                eventRoomSendMsg(msg);
            } else if (MqConstant.TAGS_FRIEND_MSG_RECORD.equals(tags)) {
                eventFriendSendMsg(msg);
            } else {
                log.info("--RocketMq 非法的消息，不处理");
            }
            redissonClient.getBucket(id.toString()).set(id, MqConstant.EXPIRE_TIME, TimeUnit.MINUTES);
        };
    }

    private void eventFriendSendMsg(Message<Event> msg) {
        FriendMsgEvent payload = JsonUtil.jsonToObj(msg.getPayload().getData(), FriendMsgEvent.class);
        RBucket<String> receiverBucket = redissonClient.getBucket("token:" + payload.getReceiverId());
        HashMap<Object, Object> map = new HashMap<>(2);
        map.put("receiverId", payload.getReceiverId());
        map.put("senderId", payload.getSenderId());
        map.put("msg", payload.getMsg());
        if (receiverBucket.isExists()) {
            notificationHandler.sendOneMessage(payload.getReceiverId(), JsonUtil.mapToJson(map));
        } else {
            log.info("--RocketMq receiver不在线");
        }
    }

    /**
     * 通知用户有人想要入群
     *
     * @param msg msg
     */
    public void eventUserJoinRoomApply(Message<Event> msg) {
        JoinRoomApplyEvent payload = JsonUtil.jsonToObj(msg.getPayload().getData(), JoinRoomApplyEvent.class);
        log.info("--RocketMq receive join room apply event:{}", msg);
        Integer founderId = payload.getFounderId();
        Notification notice = new Notification().setUserId(founderId)
                .setData(String.valueOf(payload.getRoomId()))
                .setType(NoticeConstant.TYPE_JOIN_ROOM_APPLY)
                .setTimestamp(System.currentTimeMillis())
                .setSenderId(payload.getApplyId());
        Assert.isTrue(noticeDAO.save(notice));
        RBucket<String> founderBucket = redissonClient.getBucket("token:" + founderId);
        User applier = userDAO.getBaseMapper().selectById(payload.getApplyId());
        HashMap<Object, Object> map = new HashMap<>(2);
        map.put("roomId", payload.getRoomId());
        map.put("applier", applier.getUsername());
        if (founderBucket.isExists()) {
            notificationHandler.sendOneMessage(founderId, JsonUtil.mapToJson(map));
        } else {
            log.info("--RocketMq founder不在线");
        }
    }

    /**
     * 通知在线用户有新消息
     *
     * @param msg msg
     */
    public void eventRoomSendMsg(Message<Event> msg) {
        RoomMsgEvent payload = JsonUtil.jsonToObj(msg.getPayload().getData(), RoomMsgEvent.class);
        Integer userId = payload.getUserId();
        HashMap<Object, Object> map = new HashMap<>(3);
        map.put("roomId", payload.getRoomId());
        map.put("username", userDAO.getBaseMapper().selectById(userId).getUsername());
        map.put("msg", payload.getMsg());
        List<String> tokens = new ArrayList<>();
        List<Integer> idsFromRoom = payload.getIdsFromRoom();
        notificationHandler.sendBatchMessage(JsonUtil.mapToJson(map), idsFromRoom);
    }
}
