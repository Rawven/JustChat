package www.raven.jc.util;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.redisson.api.RedissonClient;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import www.raven.jc.constant.MqConstant;
import www.raven.jc.event.Event;

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

    public static Message<Event> createMsg(String data, String tag) {
        Map<String, Object> headers = new HashMap<>(2);
        headers.put(MessageConst.PROPERTY_KEYS, IdUtil.getSnowflakeNextIdStr());
        headers.put(MessageConst.PROPERTY_TAGS, tag);
        return new GenericMessage<>(
                new Event(data), headers);
    }


    public static boolean checkMsgIsvalid(Message<Event> msg, RedissonClient redissonClient) {
        Object id = msg.getHeaders().get(MqConstant.HEADER_KEYS);
        if (id == null || redissonClient.getBucket(HEAD + id).isExists()) {
            log.info("--RocketMq 重复或非法的消息，不处理");
            return false;
        }
       return true;
    }

    public static void protectMsg(Message<Event> msg, RedissonClient redissonClient) {
        Object id = msg.getHeaders().get(MqConstant.HEADER_KEYS);
        redissonClient.getBucket(HEAD+ id).set(id, MqConstant.EXPIRE_TIME, TimeUnit.MINUTES);
    }
}
