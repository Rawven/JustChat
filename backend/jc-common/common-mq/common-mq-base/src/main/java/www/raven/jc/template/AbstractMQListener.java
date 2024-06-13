package www.raven.jc.template;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RedissonClient;
import www.raven.jc.util.MqUtil;

/**
 * 通用MQListener模板
 *
 * @author 刘家辉
 * @date 2024/06/01
 */
@Slf4j
public abstract class AbstractMQListener implements RocketMQListener<MessageExt> {
    protected final RedissonClient redissonClient;

    public AbstractMQListener(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void onMessage(MessageExt messageExt) {
        if (MqUtil.checkMsgValid(messageExt, redissonClient)) {
            return;
        }
        byte[] body = messageExt.getBody();
        String message = new String(body, StandardCharsets.UTF_8);
        log.info("--RocketMq receive event:{}", message);
        onMessage0(message, messageExt.getTags());
        MqUtil.protectMsg(messageExt, redissonClient);
    }

    /**
     * 实际的消息处理逻辑
     */
    public abstract void onMessage0(String message1, String tags);
}
