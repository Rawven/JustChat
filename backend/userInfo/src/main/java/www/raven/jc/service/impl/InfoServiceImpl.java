package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import www.raven.jc.client.IpfsClient;
import www.raven.jc.dao.UserMapper;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.User;
import www.raven.jc.entity.vo.InfoVO;
import www.raven.jc.service.InfoService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * info service impl
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@Service
@Slf4j
public class InfoServiceImpl implements InfoService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IpfsClient ipfsClient;
    @Autowired
    private HttpServletRequest request;

    @Value("${Raven.key}")
    private String key;


    @Override
    public UserInfoDTO querySingleInfo(Integer userId) {
        User user = userMapper.selectById(userId);
        log.info("userId is:{}", userId);
        log.info("查询到的用户信息为:{}", user);
        Assert.notNull(user, "用户不存在");
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUsername(user.getUsername()).setProfile(user.getProfile()).setUserId(user.getId());
        return userInfoDTO;
    }

    @Override
    public List<UserInfoDTO> queryAllInfo() {
        List<User> users = userMapper.selectList(null);
        return users.stream().map(
                user -> {
                    UserInfoDTO userInfoDTO = new UserInfoDTO();
                    userInfoDTO.setUsername(user.getUsername()).setProfile(user.getProfile()).setUserId(user.getId());
                    return userInfoDTO;
                }
        ).collect(Collectors.toList());
    }

    @Override
    public InfoVO defaultInfo(Integer userId) {
        User user = userMapper.selectById(userId);
        Assert.notNull(user, "用户不存在");
        return new InfoVO().setSignature(user.getSignature())
                .setProfile(user.getProfile())
                .setUserId(user.getId())
                .setUsername(user.getUsername());
    }

    @Override
    public List<UserInfoDTO> queryLikedInfoList(String column, String text) {
        List<User> users = userMapper.selectList(new QueryWrapper<User>().like(column, text));
        Assert.notEmpty(users, "没有找到相关用户");
        return users.stream().map(
                user -> {
                    UserInfoDTO userInfoDTO = new UserInfoDTO();
                    userInfoDTO.setUsername(user.getUsername()).setProfile(user.getProfile()).setUserId(user.getId());
                    return userInfoDTO;
                }
        ).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void setProfile(MultipartFile profile) {
        String upload = ipfsClient.upload(profile);
        String header = request.getHeader("userId");
        Assert.isTrue(userMapper.update(new UpdateWrapper<User>().eq("id", header).set("profile", upload)) > 0, "插入头像失败");
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void setSignature(String signature) {
        String header = request.getHeader("userId");
        Assert.isTrue(userMapper.update(new UpdateWrapper<User>().eq("id", header).set("signature", signature)) > 0, "插入签名失败");
    }
    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void setUsername(String username) {
        String header = request.getHeader("userId");
        Assert.isTrue(userMapper.update(new UpdateWrapper<User>().eq("id", header).set("username", username)) > 0, "插入用户名失败");
    }
}
