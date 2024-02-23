package www.raven.jc.service;

import org.springframework.scheduling.annotation.Async;

/**
 * chat async service
 *
 * @author 刘家辉
 * @date 2024/02/23
 */
public interface ChatAsyncService {
    /**
     * send message
     *
     * @param roomId room id
     * @param userId user id
     */
    @Async("JcThreadPoolTaskExecutor")
    void sendNotice(Integer roomId,Integer userId);
}
