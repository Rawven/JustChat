package www.raven.jc.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import www.raven.jc.event.JoinRoomApplyEvent;
import www.raven.jc.event.UserSendMsgEvent;
import www.raven.jc.service.NoticeService;

import java.util.function.Consumer;

/**
 * notice service impl
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
@Service
@Slf4j
public class NoticeServiceImpl implements NoticeService {
    @Override
    public void receive() {

    }
    @Bean
    public Consumer<Message<JoinRoomApplyEvent>> joinRoomApplyEventConsumer() {
        return msg -> {

        };
    }


    @Bean
    public Consumer<Message<UserSendMsgEvent>> userSendMsgEventConsumer() {
        return msg -> {

        };
    }

}
