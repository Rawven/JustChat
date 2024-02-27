package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
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
import www.raven.jc.service.TimelineFeedService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;

import static www.raven.jc.constant.ScoredSortedSetConstant.PREFIX;

/**
 * async service
 * 朋友圈是feed
 * @author 刘家辉
 * @date 2024/02/18
 */
@Service
@Slf4j
public class TimelineFeedServiceImpl implements TimelineFeedService {

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

    /**
     * add moment timeline feeding
     * 存储用户时间线（仅id）
     * @param collect collect
     * @param userId  user id
     */
    @Override
    public void addMomentTimelineFeeding(List<MomentVO> collect, Integer userId) {
        RScoredSortedSet<String> scoredSortedSet = redissonClient.getScoredSortedSet(ScoredSortedSetConstant.PREFIX + userId);
        scoredSortedSet.expire(Duration.ofDays(setProperty.expireDays));
        collect.forEach(momentVO -> {
            if (scoredSortedSet.size() >= setProperty.maxSize) {
                scoredSortedSet.pollFirst(); // 删除时间最晚的元素
            }
            scoredSortedSet.add(momentVO.getTimestamp(), momentVO.getMomentId());
        });
    }


    @Override
    public void insertMomentFeed(Integer userId, Moment moment) {
        List<RScoredSortedSet<Object>> list = getHisFriendMomentCache(userId);
        list.forEach(scoredSortedSet -> {
            if (scoredSortedSet.size() > setProperty.maxSize) {
                scoredSortedSet.pollFirst();
            }
            scoredSortedSet.add(moment.getTimestamp(), moment.getMomentId());
        });
      }

    /**
     * get his friend moment cache
     * 获取好友的朋友圈缓存
     *
     * @param userId user id
     * @return {@link List}<{@link RScoredSortedSet}<{@link Object}>>
     */
    private List<RScoredSortedSet<Object>> getHisFriendMomentCache(Integer userId) {
        RpcResult<List<UserInfoDTO>> friendAndMeInfos = userDubbo.getFriendAndMeInfos(userId);
        Assert.isTrue(friendAndMeInfos.isSuccess(), "获取好友信息失败");
        List<Integer> collect = friendAndMeInfos.getData().stream().map(UserInfoDTO::getUserId).collect(Collectors.toList());
        return collect.stream().map(integer -> redissonClient.getScoredSortedSet(PREFIX + integer)).collect(Collectors.toList());
    }

    private  List<RScoredSortedSet<Object>>  getCacheNeedUpdate(Integer userId,long time){
        List<RScoredSortedSet<Object>> list = getHisFriendMomentCache(userId);
        if(list.isEmpty()){
            return new ArrayList<>();
        }
        //检查所有有序集合若有分数等于time则将该集合加入一个list里
        List<RScoredSortedSet<Object>> collect = new ArrayList<>();
        list.forEach(scoredSortedSet -> scoredSortedSet.valueRange(time, true, time, true).stream().findFirst().ifPresent(o -> collect.add(scoredSortedSet)));
        if(collect.isEmpty()){
            return new ArrayList<>();
        }
        return collect;
    }
}
