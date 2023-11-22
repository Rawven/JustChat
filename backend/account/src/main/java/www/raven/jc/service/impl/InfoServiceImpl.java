package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import www.raven.jc.dao.UserMapper;
import www.raven.jc.entity.po.User;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.service.InfoService;

/**
 * info service impl
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@Service
public class InfoServiceImpl implements InfoService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public UserInfoDTO querySingleInfo(Integer userId) {
        User user = userMapper.selectById(userId);
        Assert.isNull(user,"用户不存在");
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUsername(user.getUsername()).setProfile(user.getProfile());
        return userInfoDTO;
    }
}
