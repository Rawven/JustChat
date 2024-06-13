package www.raven.jc.ws;

import jakarta.websocket.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RedissonClient;
import www.raven.jc.constant.ImImMqConstant;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;

/**
 * base handler
 *
 * @author 刘家辉
 * @date 2024/01/21
 */

public interface BaseHandler {
    /**
     * on message
     *
     * @param message message
     * @param session session
     */
    void onMessage(MessageDTO message, Session session);

    /**
     * send
     *
     * @param redissonClient   redisson client
     * @param ids              ids
     * @param message          message
     * @param rocketMQTemplate rocket mqtemplate
     */
    default void broadcast(RedissonClient redissonClient, List<Integer> ids,
        MessageDTO message,
        RocketMQTemplate rocketMQTemplate) {
        Map<String, List<Integer>> map = new HashMap<>();
        for (Integer id : ids) {
            String wsTopic = redissonClient.getBucket("ws:" + id).get().toString();
            List<Integer> idList = map.computeIfAbsent(wsTopic, k -> new ArrayList<>());
            idList.add(id);
        }
        for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
            MqUtil.sendMsg(rocketMQTemplate, ImImMqConstant.TAGS_SEND_MESSAGE, entry.getKey(),
                new WsMsg().setMessage(JsonUtil.objToJson(message)).setTo(entry.getValue()));
        }
    }
}
