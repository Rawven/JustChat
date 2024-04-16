package www.raven.jc.service;

import org.springframework.scheduling.annotation.Async;
import www.raven.jc.entity.po.Moment;

import java.util.List;

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
     * @param userId user id
     */
    @Async
    void buildMomentTimelineFeeding(List<Integer> userIds, Integer userId);

    /**
     * handle moment event
     *
     * @param userId user id
     * @param moment moment
     */
    @Async
    void insertMomentFeed(Integer userId, Moment moment);
}
