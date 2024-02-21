package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import www.raven.jc.api.UserDubbo;
import www.raven.jc.config.ScoredSortedSetProperty;
import www.raven.jc.constant.ScoredSortedSetConstant;
import www.raven.jc.constant.SocialUserMqConstant;
import www.raven.jc.dao.MomentDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Comment;
import www.raven.jc.entity.po.Like;
import www.raven.jc.entity.po.Moment;
import www.raven.jc.entity.po.Reply;
import www.raven.jc.entity.vo.MomentVO;
import www.raven.jc.event.model.MomentNoticeEvent;
import www.raven.jc.result.RpcResult;
import www.raven.jc.service.CacheAsyncService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;

import static www.raven.jc.constant.ScoredSortedSetConstant.PREFIX;

/**
 * async service
 *
 * @author 刘家辉
 * @date 2024/02/18
 */
@Service
public class CacheAsyncServiceImpl implements CacheAsyncService {

    @Autowired
    private ScoredSortedSetProperty setProperty;
    @Autowired
    private UserDubbo userDubbo;
    @Autowired
    private MomentDAO momentDAO;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StreamBridge streamBridge;


    @Override
    public void addMomentCache(List<MomentVO> collect, Integer userId) {
        RScoredSortedSet<MomentVO> scoredSortedSet = redissonClient.getScoredSortedSet(ScoredSortedSetConstant.PREFIX + userId);
        scoredSortedSet.expire(Duration.ofDays(setProperty.expireDays));
        collect.forEach(momentVO -> {
            if (scoredSortedSet.size() >= setProperty.maxSize) {
                scoredSortedSet.pollFirst(); // 删除分数最低的元素
            }
            scoredSortedSet.add(momentVO.getTimestamp(), momentVO);
        });
    }


    @Override
    public void updateMomentCacheAndNotice(Integer userId, Moment moment) {
        List<RScoredSortedSet<Object>> list = getHisFriendMomentCache(userId);
        handleEvent(new MomentVO(moment),true,"有人发布了朋友圈", SocialUserMqConstant.TAGS_MOMENT_NOTICE_MOMENT_FRIEND,list);
    }
    @Override
    public void updateLikeCacheAndNotice(Integer userId,Long momentTime,Like like) {
        List<RScoredSortedSet<Object>> lists = getCacheNeedUpdate(userId, momentTime);
        if(lists.isEmpty()){
            return;
        }
        MomentVO momentVO = getWannaMoment(lists,momentTime);
        List<Like> likes = momentVO.getLikes();
        // 添加新的Like
        likes.add(like);
        // 添加更新后的MomentVO
        handleEvent(momentVO,false,"有人点赞了你的朋友圈",SocialUserMqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT,lists);
    }

    @Override
    public void updateCommentCacheAndNotice(Integer userId,Long momentTime,Comment comment) {
        List<RScoredSortedSet<Object>> lists = getCacheNeedUpdate(userId, momentTime);
        if(lists.isEmpty()){
            return;
        }
        MomentVO momentVO = getWannaMoment(lists,momentTime);
        momentVO.getComments().add(comment);
        handleEvent(momentVO,false,"有人评论了你的朋友圈",SocialUserMqConstant.TAGS_MOMENT_NOTICE_WITH_LIKE_OR_COMMENT,lists);
    }
    @Async
    @Override
    public void updateReplyCacheAndNotice(Integer userId,Long momentTime,Reply reply) {
        List<RScoredSortedSet<Object>> lists = getCacheNeedUpdate(userId, momentTime);
        if(lists.isEmpty()){
            return;
        }
        MomentVO momentVO =  getWannaMoment(lists,momentTime);
        List<Comment> comments = momentVO.getComments();
        // 添加新的Reply
        comments.stream()
            .filter(comment -> comment.getId().equals(reply.getParentId()))
            .findFirst()
            .ifPresent(comment -> {
                List<Reply> replies = comment.getReplies();
                replies.add(reply);
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
