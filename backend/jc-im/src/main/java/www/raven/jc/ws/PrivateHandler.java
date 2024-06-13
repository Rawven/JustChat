package www.raven.jc.ws;

import jakarta.websocket.Session;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.config.ImProperty;
import www.raven.jc.constant.ImImMqConstant;
import www.raven.jc.constant.MessageConstant;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.entity.po.Message;
import www.raven.jc.event.SaveMsgEvent;
import www.raven.jc.service.MessageService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;

/**
 * friend chat handler
 *
 * @author 刘家辉
 * @date 2024/01/21
 */
@Slf4j
@Component
public class PrivateHandler implements BaseHandler {
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserRpcService userRpcService;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private ImProperty imProperty;

    @Override
    public void onMessage(MessageDTO message, Session session) {
        Integer friendId = message.getBelongId();
        Message realMessage = new Message().setSenderId(message.getBelongId())
            .setContent(message.getText())
            .setTimestamp(new Date(message.getTime()))
            .setReceiverId(String.valueOf(friendId))
            .setType(message.getType());
        List<Integer> ids = List.of(friendId);
        messageService.saveOfflineMessage(realMessage, ids);
        broadcast(redissonClient, ids, message, rocketMQTemplate);
        MqUtil.sendMsg(rocketMQTemplate, ImImMqConstant.TAGS_SAVE_HISTORY_MSG,
            imProperty.getInTopic(), JsonUtil.objToJson(new SaveMsgEvent().setMessage(realMessage)
                .setType(MessageConstant.FRIEND)));
    }
}
