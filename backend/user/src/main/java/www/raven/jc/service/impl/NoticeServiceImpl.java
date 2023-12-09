package www.raven.jc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import www.raven.jc.constant.NoticeConstant;
import www.raven.jc.dao.NoticeDAO;
import www.raven.jc.entity.po.Notification;
import www.raven.jc.entity.vo.NoticeVO;
import www.raven.jc.service.NoticeService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
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
