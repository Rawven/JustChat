package www.raven.jc.service.consumer;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import www.raven.jc.constant.MqConstant;
import www.raven.jc.dao.FriendDAO;
import www.raven.jc.dao.NoticeDAO;
import www.raven.jc.dao.UserDAO;
import www.raven.jc.event.Event;
import www.raven.jc.util.MqUtil;
import www.raven.jc.ws.NotificationHandler;

import java.util.Objects;
import java.util.function.Consumer;

import static www.raven.jc.constant.MqConstant.HEAD;
import static www.raven.jc.constant.MqConstant.HEADER_TAGS;

/**
 * social mq consumer
 *
 * @author 刘家辉
 * @date 2024/01/25
 */
@Service
@Slf4j
public class SocialMqConsumer {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private NotificationHandler notificationHandler;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private FriendDAO friendDAO;
    @Autowired
    private NoticeDAO noticeDAO;

    @Bean
    public Consumer<Message<Event>> eventSocialToUser() {
        return msg -> {
            //判断是否重复消息
            if(MqUtil.checkMsgIsvalid(msg, redissonClient)){
                return;
            }
            String tags = Objects.requireNonNull(msg.getHeaders().get(HEADER_TAGS)).toString();
            //判断消息类型
            if (MqConstant.TAGS_MOMENT_RELEASE_RECORD.equals(tags)) {
                //TODO
            }
            MqUtil.protectMsg(msg, redissonClient);
        };
    }

}
