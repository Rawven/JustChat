package www.raven.jc.util;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RedissonClient;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import www.raven.jc.constant.MqConstant;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static www.raven.jc.constant.MqConstant.HEAD;

/**
 * mq util
 *
 * @author 刘家辉
 * @date 2023/12/08
 */
@Slf4j
public class MqUtil {

    public static void sendMsg(RocketMQTemplate rocketMQTemplate, String tag, Message<String> message) {
        rocketMQTemplate.asyncSend("JustChat:" + tag, message, new SendCallback() {
            @Override
            public void onSuccess(org.apache.rocketmq.client.producer.SendResult sendResult) {
                log.info("--rocketMq send notice success");
            }

            @Override
            public void onException(Throwable e) {
                log.error("--rocketMq send notice error", e);
            }
        });
    }

    public static Message<String> createMsg(String data) {
        Map<String, Object> headers = new HashMap<>(1);
        headers.put(MessageConst.PROPERTY_KEYS, IdUtil.getSnowflakeNextIdStr());
        return new GenericMessage<>(
                data, headers);
    }

    public static boolean checkMsgIsvalid(MessageExt msg, RedissonClient redissonClient) {
        Object id = msg.getKeys();
        if (id == null || redissonClient.getBucket(HEAD + id).isExists()) {
            log.info("--RocketMq 重复或非法的消息，不处理");
            return true;
        }
        return false;
    }

    public static void protectMsg(MessageExt msg, RedissonClient redissonClient) {
        Object id = msg.getKeys();
        redissonClient.getBucket(HEAD + id).set(id, MqConstant.EXPIRE_TIME, TimeUnit.MINUTES);
    }
}
