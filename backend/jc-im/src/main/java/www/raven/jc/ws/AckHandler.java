package www.raven.jc.ws;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.dao.MessageAckDAO;
import www.raven.jc.dao.UserRoomDAO;
import www.raven.jc.entity.dto.MessageDTO;
import www.raven.jc.entity.po.MessageAck;
import www.raven.jc.entity.po.UserRoom;

/**
 * ack handler
 *
 * @author 刘家辉
 * @date 2024/06/12
 */
@Slf4j
@Component
public class AckHandler implements BaseHandler {
    @Autowired
    private UserRoomDAO userRoomDAO;
    @Autowired
    private MessageAckDAO messageAckDAO;

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void onMessage(MessageDTO message, Session session) {
        String msgId = message.getText();
        //更新lastAckMsgId
        UserRoom userRoom = userRoomDAO.getById(message.getBelongId());
        Date time = userRoom.getLastAckTime();
        if (time != null && time.getTime() > message.getTime()) {
            log.error("ack time error");
            session.getAsyncRemote().sendText("ack: time error");
            return;
        }
        //更新Ack
        MessageAck ack = messageAckDAO.getBaseMapper().selectOne(new QueryWrapper<MessageAck>()
            .eq("message_id", msgId)
            .eq("receiver_id", message.getBelongId())
            .eq("if_ack", false));
        if (ack != null) {
            ack.setIfAck(true);
            boolean update = messageAckDAO.updateById(ack);
            if (update) {
                userRoom.setLastAckTime(new Date(message.getTime()));
                boolean b = userRoomDAO.updateById(userRoom);
                RemoteEndpoint.Async remote = session.getAsyncRemote();
                if (b) {
                    session.getAsyncRemote().sendText("ack: success");
                    return;
                }
            }
        }
        session.getAsyncRemote().sendText("ack: fail");
        throw new RuntimeException("ack fail");
    }
}
