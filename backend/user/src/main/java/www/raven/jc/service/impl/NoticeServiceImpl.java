package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.constant.NoticeConstant;
import www.raven.jc.dao.NoticeDAO;
import www.raven.jc.dao.UserDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Notification;
import www.raven.jc.entity.po.User;
import www.raven.jc.entity.vo.NoticeVO;
import www.raven.jc.event.JoinRoomApplyEvent;
import www.raven.jc.service.NoticeService;
import www.raven.jc.util.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
    @Autowired
    private UserDAO userDAO;

    @Override
    public List<NoticeVO> loadNotice() {
        Integer userId = Integer.parseInt(request.getHeader("userId"));
        //think 通知是否需要被解决后删除 不删除留下来可以做到通知的历史记录
        List<Notification> userId1 = noticeDAO.getBaseMapper().selectList(new QueryWrapper<Notification>().eq("user_id", userId).orderByDesc("timestamp"));
        if(userId1.isEmpty()){
            return new ArrayList<>();
        }
        List<Integer> ids = userId1.stream().map(Notification::getSenderId).collect(Collectors.toList());
        List<User> users = userDAO.getBaseMapper().selectList(new QueryWrapper<User>().in("id", ids));
        Map<Integer,UserInfoDTO> map = users.stream().map(
                user -> new UserInfoDTO().setUserId(user.getId()).setUsername(user.getUsername()).setProfile(user.getProfile())
        ).collect(Collectors.toMap(UserInfoDTO::getUserId, Function.identity()));
        return userId1.stream().map(
                notification -> {
                    NoticeVO noticeVO = new NoticeVO();
                    noticeVO.setNoticeId(notification.getId())
                            .setType(notification.getType())
                            .setData(notification.getData())
                            .setTimestamp(notification.getTimestamp())
                            .setSender(map.get(notification.getSenderId()));
                    return noticeVO;
                }
        ).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void addFriendApply(Integer friendId, String message) {
        String applierId = request.getHeader("userId");
        Map<Object, Object> map = new HashMap<>(2);
        map.put("applierId", applierId);
        map.put("message", message);
        Notification notice = new Notification().setUserId(friendId)
                .setData(JsonUtil.objToJson(map))
                .setType(NoticeConstant.TYPE_ADD_FRIEND_APPLY)
                .setTimestamp(System.currentTimeMillis())
                .setSenderId(Integer.parseInt(applierId));
        Assert.isTrue(noticeDAO.save(notice));
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void addRoomApply(int founderId, JoinRoomApplyEvent payload) {
        Notification notice = new Notification().setUserId(founderId)
                .setData(String.valueOf(payload.getRoomId()))
                .setType(NoticeConstant.TYPE_JOIN_ROOM_APPLY)
                .setTimestamp(System.currentTimeMillis())
                .setSenderId(Integer.parseInt(request.getHeader("userId")));
        Assert.isTrue(noticeDAO.save(notice));
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void deleteNotification(Integer id) {
        int i = noticeDAO.getBaseMapper().deleteById(id);
        Assert.isTrue(i == 1);
    }



}
