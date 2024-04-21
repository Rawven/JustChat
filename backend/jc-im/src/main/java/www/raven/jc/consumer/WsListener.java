package www.raven.jc.consumer;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.raven.jc.constant.ImImMqConstant;
import www.raven.jc.constant.MessageConstant;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;
import www.raven.jc.ws.PrivateHandler;
import www.raven.jc.ws.RoomHandler;

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
        MessageDTO dto = JsonUtil.jsonToObj(message, MessageDTO.class);
        if (Objects.equals(dto.getType(), MessageConstant.FRIEND)) {
            PrivateHandler.sendFriendMessage(dto);
        } else if (Objects.equals(dto.getType(), MessageConstant.ROOM)) {
            RoomHandler.sendRoomMessage(dto);
        } else {
            log.info("--RocketMq 非法的消息，不处理");
        }
        MqUtil.protectMsg(messageExt, redissonClient);
    }

}
