package www.raven.jc.event;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
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
import www.raven.jc.entity.po.Friend;
import www.raven.jc.entity.po.Notification;
import www.raven.jc.entity.po.User;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;
import www.raven.jc.ws.NotificationHandler;

import static www.raven.jc.constant.MqConstant.HEADER_TAGS;

/**
 * message consumer
 *
 * @author 刘家辉
 * @date 2023/12/08
 */
@Service
@Slf4j
public class UserEventListener {

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
    public Consumer<Message<Event>> eventSocialToUser
        () {
        return msg -> {
            //判断是否重复消息
            if (MqUtil.checkMsgIsvalid(msg, redissonClient)) {
                return;
            }
            String tags = Objects.requireNonNull(msg.getHeaders().get(HEADER_TAGS)).toString();
            //判断消息类型
            if (MqConstant.TAGS_MOMENT_NOTICE_MOMENT_FRIEND.equals(tags)) {
                eventMomentNoticeFriendEvent(msg);
            } else if (MqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT.equals(tags)) {
                eventMomentNoticeLikeOrCommentEvent(msg);
            } else {
                log.info("--RocketMq 非法的消息，不处理");
            }
            MqUtil.protectMsg(msg, redissonClient);
        };
    }

    private void eventMomentNoticeFriendEvent(Message<Event> msg) {
        MomentNoticeEvent payload = JsonUtil.jsonToObj(msg.getPayload().getData(), MomentNoticeEvent.class);
        Integer userId = payload.getUserId();
        List<Friend> friends = friendDAO.getBaseMapper().selectList(new QueryWrapper<Friend>().eq("user_id", userId));
        HashMap<Object, Object> map = new HashMap<>(3);
        map.put("momentId", payload.getMomentId());
        map.put("msg", payload.getMsg());
        map.put("type", MqConstant.TAGS_MOMENT_NOTICE_MOMENT_FRIEND);
        List<Integer> idsFriend = friends.stream().map(Friend::getFriendId).map(Long::intValue).collect(Collectors.toList());
        notificationHandler.sendBatchMessage(JsonUtil.objToJson(map), idsFriend);
    }

    private void eventMomentNoticeLikeOrCommentEvent(Message<Event> msg) {
        MomentNoticeEvent payload = JsonUtil.jsonToObj(msg.getPayload().getData(), MomentNoticeEvent.class);
        Integer userId = payload.getUserId();
        HashMap<Object, Object> map = new HashMap<>(3);
        map.put("momentId", payload.getMomentId());
        map.put("msg", payload.getMsg());
        map.put("type", MqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT);
        log.info("userId");
        notificationHandler.sendOneMessage(userId,JsonUtil.objToJson(map));
    }




    @Bean
    public Consumer<Message<Event>> eventChatToUser() {
        return msg -> {
            //判断是否重复消息
            if (MqUtil.checkMsgIsvalid(msg, redissonClient)) {
                return;
            }
            String tags = Objects.requireNonNull(msg.getHeaders().get(HEADER_TAGS)).toString();
            //判断消息类型
            if (MqConstant.TAGS_CHAT_ROOM_APPLY.equals(tags)) {
                eventUserJoinRoomApply(msg);
            } else if (MqConstant.TAGS_CHAT_ROOM_MSG_RECORD.equals(tags)) {
                eventRoomSendMsg(msg);
            } else if (MqConstant.TAGS_CHAT_FRIEND_MSG_RECORD.equals(tags)) {
                eventFriendSendMsg(msg);
            } else {
                log.info("--RocketMq 非法的消息，不处理");
            }
            MqUtil.protectMsg(msg, redissonClient);
        };
    }

    private void eventFriendSendMsg(Message<Event> msg) {
        FriendMsgEvent payload = JsonUtil.jsonToObj(msg.getPayload().getData(), FriendMsgEvent.class);
        RBucket<String> receiverBucket = redissonClient.getBucket("token:" + payload.getReceiverId());
        HashMap<Object, Object> map = new HashMap<>(2);
        map.put("receiverId", payload.getReceiverId());
        map.put("senderId", payload.getSenderId());
        map.put("msg", payload.getMsg());
        map.put("type", MqConstant.TAGS_CHAT_FRIEND_MSG_RECORD);
        if (receiverBucket.isExists()) {
            notificationHandler.sendOneMessage(payload.getReceiverId(), JsonUtil.objToJson(map));
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
        RoomApplyEvent payload = JsonUtil.jsonToObj(msg.getPayload().getData(), RoomApplyEvent.class);
        log.info("--RocketMq receive join room apply event:{}", msg);
        Integer founderId = payload.getFounderId();
        Notification notice = new Notification().setUserId(founderId)
            .setData(String.valueOf(payload.getRoomId()))
            .setType(NoticeConstant.TYPE_JOIN_ROOM_APPLY)
            .setTimestamp(System.currentTimeMillis())
            .setSenderId(payload.getApplyId());
        Assert.isTrue(noticeDAO.save(notice));
        RBucket<String> founderBucket = redissonClient.getBucket("token:" + founderId);
        if (founderBucket.isExists()) {
            User applier = userDAO.getBaseMapper().selectById(payload.getApplyId());
            HashMap<Object, Object> map = new HashMap<>(2);
            map.put("roomId", payload.getRoomId());
            map.put("applier", applier.getUsername());
            map.put("type", MqConstant.TAGS_CHAT_ROOM_APPLY);
            notificationHandler.sendOneMessage(founderId, JsonUtil.objToJson(map));
            log.info("--RocketMq 已推送通知给founder");
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
        map.put("type", MqConstant.TAGS_CHAT_ROOM_MSG_RECORD);
        List<Integer> idsFromRoom = payload.getIdsFromRoom();
        notificationHandler.sendBatchMessage(JsonUtil.objToJson(map), idsFromRoom);
    }
}
