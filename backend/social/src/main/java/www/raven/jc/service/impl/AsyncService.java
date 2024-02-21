package www.raven.jc.service.impl;

import java.time.Duration;
import java.util.List;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import www.raven.jc.config.ScoredSortedSetProperty;
import www.raven.jc.constant.ScoredSortedSetConstant;
import www.raven.jc.entity.vo.MomentVO;

/**
 * async service
 *
 * @author 刘家辉
 * @date 2024/02/18
 */
@Service
public class AsyncService {

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private ScoredSortedSetProperty setProperty;

    @Async
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
}
