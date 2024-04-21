package www.raven.jc.consumer;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.constant.ImImMqConstant;
import www.raven.jc.constant.ImUserMqConstant;
import www.raven.jc.constant.JwtConstant;
import www.raven.jc.constant.MessageConstant;
import www.raven.jc.constant.NoticeConstant;
import www.raven.jc.constant.SocialUserMqConstant;
import www.raven.jc.dao.FriendChatDAO;
import www.raven.jc.dao.MessageDAO;
import www.raven.jc.dao.NoticeDAO;
import www.raven.jc.dao.RoomDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.FriendChat;
import www.raven.jc.entity.po.Notice;
import www.raven.jc.entity.po.Room;
import www.raven.jc.event.MomentNoticeEvent;
import www.raven.jc.event.RoomApplyEvent;
import www.raven.jc.event.SaveMsgEvent;
import www.raven.jc.event.model.DeleteNoticeEvent;
import www.raven.jc.result.RpcResult;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;
import www.raven.jc.ws.WebsocketService;

/**
 * message consumer
 *
 * @author 刘家辉
 * @date 2023/12/08
 */
@Component
@Slf4j
@RocketMQMessageListener(consumerGroup = "${mq.in_consumer_group}", topic = "${mq.in_topic}")
public class NoticeEventListener implements RocketMQListener<MessageExt> {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private UserRpcService userRpcService;
    @Autowired
    private NoticeDAO noticeDAO;
    @Autowired
    private MessageDAO messageDAO;
    @Autowired
    private RoomDAO roomDAO;
    @Autowired
    private FriendChatDAO friendChatDAO;

    @Override
    public void onMessage(MessageExt messageExt) {
        if (MqUtil.checkMsgIsvalid(messageExt, redissonClient)) {
            return;
        }
        byte[] body = messageExt.getBody();

        String message = new String(body, StandardCharsets.UTF_8);
        log.info("--RocketMq receive event:{}", message);

        switch (messageExt.getTags()) {
            case ImImMqConstant.TAGS_CHAT_ROOM_APPLY:
                eventUserJoinRoomApply(message);
                break;
            case ImImMqConstant.TAGS_SAVE_HISTORY_MSG:
                eventSaveMsg(message);
                break;
            case SocialUserMqConstant.TAGS_MOMENT_NOTICE_MOMENT_FRIEND:
                eventMomentNoticeFriendEvent(message);
                break;
            case SocialUserMqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT:
                eventMomentNoticeLikeOrCommentEvent(message);
                break;
            case ImUserMqConstant.TAGS_DELETE_NOTICE:
                eventDeleteNotice(message);
                break;
            default:
                log.info("--RocketMq 非法的消息，不处理");
        }

        MqUtil.protectMsg(messageExt, redissonClient);
    }

    private void eventSaveMsg(String msg) {
        SaveMsgEvent payload = JsonUtil.jsonToObj(msg, SaveMsgEvent.class);
        //保存进入历史消息db
        messageDAO.getBaseMapper().insert(payload.getMessage());
        if (payload.getMessage().getType().equals(MessageConstant.ROOM)) {
            //更新群聊的最后一条消息
            Assert.isTrue(roomDAO.getBaseMapper().updateById(new Room().setRoomId(Integer.valueOf(payload.getMessage().getReceiverId())).setLastMsgId(payload.getMessage().getId())) > 0, "更新失败");
        } else if (payload.getMessage().getType().equals(MessageConstant.FRIEND)) {
            //更新好友的最后一条消息id
            FriendChat friendChat = friendChatDAO.getBaseMapper().selectOne(new QueryWrapper<FriendChat>().eq("fix_id", payload.getMessage().getReceiverId()));
            Assert.notNull(friendChat, "好友不存在");
            int i = friendChatDAO.getBaseMapper().updateById(friendChat.setLastMsgId(payload.getMessage().getId()));
            Assert.isTrue(i > 0, "更新失败");
        }

    }

    private void eventMomentNoticeFriendEvent(String msg) {
        MomentNoticeEvent payload = JsonUtil.jsonToObj(msg, MomentNoticeEvent.class);
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

    private void eventMomentNoticeLikeOrCommentEvent(String msg) {
        MomentNoticeEvent payload = JsonUtil.jsonToObj(msg, MomentNoticeEvent.class);
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
    public void eventUserJoinRoomApply(String msg) {
        RoomApplyEvent payload = JsonUtil.jsonToObj(msg, RoomApplyEvent.class);
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
            map.put("type", ImImMqConstant.TAGS_CHAT_ROOM_APPLY);
            WebsocketService.sendOneMessage(founderId, JsonUtil.objToJson(map));
            log.info("--RocketMq 已推送通知给founder");
        } else {
            log.info("--RocketMq founder不在线");
        }
    }

    private void eventDeleteNotice(String msg) {
        DeleteNoticeEvent event = JsonUtil.jsonToObj(msg, DeleteNoticeEvent.class);
        Assert.isTrue(noticeDAO.removeById(event.getNoticeId()), "删除失败");
    }

}
