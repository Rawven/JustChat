package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.constant.MqConstant;
import www.raven.jc.constant.NoticeConstant;
import www.raven.jc.dao.FriendDAO;
import www.raven.jc.dao.NoticeDAO;
import www.raven.jc.dao.UserDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Friend;
import www.raven.jc.entity.po.Notice;
import www.raven.jc.entity.po.User;
import www.raven.jc.entity.vo.NoticeVO;
import www.raven.jc.service.NoticeService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.RequestUtil;
import www.raven.jc.ws.NotificationHandler;

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
    @Autowired
    private FriendDAO friendDAO;
    @Autowired
    private NotificationHandler handler;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public List<NoticeVO> loadNotice() {
        int userId = RequestUtil.getUserId(request);
        List<Notice> notices = noticeDAO.getBaseMapper().selectList(new QueryWrapper<Notice>().eq("user_id", userId).orderByDesc("timestamp"));
        if (notices.isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> ids = notices.stream().map(Notice::getSenderId).collect(Collectors.toList());
        List<User> users = userDAO.getBaseMapper().selectList(new QueryWrapper<User>().in("id", ids));
        Map<Integer, UserInfoDTO> map = users.stream().map(
            user -> new UserInfoDTO().setUserId(user.getId()).setUsername(user.getUsername()).setProfile(user.getProfile())
        ).collect(Collectors.toMap(UserInfoDTO::getUserId, Function.identity()));
        return notices.stream().map(
            notice -> {
                NoticeVO noticeVO = new NoticeVO();
                noticeVO.setNoticeId(notice.getId())
                    .setType(notice.getType())
                    .setData(notice.getData())
                    .setTimestamp(notice.getTimestamp())
                    .setSender(map.get(notice.getSenderId()));
                return noticeVO;
            }
        ).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void addFriendApply(String friendName) {
        int applierId = RequestUtil.getUserId(request);
        User user = userDAO.getBaseMapper().selectOne(new QueryWrapper<User>().eq("username", friendName));
        Assert.notNull(user, "用户不存在");
        Assert.isNull(friendDAO.getBaseMapper().selectOne(new QueryWrapper<Friend>().eq("user_id", applierId).eq("friend_id", user.getId())), "已经是好友了");
        Integer friendId = user.getId();
        Notice notice = new Notice().setUserId(friendId)
            .setData("暂无")
            .setType(NoticeConstant.TYPE_ADD_FRIEND_APPLY)
            .setTimestamp(System.currentTimeMillis())
            .setSenderId(applierId);
        Assert.isTrue(noticeDAO.save(notice));
        RBucket<String> friendBucket = redissonClient.getBucket("token:" + friendId);
        HashMap<Object, Object> map = new HashMap<>(1);
        map.put("type", MqConstant.TAGS_USER_FRIEND_APPLY);
        if (friendBucket.isExists()) {
            handler.sendOneMessage(friendId, JsonUtil.objToJson(map));
        } else {
            log.info("--RocketMq receiver不在线");
        }
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void deleteNotification(Integer id) {
        Assert.isTrue(noticeDAO.getBaseMapper().deleteById(id) == 1);
    }

}
