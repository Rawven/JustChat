package www.raven.jc.service.impl;

import java.util.List;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import www.raven.jc.constant.RedisSortedConstant;
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

    @Async
    public void addMomentCache(List<MomentVO> collect, Integer userId) {

        RScoredSortedSet<MomentVO> scoredSortedSet = redissonClient.getScoredSortedSet(RedisSortedConstant.PREFIX + userId);
        collect.forEach(momentVO -> scoredSortedSet.add(momentVO.getTimestamp(), momentVO));
    }
}
