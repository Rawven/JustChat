package www.raven.jc.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import www.raven.jc.constant.ImImMqConstant;
import www.raven.jc.template.AbstractMqListener;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.ws.WebsocketService;
import www.raven.jc.ws.WsMsg;

/**
 * ws listener
 *
 * @author 刘家辉
 * @date 2024/04/21
 */
@Component
@Slf4j
@RocketMQMessageListener(consumerGroup = "${mq.ws_consumer_group}", topic = "${mq.ws_topic}", selectorExpression = ImImMqConstant.TAGS_SEND_MESSAGE)
public class WsListener extends AbstractMqListener {

    public WsListener(RedissonClient redissonClient) {
        super(redissonClient);
    }

    @Override
    public void onMessage0(String message, String tags) {
        WsMsg wsMsg = JsonUtil.jsonToObj(message, WsMsg.class);
        WebsocketService.sendBatchMessage(wsMsg.getMessage(), wsMsg.getTo());
    }

}
