package www.raven.jc.service;

import java.util.List;
import org.springframework.scheduling.annotation.Async;
import www.raven.jc.entity.po.Comment;
import www.raven.jc.entity.po.Like;
import www.raven.jc.entity.po.Moment;
import www.raven.jc.entity.po.Reply;
import www.raven.jc.entity.vo.MomentVO;

/**
 * async service
 *
 * @author 刘家辉
 * @date 2024/02/21
 */
public interface TimelineFeedService {

    /**
     * add moment cache
     *
     * @param collect collect
     * @param userId  user id
     */
    @Async("JcThreadPoolTaskExecutor")
     void addMomentTimelineFeeding(List<MomentVO> collect, Integer userId);

    /**
     * handle moment event
     *
     * @param userId user id
     * @param moment moment
     */
    @Async("JcThreadPoolTaskExecutor")
     void insertMomentFeed(Integer userId, Moment moment) ;
}