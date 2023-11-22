package www.raven.jc.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.web.multipart.MultipartFile;
import www.raven.jc.client.IpfsClient;
import www.raven.jc.dao.UserMapper;
import www.raven.jc.entity.model.LoginModel;
import www.raven.jc.entity.model.RegisterModel;
import www.raven.jc.entity.po.User;
import www.raven.jc.service.AccountService;
import www.raven.jc.util.JwtUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * account service impl
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IpfsClient ipfsClient;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private HttpServletRequest request;

    @Value("${Raven.key}")
    private String key;
    @Override
    public String login(LoginModel loginModel) {
        User user = userMapper.selectOne(new QueryWrapper<User>().
                eq("username", loginModel.getUsername()).
                eq("password", loginModel.getPassword()));
        Assert.notNull(user, "用户名或密码错误");
        String token = JwtUtil.createToken(user.getId(), key);
        redissonClient.getBucket("token:"+user.getId()).set(token);
        return token;
    }

    @Override
    public String register(RegisterModel registerModel) {
         Assert.isNull( userMapper.selectOne(new QueryWrapper<User>().
                eq("username", registerModel.getUsername())));
        User user = new User();
        Assert.isTrue(userMapper.insert(user.
                setUsername(registerModel.getUsername()).
                setPassword(registerModel.getPassword())
                .setEmail(registerModel.getEmail())) >0);
        String token = JwtUtil.createToken(user.getId(), key);
        redissonClient.getBucket("token:"+user.getId()).set(token);
        return token;
    }

    @Override
    public void profileUpload(MultipartFile profile) {
        String upload = ipfsClient.upload(profile);
        String header = request.getHeader("userId");
        Assert.isTrue(userMapper.update(new UpdateWrapper<User>().eq("id",header).set("profile",upload))>0,"插入头像失败");

    }
}
