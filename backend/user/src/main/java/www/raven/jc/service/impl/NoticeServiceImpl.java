package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import www.raven.jc.constant.NoticeConstant;
import www.raven.jc.dao.NoticeDAO;
import www.raven.jc.dao.UserDAO;
import www.raven.jc.entity.po.Notification;
import www.raven.jc.entity.vo.NoticeVO;
import www.raven.jc.event.Event;
import www.raven.jc.event.JoinRoomApplyEvent;
import www.raven.jc.event.RoomMsg;
import www.raven.jc.event.RoomMsgEvent;
import www.raven.jc.service.NoticeService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.websocket.NotificationHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    private HttpServletRequest request;
    @Autowired
    private NoticeDAO noticeDAO;

    @Override
    public List<NoticeVO> loadNotice() {
        Integer userId = Integer.parseInt(request.getHeader("userId"));
        List<Notification> userId1 = noticeDAO.getBaseMapper().selectList(new QueryWrapper<Notification>().eq("user_id", userId).eq("status", NoticeConstant.STATUS_UNREAD).orderByDesc("timestamp"));
           return userId1.stream().map(
                    notification -> {
                        NoticeVO noticeVO = new NoticeVO();
                        noticeVO.setType(notification.getType())
                                .setMessage(notification.getMessage())
                                .setTimestamp(notification.getTimestamp());
                        return noticeVO;
                    }
            ).collect(Collectors.toList());
    }

    @Override
    public void deleteNewRoomMsgNotice(Integer roomId) {

    }


}
