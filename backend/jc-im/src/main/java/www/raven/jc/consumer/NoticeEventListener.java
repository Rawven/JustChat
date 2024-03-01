package www.raven.jc.consumer;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
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
import www.raven.jc.api.UserRpcService;
import www.raven.jc.constant.ImUserMqConstant;
import www.raven.jc.constant.JwtConstant;
import www.raven.jc.constant.NoticeConstant;
import www.raven.jc.constant.SocialUserMqConstant;
import www.raven.jc.dao.NoticeDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Notice;
import www.raven.jc.event.Event;
import www.raven.jc.event.model.DeleteNoticeEvent;
import www.raven.jc.event.model.FriendMsgEvent;
import www.raven.jc.event.model.MomentNoticeEvent;
import www.raven.jc.event.model.RoomApplyEvent;
import www.raven.jc.event.model.RoomMsgEvent;
import www.raven.jc.result.RpcResult;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;
import www.raven.jc.ws.WebsocketService;

import static www.raven.jc.constant.MqConstant.HEADER_TAGS;

/**
 * message consumer
 *
 * @author 刘家辉
 * @date 2023/12/08
 */
@Service
@Slf4j
public class NoticeEventListener {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private UserRpcService userRpcService;
    @Autowired
    private NoticeDAO noticeDAO;
    @Bean
    public Consumer<Message<Event>> eventUserToIm() {
        return msg -> {
            //判断是否重复消息
            if (MqUtil.checkMsgIsvalid(msg, redissonClient)) {
                return;
            }
            String tags = Objects.requireNonNull(msg.getHeaders().get(HEADER_TAGS)).toString();
            //判断消息类型
            switch (tags) {
                case ImUserMqConstant.TAGS_DELETE_NOTICE:
                     eventDeleteNotice(msg);
                    break;
                default:
                    log.info("--RocketMq 非法的消息，不处理");
            }
            MqUtil.protectMsg(msg, redissonClient);
        };
    }

    @Bean
    public Consumer<Message<Event>> eventFeedToIm() {
        return msg -> {
            //判断是否重复消息
            if (MqUtil.checkMsgIsvalid(msg, redissonClient)) {
                return;
            }
            String tags = Objects.requireNonNull(msg.getHeaders().get(HEADER_TAGS)).toString();
            //判断消息类型
            switch (tags) {
                case SocialUserMqConstant.TAGS_MOMENT_NOTICE_MOMENT_FRIEND:
                    eventMomentNoticeFriendEvent(msg);
                    break;
                case SocialUserMqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT:
                    eventMomentNoticeLikeOrCommentEvent(msg);
                    break;
                default:
                    log.info("--RocketMq 非法的消息，不处理");
            }
            MqUtil.protectMsg(msg, redissonClient);
        };
    }

    @Bean
    public Consumer<Message<Event>> eventImToIm() {
        return msg -> {
            //判断是否重复消息
            if (MqUtil.checkMsgIsvalid(msg, redissonClient)) {
                return;
            }
            String tags = Objects.requireNonNull(msg.getHeaders().get(HEADER_TAGS)).toString();
            //判断消息类型
            switch (tags) {
                case ImUserMqConstant.TAGS_CHAT_ROOM_APPLY:
                    eventUserJoinRoomApply(msg);
                    break;
                default:
                    log.info("--RocketMq 非法的消息，不处理");
            }
            MqUtil.protectMsg(msg, redissonClient);
        };
    }
    private void eventMomentNoticeFriendEvent(Message<Event> msg) {
        MomentNoticeEvent payload = JsonUtil.jsonToObj(msg.getPayload().getData(), MomentNoticeEvent.class);
        Integer userId = payload.getUserId();
        RpcResult<List<UserInfoDTO>> friendInfos = userRpcService.getFriendInfos(userId);
        Assert.isTrue(friendInfos.isSuccess(), "获取好友列表失败");
        HashMap<Object, Object> map = new HashMap<>(3);
        map.put("momentId", payload.getMomentId());
        map.put("msg", payload.getMsg());
        map.put("type", SocialUserMqConstant.TAGS_MOMENT_NOTICE_MOMENT_FRIEND);
        List<Integer> idsFriend = friendInfos.getData().stream().map(UserInfoDTO::getUserId).collect(Collectors.toList());
        WebsocketService.sendBatchMessage(JsonUtil.objToJson(map), idsFriend);
    }

    private void eventMomentNoticeLikeOrCommentEvent(Message<Event> msg) {
        MomentNoticeEvent payload = JsonUtil.jsonToObj(msg.getPayload().getData(), MomentNoticeEvent.class);
        Integer userId = payload.getUserId();
        HashMap<Object, Object> map = new HashMap<>(3);
        map.put("momentId", payload.getMomentId());
        map.put("msg", payload.getMsg());
        map.put("type", SocialUserMqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT);
        WebsocketService.sendOneMessage(userId, JsonUtil.objToJson(map));
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
        Notice notice = new Notice().setUserId(founderId)
            .setData(String.valueOf(payload.getRoomId()))
            .setType(NoticeConstant.TYPE_JOIN_ROOM_APPLY)
            .setTimestamp(System.currentTimeMillis())
            .setSenderId(payload.getApplyId());
        Assert.isTrue(noticeDAO.save(notice), "保存通知失败");
        RBucket<String> founderBucket = redissonClient.getBucket(JwtConstant.TOKEN + founderId);
        if (founderBucket.isExists()) {
            HashMap<Object, Object> map = new HashMap<>(1);
            map.put("type", ImUserMqConstant.TAGS_CHAT_ROOM_APPLY);
            WebsocketService.sendOneMessage(founderId, JsonUtil.objToJson(map));
            log.info("--RocketMq 已推送通知给founder");
        } else {
            log.info("--RocketMq founder不在线");
        }
    }



    private void eventDeleteNotice(Message<Event> msg) {
        DeleteNoticeEvent event = JsonUtil.jsonToObj(msg.getPayload().getData(), DeleteNoticeEvent.class);
        Assert.isTrue(noticeDAO.removeById(event.getNoticeId()), "删除失败");
    }
}
