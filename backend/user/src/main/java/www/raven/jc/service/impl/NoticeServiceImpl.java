package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import www.raven.jc.constant.NoticeConstant;
import www.raven.jc.dao.NoticeDAO;
import www.raven.jc.dao.UserDAO;
import www.raven.jc.entity.po.Notification;
import www.raven.jc.entity.vo.NoticeVO;
import www.raven.jc.event.JoinRoomApplyEvent;
import www.raven.jc.event.UserSendMsgEvent;
import www.raven.jc.service.NoticeService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.websocket.NotificationHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
    private final static long EXPIRE_TIME = 10;
    @Autowired
    private NoticeDAO noticeDAO;
    @Autowired
    private NotificationHandler notificationHandler;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private RedissonClient redissonClient;


    @Bean
    public Consumer<Message<JoinRoomApplyEvent>> eventUserJoinRoomApply() {
        return msg -> {
            JoinRoomApplyEvent payload = msg.getPayload();
            if(redissonClient.getBucket(payload.getSnowFlakeId()).isExists()){
                log.info("重复的消息，不处理");
                return;
            }
            log.info("receive join room apply event:{}", msg.toString());
            Integer founderId = payload.getFounderId();
            Notification notice = new Notification().setUserId(founderId)
                    .setMessage(JsonUtil.objToJson(payload))
                    .setType(NoticeConstant.TYPE_JOIN_ROOM_APPLY)
                    .setTimestamp(System.currentTimeMillis())
                    .setStatus(NoticeConstant.STATUS_UNREAD);
            Assert.isTrue(noticeDAO.save(notice));
            ArrayList<Integer> ids = new ArrayList<>();
            ids.add(founderId);
            sendMsgToUser(ids, "有人申请加入你的聊天室");
            RBucket<Object> bucket = redissonClient.getBucket(payload.getSnowFlakeId());
            //消息有十分钟有效时间
            bucket.set(payload.getSnowFlakeId(), EXPIRE_TIME, TimeUnit.MINUTES);
        };
    }

    @Bean
    public Consumer<Message<UserSendMsgEvent>> eventUserSendMsg() {
        return msg -> {
            log.info("receive user send msg event:{}", msg.toString());
            UserSendMsgEvent payload = msg.getPayload();
            Integer userId = payload.getUserId();
            Notification notice = new Notification().setUserId(userId)
                    .setMessage(JsonUtil.objToJson(payload))
                    .setType(NoticeConstant.TYPE_USER_SEND_MSG)
                    .setTimestamp(System.currentTimeMillis())
                    .setStatus(NoticeConstant.STATUS_UNREAD);
            Assert.isTrue(noticeDAO.save(notice));
            sendMsgToUser(payload.getIdsFromRoom(), "有人发消息了");
        };
    }

    private void sendMsgToUser(List<Integer> userId, String msg) {
        HashMap<Integer, Integer> map = new HashMap<>(userId.size());
        userId.forEach(id -> map.put(id, 1));
        notificationHandler.sendMessageToIds(msg, map);
    }

    @Override
    public List<NoticeVO> loadNotice() {
        Integer userId = Integer.parseInt(request.getHeader("userId"));
        List<Notification> userId1 = noticeDAO.getBaseMapper().selectList(new QueryWrapper<Notification>().eq("user_id", userId));

        return null;
    }
}
