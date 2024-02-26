package www.raven.jc.event;

import com.alibaba.nacos.common.http.param.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * sse util
 *
 * @author 刘家辉
 * @date 2024/02/26
 */
@Slf4j
public class SseNotificationHandler {
    /**
     * 当前连接数
     */
    private static AtomicInteger count = new AtomicInteger(0);

    /**
     * 使用map对象，便于根据userId来获取对应的SseEmitter，或者放redis里面
     */
    private static Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    /**
     * 创建用户连接并返回 SseEmitter
     *
     * @param id id
     * @return SseEmitter
     */
    public static SseEmitter connect(Integer id) {
        String userId = id.toString();
        if(sseEmitterMap.containsKey(userId)){
            log.info("--SSE 用户[{}]已存在连接", userId);
            return sseEmitterMap.get(userId);
        }
        // 设置超时时间，0表示不过期。默认30秒，超过时间未完成会抛出异常：AsyncRequestTimeoutException
        SseEmitter sseEmitter = new SseEmitter(0L);
        // 注册回调
        sseEmitter.onCompletion(completionCallBack(userId));
        sseEmitter.onError(errorCallBack(userId));
        sseEmitter.onTimeout(timeoutCallBack(userId));
        sseEmitterMap.put(userId, sseEmitter);
        // 数量+1
        count.getAndIncrement();
        log.info("--SSE 创建新的sse连接，当前用户：{}", userId);
        return sseEmitter;
    }

    /**
     * 给指定用户发送信息
     */
    public static void sendMessage(Integer userId, String message) {
        sendBaseMessage(userId.toString(), message);
    }
    private static void sendBaseMessage(String id, String message) {
        if (sseEmitterMap.containsKey(id)) {
            try {
                // sseEmitterMap.get(userId).send(message, MediaType.APPLICATION_JSON);
                sseEmitterMap.get(id).send(message);
            }
            catch (IOException e) {
                log.error("--SSE 用户[{}]推送异常:{}", id, e.getMessage());
                removeUser(id);
            }
        }
    }

    /**
     * 群发消息
     */
    public static void sendBatchMessage(String wsInfo, List<Integer> intIds) {
        // 转化为字符串
        List<String> userIds =  intIds.stream().map(String::valueOf).collect(Collectors.toList());
        userIds.forEach(userId -> sendBaseMessage(userId,wsInfo));
    }

    /**
     * 群发所有人
     */
    public static void sendAllMessage(String wsInfo) {
        sseEmitterMap.forEach((k, v) -> {
            try {
                v.send(wsInfo, org.springframework.http.MediaType.valueOf(MediaType.APPLICATION_JSON));
            }
            catch (IOException e) {
                log.error("--SSE 用户[{}]推送异常:{}", k, e.getMessage());
                removeUser(k);
            }
        });
    }

    /**
     * 移除用户连接
     */
    public static void removeUser(String userId) {
        sseEmitterMap.remove(userId);
        // 数量-1
        count.getAndDecrement();
        log.info("--SSE 移除用户：{}", userId);
    }

    /**
     * 获取当前连接信息
     */
    public static List<String> getIds() {
        return new ArrayList<>(sseEmitterMap.keySet());
    }

    /**
     * 获取当前连接数量
     */
    public static int getUserCount() {
        return count.intValue();
    }

    private static Runnable completionCallBack(String userId) {
        return () -> {
            log.info("--SSE 结束连接：{}", userId);
            removeUser(userId);
        };
    }

    private static Runnable timeoutCallBack(String userId) {
        return () -> {
            log.info("--SSE 连接超时：{}", userId);
            removeUser(userId);
        };
    }

    private static Consumer<Throwable> errorCallBack(String userId) {
        return throwable -> {
            log.info("--SSE 连接异常：{}", userId);
            removeUser(userId);
        };
    }
}
