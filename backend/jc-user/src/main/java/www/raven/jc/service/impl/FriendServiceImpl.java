package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.constant.ImUserMqConstant;
import www.raven.jc.dao.FriendDAO;
import www.raven.jc.dao.UserDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Friend;
import www.raven.jc.event.model.DeleteNoticeEvent;
import www.raven.jc.service.FriendService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;
import www.raven.jc.util.RequestUtil;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * friend service impl
 *
 * @author 刘家辉
 * @date 2024/01/20
 */
@Service
@Slf4j
public class FriendServiceImpl implements FriendService {
    @Autowired
    private FriendDAO friendDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private StreamBridge streamBridge;

    @Override
    public List<UserInfoDTO> getFriendInfos(int userId) {
        return userDAO.getBaseMapper().selectUserByFriendId(userId).stream().map(
                user -> new UserInfoDTO()
                        .setUserId(user.getId()).setUsername(user.getUsername()).setProfile(user.getProfile())
        ).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void agreeApplyFromFriend(int friendId, int noticeId) {
        int userId = RequestUtil.getUserId(request);
        Friend friend = new Friend().setUserId((long) userId).setFriendId((long) friendId);
        Friend friend1 = new Friend().setUserId((long) friendId).setFriendId((long) userId);
        boolean b = friendDAO.saveBatch(List.of(friend, friend1));
        Assert.isTrue(b, "成为好友失败");
        streamBridge.send("producer-out-1", MqUtil.createMsg(JsonUtil.objToJson(new DeleteNoticeEvent(noticeId)), ImUserMqConstant.TAGS_DELETE_NOTICE));
    }

    @Override
    public void refuseApplyFromFriend(int noticeId) {
        streamBridge.send("producer-out-1", MqUtil.createMsg(JsonUtil.objToJson(new DeleteNoticeEvent(noticeId)), ImUserMqConstant.TAGS_DELETE_NOTICE));
    }

    @Override
    public List<UserInfoDTO> getFriendAndMeInfos(int userId) {
        return userDAO.getBaseMapper().selectUsersAndFriends(userId).stream().map(
                user -> new UserInfoDTO()
                        .setUserId(user.getId()).setUsername(user.getUsername()).setProfile(user.getProfile())
        ).collect(Collectors.toList());
    }

}
