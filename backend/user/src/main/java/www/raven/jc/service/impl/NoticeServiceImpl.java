package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import www.raven.jc.constant.NoticeConstant;
import www.raven.jc.dao.NoticeDAO;
import www.raven.jc.entity.po.Notification;
import www.raven.jc.entity.vo.NoticeVO;
import www.raven.jc.service.NoticeService;
import www.raven.jc.util.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        //think 通知是否需要被解决后删除 不删除留下来可以做到通知的历史记录
        List<Notification> userId1 = noticeDAO.getBaseMapper().selectList(new QueryWrapper<Notification>().eq("user_id", userId).
                eq("status", NoticeConstant.STATUS_UNREAD).orderByDesc("timestamp"));
        return userId1.stream().map(
                notification -> {
                    NoticeVO noticeVO = new NoticeVO();
                    noticeVO.setNoticeId(notification.getId())
                            .setType(notification.getType())
                            .setMessage(notification.getMessage())
                            .setTimestamp(notification.getTimestamp());
                    return noticeVO;
                }
        ).collect(Collectors.toList());
    }
    @Override
    public void addFriendApply(Integer friendId,String message){
        String applierId = request.getHeader("userId");
        Map<Object,Object> map = new HashMap<>(2);
        map.put("applierId",applierId);
        map.put("message",message);
        Notification notice = new Notification().setUserId(friendId)
                .setMessage(JsonUtil.objToJson(map))
                .setType(NoticeConstant.TYPE_ADD_FRIEND_APPLY)
                .setTimestamp(System.currentTimeMillis())
                .setStatus(NoticeConstant.STATUS_UNREAD);
        Assert.isTrue(noticeDAO.save(notice));
    }
    @Override
    public void addRoomApply(int founderId,Object payload){
        Notification notice = new Notification().setUserId(founderId)
                .setMessage(JsonUtil.objToJson(payload))
                .setType(NoticeConstant.TYPE_JOIN_ROOM_APPLY)
                .setTimestamp(System.currentTimeMillis())
                .setStatus(NoticeConstant.STATUS_UNREAD);
        Assert.isTrue(noticeDAO.save(notice));
    }

    @Override
    public void doneNotification(Integer id) {
        Notification notification = noticeDAO.getById(id);
        notification.setStatus(NoticeConstant.STATUS_DONE);
        Assert.isTrue(noticeDAO.updateById(notification));
    }

    @Override
    public void deleteNewRoomMsgNotice(Integer roomId) {

    }


}
