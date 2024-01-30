package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.dao.FriendDAO;
import www.raven.jc.dao.UserDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Friend;
import www.raven.jc.entity.po.User;
import www.raven.jc.service.FriendService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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

    @Override
    public List<UserInfoDTO> getFriendInfos(int userId) {
        List<Long> collect = getFriendIds(userId);
        return userDAO.getBaseMapper().selectList(new QueryWrapper<User>().in("id", collect)).stream().map(
                user -> new UserInfoDTO()
                        .setUserId(user.getId()).setUsername(user.getUsername()).setProfile(user.getProfile())
        ).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void agreeApplyFromFriend(int friendId) {
        int userId = Integer.parseInt(request.getHeader("userId"));
        Friend friend = new Friend().setUserId((long) userId).setFriendId((long) friendId);
        Friend friend1 = new Friend().setUserId((long) friendId).setFriendId((long) userId);
        boolean b = friendDAO.saveBatch(List.of(friend, friend1));
        Assert.isTrue(b,"成为好友失败");
    }

    @Override
    public List<UserInfoDTO> getFriendAndMeInfos(int userId) {
        List<Long> collect = getFriendIds(userId);
        collect.add((long) userId);
        return userDAO.getBaseMapper().selectList(new QueryWrapper<User>().in("id", collect)).stream().map(
                user -> new UserInfoDTO()
                        .setUserId(user.getId()).setUsername(user.getUsername()).setProfile(user.getProfile())
        ).collect(Collectors.toList());
    }
    private List<Long> getFriendIds(int userId){
        List<Friend> userId1 = friendDAO.getBaseMapper().selectList(new QueryWrapper<Friend>().eq("user_id", userId));
        if(userId1.isEmpty()){
            return new ArrayList<>();
        }
        return userId1.stream().map(Friend::getFriendId).collect(Collectors.toList());
    }
}
