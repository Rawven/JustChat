package www.raven.jc.service.consumer;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import www.raven.jc.constant.NoticeConstant;
import www.raven.jc.dao.NoticeDAO;
import www.raven.jc.dao.UserDAO;
import www.raven.jc.entity.po.Notification;
import www.raven.jc.event.Event;
import www.raven.jc.event.JoinRoomApplyEvent;
import www.raven.jc.event.RoomMsgEvent;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.websocket.NotificationHandler;

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
    private final static long EXPIRE_TIME = 10;
    private static final String TAGS_APPLY = "APPLY";
    private static final String TAGS_RECORD = "RECORD";
    @Autowired
    private NoticeDAO noticeDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private NotificationHandler notificationHandler;

    @Autowired
    private RedissonClient redissonClient;


    @Bean
    public Consumer<Message<Event>> eventChatToUser() {
        return msg -> {
            String tags = Objects.requireNonNull(msg.getHeaders().get("ROCKET_TAGS")).toString();
            //判断消息类型
            if (TAGS_APPLY.equals(tags)) {
                eventUserJoinRoomApply(msg);
            } else if (TAGS_RECORD.equals(tags)) {
                eventUserSendMsg(msg);
            } else {
                log.info("非法的消息，不处理");
            }
        };
    }

    /**
     * 通知用户有人想要入群
     *
     * @param msg msg
     */
    public void eventUserJoinRoomApply(Message<Event> msg) {
        //查看是否已经处理过了
        Object id = msg.getHeaders().get("ROCKET_KEYS");
        if (id == null || checkExist(id)) {
            log.info("重复或非法的消息，不处理");
            return;
        }
        JoinRoomApplyEvent payload = JsonUtil.jsonToObj(msg.getPayload().getData(), JoinRoomApplyEvent.class);
        log.info("receive join room apply event:{}", msg);
        Integer founderId = payload.getFounderId();
        Notification notice = new Notification().setUserId(founderId)
                .setMessage(JsonUtil.objToJson(payload))
                .setType(NoticeConstant.TYPE_JOIN_ROOM_APPLY)
                .setTimestamp(System.currentTimeMillis())
                .setStatus(NoticeConstant.STATUS_UNREAD);
        Assert.isTrue(noticeDAO.save(notice));
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(founderId);
        //TODO 待修改
        sendMsgToUser(ids, "有人申请加入你的聊天室");
        saveIdInCache(id.toString());
    }


    /**
     * 通知在线用户有新消息
     *
     * @param msg msg
     */
    public void eventUserSendMsg(Message<Event> msg) {
        Object id = msg.getHeaders().get("ROCKET_KEYS");
        if (id == null || checkExist(id)) {
            log.info("重复或非法的消息，不处理");
            return;
        }
        log.info("收到消息");
        RoomMsgEvent payload = JsonUtil.jsonToObj(msg.getPayload().getData(), RoomMsgEvent.class);
        Integer userId = payload.getUserId();
        HashMap<Object, Object> map = new HashMap<>(3);
        map.put("roomId", payload.getRoomId());
        map.put("username", userDAO.getBaseMapper().selectById(userId).getUsername());
        map.put("msg", payload.getMsg());
        sendMsgToUser(payload.getIdsFromRoom(), JsonUtil.mapToJson(map));
        saveIdInCache(Objects.requireNonNull(id).toString());
    }

    private Boolean checkExist(Object id) {
        RBucket<Object> bucket = redissonClient.getBucket(id.toString());
        return bucket.isExists();
    }

    private void saveIdInCache(String id) {
        RBucket<Object> bucket = redissonClient.getBucket(id);
        bucket.set(id, EXPIRE_TIME, TimeUnit.MINUTES);
    }

    private void sendMsgToUser(List<Integer> userId, String msg) {
        HashMap<Integer, Integer> map = new HashMap<>(userId.size());
        userId.forEach(id -> map.put(id, 1));
        notificationHandler.sendMessageToIds(msg, map);
    }
}
