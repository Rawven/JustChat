package www.raven.jc.event;

import cn.hutool.core.lang.Assert;
import java.util.ArrayList;
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
import www.raven.jc.constant.MqConstant;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Comment;
import www.raven.jc.entity.po.Like;
import www.raven.jc.entity.po.Moment;
import www.raven.jc.entity.po.Reply;
import www.raven.jc.entity.vo.MomentVO;
import www.raven.jc.event.model.MomentCommentEvent;
import www.raven.jc.event.model.MomentLikeEvent;
import www.raven.jc.event.model.MomentReleaseEvent;
import www.raven.jc.event.model.MomentReplyEvent;
import www.raven.jc.result.RpcResult;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;

import static www.raven.jc.constant.MqConstant.HEADER_TAGS;
import static www.raven.jc.service.impl.SocialServiceImpl.PREFIX;

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
            if (MqConstant.TAGS_MOMENT_INTERNAL_RELEASE_RECORD.equals(tags)) {
                handleMomentEvent(JsonUtil.jsonToObj(msg.getPayload().getData(), MomentReleaseEvent.class));
            } else if (MqConstant.TAGS_MOMENT_INTERNAL_LIKE_RECORD.equals(tags)) {
                handleLikeEvent(JsonUtil.jsonToObj(msg.getPayload().getData(), MomentLikeEvent.class));
            } else if (MqConstant.TAGS_MOMENT_INTERNAL_COMMENT_RECORD.equals(tags)) {
                handleCommentEvent(JsonUtil.jsonToObj(msg.getPayload().getData(), MomentCommentEvent.class));
            } else if(MqConstant.TAGS_MOMENT_INTERNAL_REPLY_NOTICE.equals(tags)) {
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
        updateMomentCache(event.getReleaseId(), new MomentVO(moment));
        streamBridge.send("producer-out-1", MqUtil.createMsg(
            JsonUtil.objToJson(new MomentNoticeEvent().setMomentId(event.getMoment().getMomentId().toHexString()).setUserId(event.getMoment().getUserInfo().getUserId())
                .setMsg("有人发布了朋友圈"))
            , MqConstant.TAGS_MOMENT_NOTICE_MOMENT_FRIEND));
    }

    public void handleLikeEvent(MomentLikeEvent event) {
        MomentVO momentVO = getWannaMoment(event.getMomentId(), event.getMomentUserId());
            List<Like> likes = momentVO.getLikes();
            if (likes == null) {
                likes = new ArrayList<>();
                momentVO.setLikes(likes);
            }
            // 添加新的Like
            likes.add(event.getLike());
            // 添加更新后的MomentVO
        updateMomentCache(event.getMomentUserId(), momentVO);
        streamBridge.send("producer-out-1", MqUtil.createMsg(
            JsonUtil.objToJson(new MomentNoticeEvent().setMomentId(event.getMomentId()).setUserId(event.getMomentUserId()).setMsg("有人点赞你的朋友圈了")),
            MqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT));
    }

    public void handleCommentEvent(MomentCommentEvent event) {
        MomentVO momentVO = getWannaMoment(event.getMomentId(), event.getMomentUserId());
        List<Comment> comments = momentVO.getComments();
        if (comments == null) {
            comments = new ArrayList<>();
            momentVO.setComments(comments);
        }
        // 添加新的Comment
        comments.add(event.getComment());
        updateMomentCache(event.getMomentUserId(), momentVO);
        streamBridge.send("producer-out-1", MqUtil.createMsg(
            JsonUtil.objToJson(new MomentNoticeEvent().setMomentId(event.getMomentId()).setUserId(event.getMomentUserId()).setMsg("有人评论了你的朋友圈")),
            MqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT));
    }

    private void handleReplyEvent(MomentReplyEvent event) {
        MomentVO momentVO = getWannaMoment(event.getMomentId(), event.getMomentUserId());
        List<Comment> comments = momentVO.getComments();
        if (comments == null) {
            comments = new ArrayList<>();
            momentVO.setComments(comments);
        }
        // 添加新的Reply
        comments.stream()
            .filter(comment -> comment.getId().equals(event.getCommentId()))
            .findFirst()
            .ifPresent(comment -> {
                List<Reply> replies = comment.getReplies();
                if (replies == null) {
                    replies = new ArrayList<>();
                    comment.setReplies(replies);
                }
                replies.add(event.getReply());
            });
        updateMomentCache(event.getMomentUserId(), momentVO);
        streamBridge.send("producer-out-1", MqUtil.createMsg(
            JsonUtil.objToJson(new MomentNoticeEvent().setMomentId(event.getMomentId()).setUserId(event.getMomentUserId()).setMsg("有人回复了你的朋友圈")),
            MqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT));
    }



    public void updateMomentCache(Integer userId, MomentVO momentVo) {
        List<RScoredSortedSet<Object>> collect1 = getHisFriendMomentCache(userId);
        collect1.forEach(scoredSortedSet -> {
            // 获取所有的MomentVO
            List<Object> allMoments = new ArrayList<>(scoredSortedSet.valueRange(0, -1));
            // 查找并移除旧的MomentVO
            allMoments.stream()
                .filter(o -> o instanceof MomentVO && ((MomentVO) o).getMomentId().equals(momentVo.getMomentId()))
                .findFirst()
                .ifPresent(scoredSortedSet::remove);
            // 添加新的MomentVO
            scoredSortedSet.add(System.currentTimeMillis(), momentVo);
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
        List<Object> objects = (List<Object>) scoredSortedSet.valueRange(0, -1);
        List<MomentVO> moments = objects.stream().map(o -> (MomentVO) o).collect(Collectors.toList());
        return moments.stream().filter(momentVO -> momentVO.getMomentId().equals(momentId)).findFirst().orElse(null);
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
