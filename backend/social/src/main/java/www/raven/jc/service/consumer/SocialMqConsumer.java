package www.raven.jc.service.consumer;

import cn.hutool.core.lang.Assert;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import www.raven.jc.api.UserDubbo;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.vo.MomentVO;
import www.raven.jc.event.Event;
import www.raven.jc.result.RpcResult;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static www.raven.jc.service.impl.SocialServiceImpl.PREFIX;

/**
 * socail mq consumer
 *
 * @author 刘家辉
 * @date 2024/01/25
 */
@Component
public class SocialMqConsumer {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private UserDubbo userDubbo;
    @Bean
    public Consumer<Message<Event>> eventToPull(){
        return msg->{

        };
    }

    public void addCache(Integer userId,MomentVO momentVo){
        RpcResult<List<UserInfoDTO>> friendAndMeInfos = userDubbo.getFriendAndMeInfos(userId);
        Assert.isTrue(friendAndMeInfos.isSuccess(), "获取好友信息失败");
        List<Integer> collect = friendAndMeInfos.getData().stream().map(UserInfoDTO::getUserId).collect(Collectors.toList());
        List<RScoredSortedSet<Object>> collect1 = collect.stream().map(integer -> redissonClient.getScoredSortedSet(PREFIX + integer)).collect(Collectors.toList());
        collect1.forEach(scoredSortedSet -> {
            scoredSortedSet.add(System.currentTimeMillis(),momentVo);
        });
    }
}
