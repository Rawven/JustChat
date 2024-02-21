package www.raven.jc.event;

import cn.hutool.core.lang.Assert;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import www.raven.jc.api.UserDubbo;
import www.raven.jc.constant.RedisSortedConstant;
import www.raven.jc.constant.SocialUserMqConstant;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Comment;
import www.raven.jc.entity.po.Like;
import www.raven.jc.entity.po.Moment;
import www.raven.jc.entity.po.Reply;
import www.raven.jc.entity.vo.MomentVO;
import www.raven.jc.event.model.MomentCommentEvent;
import www.raven.jc.event.model.MomentLikeEvent;
import www.raven.jc.event.model.MomentNoticeEvent;
import www.raven.jc.event.model.MomentReleaseEvent;
import www.raven.jc.event.model.MomentReplyEvent;
import www.raven.jc.result.RpcResult;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;

import static www.raven.jc.constant.MqConstant.HEADER_TAGS;
import static www.raven.jc.constant.RedisSortedConstant.PREFIX;

/**
 * JcEvent listener
 * event listener
 *
 * @author 刘家辉
 * @date 2024/02/06
 */
@Component
@Slf4j
public class SocialEventListener {
    @Autowired
    private UserDubbo userDubbo;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StreamBridge streamBridge;

    @Bean
    public Consumer<Message<Event>> eventToPull() {
        return msg -> {
            //判断是否重复消息
            if (MqUtil.checkMsgIsvalid(msg, redissonClient)) {
                return;
            }
            String tags = Objects.requireNonNull(msg.getHeaders().get(HEADER_TAGS)).toString();
            log.info("--RocketMq 收到消息的类型tag为：{}", tags);
            //判断消息类型
            if (SocialUserMqConstant.TAGS_MOMENT_INTERNAL_RELEASE_RECORD.equals(tags)) {
                handleMomentEvent(JsonUtil.jsonToObj(msg.getPayload().getData(), MomentReleaseEvent.class));
            } else if (SocialUserMqConstant.TAGS_MOMENT_INTERNAL_LIKE_RECORD.equals(tags)) {
                handleLikeEvent(JsonUtil.jsonToObj(msg.getPayload().getData(), MomentLikeEvent.class));
            } else if (SocialUserMqConstant.TAGS_MOMENT_INTERNAL_COMMENT_RECORD.equals(tags)) {
                handleCommentEvent(JsonUtil.jsonToObj(msg.getPayload().getData(), MomentCommentEvent.class));
            } else if(SocialUserMqConstant.TAGS_MOMENT_INTERNAL_REPLY_NOTICE.equals(tags)) {
                handleReplyEvent(JsonUtil.jsonToObj(msg.getPayload().getData(), MomentReplyEvent.class));
            }else {
                log.info("--RocketMq 非法的消息，不处理");
                return;
            }
            MqUtil.protectMsg(msg, redissonClient);
        };
    }

    public void handleMomentEvent(MomentReleaseEvent event) {
        Moment moment = event.getMoment();
        insertOrUpdateMomentCache(event.getReleaseId(), new MomentVO(moment),true);
        streamBridge.send("producer-out-1", MqUtil.createMsg(
            JsonUtil.objToJson(new MomentNoticeEvent().setMomentId(event.getMoment().getMomentId().toHexString()).setUserId(event.getMoment().getUserInfo().getUserId())
                .setMsg("有人发布了朋友圈"))
            , SocialUserMqConstant.TAGS_MOMENT_NOTICE_MOMENT_FRIEND));
    }

    public void handleLikeEvent(MomentLikeEvent event) {
        MomentVO momentVO = getWannaMoment(event.getMomentId(), event.getMomentUserId());
            List<Like> likes = momentVO.getLikes();
            // 添加新的Like
            likes.add(event.getLike());
            // 添加更新后的MomentVO
        insertOrUpdateMomentCache(event.getMomentUserId(), momentVO,false);
        streamBridge.send("producer-out-1", MqUtil.createMsg(
            JsonUtil.objToJson(new MomentNoticeEvent().setMomentId(event.getMomentId()).setUserId(event.getMomentUserId()).setMsg("有人点赞你的朋友圈了")),
            SocialUserMqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT));
    }

    public void handleCommentEvent(MomentCommentEvent event) {
        MomentVO momentVO = getWannaMoment(event.getMomentId(), event.getMomentUserId());
        List<Comment> comments = momentVO.getComments();
        // 添加新的Comment
        comments.add(event.getComment());
        insertOrUpdateMomentCache(event.getMomentUserId(), momentVO,false);
        streamBridge.send("producer-out-1", MqUtil.createMsg(
            JsonUtil.objToJson(new MomentNoticeEvent().setMomentId(event.getMomentId()).setUserId(event.getMomentUserId()).setMsg("有人评论了你的朋友圈")),
            SocialUserMqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT));
    }

    private void handleReplyEvent(MomentReplyEvent event) {
        MomentVO momentVO = getWannaMoment(event.getMomentId(), event.getMomentUserId());
        List<Comment> comments = momentVO.getComments();
        // 添加新的Reply
        comments.stream()
            .filter(comment -> comment.getId().equals(event.getCommentId()))
            .findFirst()
            .ifPresent(comment -> {
                List<Reply> replies = comment.getReplies();
                replies.add(event.getReply());
            });
        insertOrUpdateMomentCache(event.getMomentUserId(), momentVO,false);
        streamBridge.send("producer-out-1", MqUtil.createMsg(
            JsonUtil.objToJson(new MomentNoticeEvent().setMomentId(event.getMomentId()).setUserId(event.getMomentUserId()).setMsg("有人回复了你的朋友圈")),
            SocialUserMqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT));
    }



    public void insertOrUpdateMomentCache(Integer userId, MomentVO momentVo,Boolean insert) {
        List<RScoredSortedSet<Object>> caches = getHisFriendMomentCache(userId);
        if(insert){
            caches.forEach(scoredSortedSet -> {
                if (scoredSortedSet.size() > RedisSortedConstant.MAX_SIZE) {
                    scoredSortedSet.pollFirst();
                }
                scoredSortedSet.add(System.currentTimeMillis(), momentVo);
            });
            return;
        }
        caches.forEach(scoredSortedSet -> {
            // 使用iterator移除旧的MomentVO
            Iterator<Object> iterator = scoredSortedSet.iterator();
            Double time = (double) System.currentTimeMillis();
            while (iterator.hasNext()) {
                Object o = iterator.next();
                if (o instanceof MomentVO && ((MomentVO) o).getMomentId().equals(momentVo.getMomentId())) {
                    time = scoredSortedSet.getScore(o);
                    iterator.remove();
                    break;  // 找到并删除元素后立即退出循环
                }
            }
            // 更新MomentVO
            scoredSortedSet.add(time, momentVo);
        });
    }

    /**
     * get special moment
     * 获取特定的朋友圈
     *
     * @param momentId     moment id
     * @param momentUserId moment user id
     * @return {@link MomentVO}
     */
    private MomentVO getWannaMoment(String momentId,Integer momentUserId) {
        RScoredSortedSet<Object> scoredSortedSet = redissonClient.getScoredSortedSet(PREFIX + momentUserId);
        return scoredSortedSet.stream()
            .filter(o -> o instanceof MomentVO && ((MomentVO) o).getMomentId().equals(momentId))
            .map(o -> (MomentVO) o)
            .findFirst()
            .orElse(null);
    }

    /**
     * get his friend moment cache
     * 获取好友的朋友圈缓存
     *
     * @param userId user id
     * @return {@link List}<{@link RScoredSortedSet}<{@link Object}>>
     */
    private List<RScoredSortedSet<Object>> getHisFriendMomentCache(Integer userId) {
        Assert.isTrue(userDubbo != null, "userDubbo Null");
        RpcResult<List<UserInfoDTO>> friendAndMeInfos = userDubbo.getFriendAndMeInfos(userId);
        Assert.isTrue(friendAndMeInfos.isSuccess(), "获取好友信息失败");
        List<Integer> collect = friendAndMeInfos.getData().stream().map(UserInfoDTO::getUserId).collect(Collectors.toList());
        return collect.stream().map(integer -> redissonClient.getScoredSortedSet(PREFIX + integer)).collect(Collectors.toList());
    }
}
