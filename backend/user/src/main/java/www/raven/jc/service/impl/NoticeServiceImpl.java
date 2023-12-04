package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import www.raven.jc.constant.NoticeConstant;
import www.raven.jc.dao.NoticeDAO;
import www.raven.jc.dao.UserDAO;
import www.raven.jc.entity.po.Notification;
import www.raven.jc.event.JoinRoomApplyEvent;
import www.raven.jc.event.UserSendMsgEvent;
import www.raven.jc.service.NoticeService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.websocket.NotificationHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    @Autowired
    private NoticeDAO noticeDAO;
    @Autowired
    private NotificationHandler notificationHandler;
    @Autowired
    private UserDAO userDAO;
    @Override
    public void receive() {

    }
    @Bean
    public Consumer<Message<JoinRoomApplyEvent>> joinRoomApplyEventConsumer() {
        return msg -> {
            log.info("receive join room apply event:{}",msg.toString());
            JoinRoomApplyEvent payload = msg.getPayload();
            Integer founderId = payload.getFounderId();
            Notification notice =new Notification().setUserId(founderId)
                    .setMessage(JsonUtil.objToJson(payload))
                    .setType(NoticeConstant.TYPE_JOIN_ROOM_APPLY)
                    .setTimestamp(System.currentTimeMillis());
            Assert.isTrue(noticeDAO.save(notice));
            ArrayList<Integer> ids = new ArrayList<>();
            ids.add(founderId);
            sendMsgToUser(ids, "有人申请加入你的聊天室");
        };
    }
    @Bean
    public Consumer<Message<UserSendMsgEvent>> userSendMsgEventConsumer() {
        return msg -> {
            log.info("receive user send msg event:{}",msg.toString());
            UserSendMsgEvent payload = msg.getPayload();
            Integer userId = payload.getUserId();
            Notification notice =new Notification().setUserId(userId)
                    .setMessage(JsonUtil.objToJson(payload))
                    .setType(NoticeConstant.TYPE_USER_SEND_MSG)
                    .setTimestamp(System.currentTimeMillis());
            Assert.isTrue(noticeDAO.save(notice));
            sendMsgToUser(payload.getIdsFromRoom(), "有人发消息了");
        };
    }
    private void sendMsgToUser(List<Integer> userId, String msg) {
        HashMap<Integer, Integer> map = new HashMap<>(userId.size());
        userId.forEach(id -> map.put(id, 1));
        notificationHandler.sendMessageToIds(msg,map);
    }

}
