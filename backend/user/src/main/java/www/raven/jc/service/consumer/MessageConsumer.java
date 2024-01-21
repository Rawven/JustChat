package www.raven.jc.service.consumer;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import www.raven.jc.constant.MqConstant;
import www.raven.jc.dao.FriendDAO;
import www.raven.jc.dao.UserDAO;
import www.raven.jc.entity.po.User;
import www.raven.jc.event.Event;
import www.raven.jc.event.FriendMsgEvent;
import www.raven.jc.event.JoinRoomApplyEvent;
import www.raven.jc.event.RoomMsgEvent;
import www.raven.jc.service.NoticeService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.ws.NotificationHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * message consumer
 *
 * @author 刘家辉
 * @date 2023/12/08
 */
@Service
@Slf4j
public class MessageConsumer {

    private static final String TAGS_ROOM_APPLY = "ROOM_APPLY";
    private static final String TAGS_FRIEND_APPLY = "FRIEND_APPLY";
    private static final String TAGS_ROOM_MSG_RECORD = "ROOM_MSG_RECORD";
    private static final String TAGS_FRIEND_MSG_RECORD = "FRIEND_MSG_RECORD";
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private NotificationHandler notificationHandler;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private FriendDAO friendDAO;


    @Bean
    public Consumer<Message<Event>> eventChatToUser() {
        return msg -> {
            //判断是否重复消息
            Object id = msg.getHeaders().get(MqConstant.HEADER_KEYS);
            if (id == null || redissonClient.getBucket(id.toString()).isExists()) {
                log.info("重复或非法的消息，不处理");
                return;
            }
            String tags = Objects.requireNonNull(msg.getHeaders().get(MqConstant.HEADER_TAGS)).toString();
            //判断消息类型
            if (TAGS_ROOM_APPLY.equals(tags)) {
                eventUserJoinRoomApply(msg);
            } else if (TAGS_FRIEND_APPLY.equals(tags)) {
                eventUserApplyFriendApply(msg);
            } else if (TAGS_ROOM_MSG_RECORD.equals(tags)) {
                eventRoomSendMsg(msg);
            } else if (TAGS_FRIEND_MSG_RECORD.equals(tags)) {
                eventFriendSendMsg(msg);
            } else {
                log.info("非法的消息，不处理");
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
            notificationHandler.sendOneMessage(receiverBucket.get(), JsonUtil.mapToJson(map));
        } else {
            log.info("receiver不在线");
        }
    }

    private void eventUserApplyFriendApply(Message<Event> msg) {
    }

    /**
     * 通知用户有人想要入群
     *
     * @param msg msg
     */
    public void eventUserJoinRoomApply(Message<Event> msg) {
        JoinRoomApplyEvent payload = JsonUtil.jsonToObj(msg.getPayload().getData(), JoinRoomApplyEvent.class);
        log.info("receive join room apply event:{}", msg);
        Integer founderId = payload.getFounderId();
        noticeService.addRoomApply(founderId, payload);
        RBucket<String> founderBucket = redissonClient.getBucket("token:" + founderId);
        User applier = userDAO.getBaseMapper().selectById(payload.getApplyId());
        HashMap<Object, Object> map = new HashMap<>(2);
        map.put("roomId", payload.getRoomId());
        map.put("applier", applier.getUsername());
        if (founderBucket.isExists()) {
            notificationHandler.sendOneMessage(founderBucket.get(), JsonUtil.mapToJson(map));
        } else {
            log.info("founder不在线");
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
        payload.getIdsFromRoom().forEach(id -> {
            RBucket<String> bucket = redissonClient.getBucket("token:" + id);
            if (bucket.isExists()) {
                tokens.add(bucket.get());
            }
        });
        notificationHandler.sendBatchMessage(JsonUtil.mapToJson(map), tokens);
    }
}
