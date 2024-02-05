package www.raven.jc.service.consumer;

import cn.hutool.core.lang.Assert;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import www.raven.jc.api.UserDubbo;
import www.raven.jc.constant.MqConstant;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Comment;
import www.raven.jc.entity.po.Like;
import www.raven.jc.entity.vo.MomentVO;
import www.raven.jc.event.Event;
import www.raven.jc.event.MomentCommentEvent;
import www.raven.jc.event.MomentLikeEvent;
import www.raven.jc.event.MomentReleaseEvent;
import www.raven.jc.result.RpcResult;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;

import static www.raven.jc.constant.MqConstant.HEADER_TAGS;
import static www.raven.jc.service.impl.SocialServiceImpl.PREFIX;

/**
 * socail mq consumer
 *
 * @author 刘家辉
 * @date 2024/01/25
 */
@Service
@Slf4j
public class SocialMqConsumer {
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 接收信息来用于更新 朋友圈timeline
     *
     * @return {@link Consumer}<{@link Message}<{@link Event}>>
     */
    @Bean
    public Consumer<Message<Event>> eventToPull() {
        return msg -> {
            //判断是否重复消息
            log.info("能否收到信息");
            if (!MqUtil.checkMsgIsvalid(msg, redissonClient)) {
                return;
            }
            String tags = Objects.requireNonNull(msg.getHeaders().get(HEADER_TAGS)).toString();
            log.info(tags);
            //判断消息类型
            if (MqConstant.TAGS_MOMENT_RELEASE_RECORD.equals(tags)) {
                eventReleaseMoment(msg);
            } else if (MqConstant.TAGS_MOMENT_LIKE_RECORD.equals(tags)) {
                log.info("更新点赞缓存");
                eventLikeMoment(msg);
            } else if (MqConstant.TAGS_MOMENT_COMMENT_RECORD.equals(tags)) {
                eventCommentMoment(msg);
            } else {
                log.info("--RocketMq 非法的消息，不处理");
            }
            MqUtil.protectMsg(msg, redissonClient);
        };
    }

    private void eventCommentMoment(Message<Event> msg) {
        MomentCommentEvent event = JsonUtil.jsonToObj(msg.getPayload().getData(), MomentCommentEvent.class);
        Comment comment = JsonUtil.jsonToObj(event.getComment(), Comment.class);
        addCommentCache(event.getMomentUserId(), event.getMomentId(), comment);

        //TODO 通知
    }

    private void eventLikeMoment(Message<Event> msg) {
        MomentLikeEvent event = JsonUtil.jsonToObj(msg.getPayload().getData(), MomentLikeEvent.class);
        addLikeCache(event.getMomentUserId(), event.getMomentId(), JsonUtil.jsonToObj(event.getLike(), Like.class));

        //TODO 通知
    }

    private void eventReleaseMoment(Message<Event> msg) {
        MomentReleaseEvent momentReleaseEvent = JsonUtil.jsonToObj(msg.getPayload().getData(), MomentReleaseEvent.class);
        MomentVO moment = JsonUtil.jsonToObj(momentReleaseEvent.getMoment(), MomentVO.class);
        addMomentCache(momentReleaseEvent.getReleaseId(), moment);

        //TODO 通知
    }

    public void addMomentCache(Integer userId, MomentVO momentVo) {
        List<RScoredSortedSet<Object>> collect1 = getMomentCache(userId);
        collect1.forEach(scoredSortedSet -> scoredSortedSet.add(System.currentTimeMillis(), momentVo));
    }

    public void addLikeCache(Integer momentUserId, String momentId, Like like) {
        //获取所有好友的缓存
        List<RScoredSortedSet<Object>> collect1 = getMomentCache(momentUserId);
        //将like插入到所有好友的moment缓存中
        collect1.forEach(scoredSortedSet -> {
            //获取所有的moment
            List<MomentVO> collect2 = getFriendMoment(scoredSortedSet, momentId);
            if (collect2.size() == 1) {
                MomentVO momentVO = collect2.get(0);
                List<Like> likes = momentVO.getLikes();
                likes.add(like);
                scoredSortedSet.add(System.currentTimeMillis(), momentVO);
            }
        });
    }

    public void addCommentCache(Integer momentUserid, String momentId, Comment comment) {
        List<RScoredSortedSet<Object>> cache = getMomentCache(momentUserid);
        cache.forEach(scoredSortedSet -> {
            //获取所有的moment
            List<MomentVO> collect2 = getFriendMoment(scoredSortedSet, momentId);
            if (collect2.size() == 1) {
                MomentVO momentVO = collect2.get(0);
                List<Comment> comments = momentVO.getComments();
                comments.add(comment);
                scoredSortedSet.add(System.currentTimeMillis(), momentVO);
            }
        });
    }

    private List<MomentVO> getFriendMoment(RScoredSortedSet<Object> scoredSortedSet, String momentId) {
        List<Object> objects = (List<Object>) scoredSortedSet.valueRange(0, -1);
        List<MomentVO> moments = objects.stream().map(o -> JsonUtil.jsonToObj(o.toString(), MomentVO.class)).collect(Collectors.toList());
        return moments.stream().filter(momentVO -> momentVO.getMomentId().equals(momentId)).collect(Collectors.toList());
    }

    private List<RScoredSortedSet<Object>> getMomentCache(Integer userId) {
        Assert.isTrue(userDubbo == null,"userDubbo Null");
        RpcResult<List<UserInfoDTO>> friendAndMeInfos = userDubbo.getFriendAndMeInfos(userId);
        Assert.isTrue(friendAndMeInfos.isSuccess(), "获取好友信息失败");
        List<Integer> collect = friendAndMeInfos.getData().stream().map(UserInfoDTO::getUserId).collect(Collectors.toList());
        return collect.stream().map(integer -> redissonClient.getScoredSortedSet(PREFIX + integer)).collect(Collectors.toList());
    }
}
