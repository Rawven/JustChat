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
import www.raven.jc.entity.vo.MomentVO;
import www.raven.jc.event.model.MomentCommentEvent;
import www.raven.jc.event.model.MomentLikeEvent;
import www.raven.jc.event.model.MomentReleaseEvent;
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
                handleReleaseEvent(JsonUtil.jsonToObj(msg.getPayload().getData(), MomentReleaseEvent.class));
            } else if (MqConstant.TAGS_MOMENT_INTERNAL_LIKE_RECORD.equals(tags)) {
                handleLikeEvent(JsonUtil.jsonToObj(msg.getPayload().getData(), MomentLikeEvent.class));
            } else if (MqConstant.TAGS_MOMENT_INTERNAL_COMMENT_RECORD.equals(tags)) {
                handleCommentEvent(JsonUtil.jsonToObj(msg.getPayload().getData(), MomentCommentEvent.class));
            } else {
                log.info("--RocketMq 非法的消息，不处理");
                return;
            }
            MqUtil.protectMsg(msg, redissonClient);
        };
    }

    public void handleLikeEvent(MomentLikeEvent event) {
        addLikeCache(event.getMomentUserId(), event.getMomentId(), event.getLike());

        streamBridge.send("producer-out-1", MqUtil.createMsg(
            JsonUtil.objToJson(new MomentNoticeEvent().setMomentId(event.getMomentId()).setUserId(event.getMomentUserId()).setMsg("有人点赞你的朋友圈了")),
            MqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT));
    }

    public void handleCommentEvent(MomentCommentEvent event) {
        Comment comment = event.getComment();
        addCommentCache(event.getMomentUserId(), event.getMomentId(), comment);

        streamBridge.send("producer-out-1", MqUtil.createMsg(
            JsonUtil.objToJson(new MomentNoticeEvent().setMomentId(event.getMomentId()).setUserId(event.getMomentUserId()).setMsg("有人评论了你的朋友圈")),
            MqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT));
    }

    public void handleReleaseEvent(MomentReleaseEvent event) {
        Moment moment = event.getMoment();
        addMomentCache(event.getReleaseId(), new MomentVO(moment));
        streamBridge.send("producer-out-1", MqUtil.createMsg(
            JsonUtil.objToJson(new MomentNoticeEvent().setMomentId(event.getMoment().getMomentId().toHexString()).setUserId(event.getMoment().getUserInfo().getUserId())
                .setMsg("有人发布了朋友圈"))
            , MqConstant.TAGS_MOMENT_NOTICE_MOMENT_FRIEND));
    }

    public void addMomentCache(Integer userId, MomentVO momentVo) {
        List<RScoredSortedSet<Object>> collect1 = getMomentCache(userId);
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

    public void addLikeCache(Integer momentUserId, String momentId, Like like) {
        //获取所有好友的缓存
        List<RScoredSortedSet<Object>> collect1 = getMomentCache(momentUserId);
        //将like插入到所有好友的moment缓存中
        collect1.forEach(scoredSortedSet -> {
            //获取所有的moment
            List<MomentVO> collect2 = getFriendMoment(scoredSortedSet, momentId);
            if (collect2.size() == 1) {
                MomentVO momentVO = collect2.get(0);
                // 移除旧的MomentVO
                scoredSortedSet.remove(momentVO);
                List<Like> likes = momentVO.getLikes();
                if (likes == null) {
                    likes = new ArrayList<>();
                    momentVO.setLikes(likes);
                }
                // 添加新的Like
                likes.add(like);
                // 添加更新后的MomentVO
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
                // 移除旧的MomentVO
                scoredSortedSet.remove(momentVO);
                List<Comment> comments = momentVO.getComments();
                if (comments == null) {
                    comments = new ArrayList<>();
                    momentVO.setComments(comments);
                }
                //TODO 嵌套评论缓存
                comments.add(comment);
                // 添加新的Comment
                scoredSortedSet.add(System.currentTimeMillis(), momentVO);
            }
        });
    }

    private List<MomentVO> getFriendMoment(RScoredSortedSet<Object> scoredSortedSet, String momentId) {
        List<Object> objects = (List<Object>) scoredSortedSet.valueRange(0, -1);
        List<MomentVO> moments = objects.stream().map(o -> (MomentVO) o).collect(Collectors.toList());
        return moments.stream().filter(momentVO -> momentVO.getMomentId().equals(momentId)).collect(Collectors.toList());
    }

    private List<RScoredSortedSet<Object>> getMomentCache(Integer userId) {
        Assert.isTrue(userDubbo != null, "userDubbo Null");
        RpcResult<List<UserInfoDTO>> friendAndMeInfos = userDubbo.getFriendAndMeInfos(userId);
        Assert.isTrue(friendAndMeInfos.isSuccess(), "获取好友信息失败");
        List<Integer> collect = friendAndMeInfos.getData().stream().map(UserInfoDTO::getUserId).collect(Collectors.toList());
        return collect.stream().map(integer -> redissonClient.getScoredSortedSet(PREFIX + integer)).collect(Collectors.toList());
    }
}
