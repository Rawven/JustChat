package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
public class FriendServiceImpl implements FriendService {
    @Autowired
    private FriendDAO friendDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private HttpServletRequest request;
    @Override
    public List<UserInfoDTO> getFriendInfos(int userId) {
        List<Friend> userId1 = friendDAO.getBaseMapper().selectList(new QueryWrapper<Friend>().eq("user_id", userId));
        if(userId1.isEmpty()){
            return new ArrayList<>();
        }
        List<Long> collect = userId1.stream().map(Friend::getFriendId).collect(Collectors.toList());
        return userDAO.getBaseMapper().selectList(new QueryWrapper<User>().in("id", collect)).stream().map(
                user -> new UserInfoDTO()
                        .setUserId(user.getId()).setUsername(user.getUsername()).setProfile(user.getProfile())
        ).collect(Collectors.toList());
    }

    @Override
    public void agreeApplyFromFriend(int friendId) {
        int userId = Integer.parseInt(request.getHeader("userId"));
        Friend friend = new Friend().setUserId((long) userId).setFriendId((long) friendId);
        int insert = friendDAO.getBaseMapper().insert(friend);
        Assert.isTrue(insert == 1, "成为好友失败");
    }
}
