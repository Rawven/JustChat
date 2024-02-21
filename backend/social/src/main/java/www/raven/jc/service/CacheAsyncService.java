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
public interface CacheAsyncService {

    /**
     * add moment cache
     *
     * @param collect collect
     * @param userId  user id
     */
    @Async("JcThreadPoolTaskExecutor")
     void addMomentCache(List<MomentVO> collect, Integer userId);

    /**
     * handle moment event
     *
     * @param userId user id
     * @param moment moment
     */
    @Async("JcThreadPoolTaskExecutor")
     void updateMomentCacheAndNotice(Integer userId, Moment moment) ;

    /**
     * handle like event
     *
     * @param userId     user id
     * @param momentTime moment time
     * @param like       like
     */
    @Async("JcThreadPoolTaskExecutor")
     void updateLikeCacheAndNotice(Integer userId,Long momentTime, Like like);

    /**
     * handle comment event
     *
     * @param userId     user id
     * @param momentTime moment time
     * @param comment    comment
     */
    @Async("JcThreadPoolTaskExecutor")
     void updateCommentCacheAndNotice(Integer userId,Long momentTime, Comment comment);

    /**
     * handle reply event
     *
     * @param userId     user id
     * @param momentTime moment time
     * @param reply      reply
     */
    @Async("JcThreadPoolTaskExecutor")
     void updateReplyCacheAndNotice(Integer userId,Long momentTime, Reply reply) ;
}
