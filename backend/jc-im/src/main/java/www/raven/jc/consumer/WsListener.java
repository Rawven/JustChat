package www.raven.jc.consumer;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.raven.jc.constant.ImImMqConstant;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;
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
public class WsListener implements RocketMQListener<MessageExt> {
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void onMessage(MessageExt messageExt) {
        if (MqUtil.checkMsgValid(messageExt, redissonClient)) {
            return;
        }
        byte[] body = messageExt.getBody();
        String message = new String(body, StandardCharsets.UTF_8);
        log.info("--RocketMq receive event:{}", message);
        WsMsg wsMsg = JsonUtil.jsonToObj(message, WsMsg.class);
        WebsocketService.sendBatchMessage(wsMsg.getMessage(), wsMsg.getTo());
        MqUtil.protectMsg(messageExt, redissonClient);
    }

}
