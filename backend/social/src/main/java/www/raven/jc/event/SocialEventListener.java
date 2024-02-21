package www.raven.jc.event;

import cn.hutool.core.lang.Assert;
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
import www.raven.jc.config.ScoredSortedSetProperty;
import www.raven.jc.constant.SocialUserMqConstant;
import www.raven.jc.dao.MomentDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Comment;
import www.raven.jc.entity.po.Like;
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
import static www.raven.jc.constant.ScoredSortedSetConstant.PREFIX;

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
    private MomentDAO momentDAO;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StreamBridge streamBridge;
    @Autowired
    private ScoredSortedSetProperty setProperty;

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
    private void handleMomentEvent(MomentReleaseEvent event) {
        List<RScoredSortedSet<Object>> list = getHisFriendMomentCache(event.getMoment().getUserInfo().getUserId());
        handleEvent(new MomentVO(event.getMoment()),true,"有人发布了朋友圈",SocialUserMqConstant.TAGS_MOMENT_NOTICE_MOMENT_FRIEND,list);
    }

    private void handleLikeEvent(MomentLikeEvent event) {
        List<RScoredSortedSet<Object>> lists = getCacheNeedUpdate(event.getMomentUserId(), event.getMomentTime());
        if(lists.isEmpty()){
            return;
        }
        MomentVO momentVO = getWannaMoment(lists,event.getMomentTime());
            List<Like> likes = momentVO.getLikes();
            // 添加新的Like
            likes.add(event.getLike());
            // 添加更新后的MomentVO
        handleEvent(momentVO,false,"有人点赞了你的朋友圈",SocialUserMqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT,lists);
    }

    private void handleCommentEvent(MomentCommentEvent event) {
        List<RScoredSortedSet<Object>> lists = getCacheNeedUpdate(event.getMomentUserId(), event.getMomentTime());
        if(lists.isEmpty()){
            return;
        }
        MomentVO momentVO = getWannaMoment(lists,event.getMomentTime());
        momentVO.getComments().add(event.getComment());
        handleEvent(momentVO,false,"有人评论了你的朋友圈",SocialUserMqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT,lists);
    }

    private void handleReplyEvent(MomentReplyEvent event) {
        List<RScoredSortedSet<Object>> lists = getCacheNeedUpdate(event.getMomentUserId(), event.getMomentTime());
        if(lists.isEmpty()){
            return;
        }
        MomentVO momentVO =  getWannaMoment(lists,event.getMomentTime());
        List<Comment> comments = momentVO.getComments();
        // 添加新的Reply
        comments.stream()
            .filter(comment -> comment.getId().equals(event.getCommentId()))
            .findFirst()
            .ifPresent(comment -> {
                List<Reply> replies = comment.getReplies();
                replies.add(event.getReply());
            });
        handleEvent(momentVO,false,"有人回复了你的评论",SocialUserMqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT,lists);
    }

    private void handleEvent(MomentVO momentVo,Boolean insert,String msg,String tag,List<RScoredSortedSet<Object>> caches) {
        insertOrUpdateMomentCache(momentVo,insert,caches);
        streamBridge.send("producer-out-1", MqUtil.createMsg(
            JsonUtil.objToJson(new MomentNoticeEvent().setMomentId(momentVo.getMomentId()).setUserId(momentVo.getUserInfo().getUserId()).setMsg(msg)),
            tag));
    }



    public void insertOrUpdateMomentCache(MomentVO momentVo,Boolean insert,List<RScoredSortedSet<Object>> caches) {
        Long time = momentVo.getTimestamp();
        if(insert){
            caches.forEach(scoredSortedSet -> {
                if (scoredSortedSet.size() > setProperty.maxSize) {
                    scoredSortedSet.pollFirst();
                }
                scoredSortedSet.add(time, momentVo);
            });
        }else {
            caches.forEach(scoredSortedSet -> {
                // 更新MomentVO
                scoredSortedSet.removeRangeByScore(time, true, time, true);
                scoredSortedSet.add(time, momentVo);
            });
        }
    }

    /**
     * get special moment
     * 获取特定的朋友圈
     *
     * @param lists lists
     * @param time  time
     * @return {@link MomentVO}
     */
    private MomentVO getWannaMoment(List<RScoredSortedSet<Object>> lists,Long time) {
        return (MomentVO) lists.get(0).valueRange(time,true,time,true).stream().findFirst().orElse(null);
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

    private  List<RScoredSortedSet<Object>>  getCacheNeedUpdate(Integer userId,long time){
        List<RScoredSortedSet<Object>> list = getHisFriendMomentCache(userId);
        //检查所有有序集合若有分数等于time则将该集合加入一个list里
        return list.stream().filter(scoredSortedSet -> scoredSortedSet.contains(time)).collect(Collectors.toList());
    }
}
