package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import www.raven.jc.dao.UserMapper;
import www.raven.jc.entity.model.LoginModel;
import www.raven.jc.entity.model.RegisterModel;
import www.raven.jc.entity.po.User;
import www.raven.jc.service.AuthService;
import www.raven.jc.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * account service impl
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${Raven.key}")
    private String key;

    @Override
    public String login(LoginModel loginModel) {
        User user = userMapper.selectOne(new QueryWrapper<User>().
                eq("username", loginModel.getUsername()));
        Assert.notNull(user, "用户不存在");
        Assert.isTrue(passwordEncoder.matches(loginModel.getPassword(), user.getPassword()), "密码错误");
        return getTokenClaims(user);
    }

    @Override
    public String register(RegisterModel registerModel) {
        Assert.isNull(userMapper.selectOne(new QueryWrapper<User>().
                eq("username", registerModel.getUsername())));
        User user = new User();
        Assert.isTrue(userMapper.insert(user.
                setUsername(registerModel.getUsername()).
                setPassword(passwordEncoder.encode(registerModel.getPassword()))
                .setEmail(registerModel.getEmail())) > 0);
        return getTokenClaims(user);
    }

    private String getTokenClaims(User user) {
        HashMap<String, Object> claims = new HashMap<>(3);
        claims.put("userId", user.getId());
        claims.put("role",user.getRole());
        String token = JwtUtil.createToken(claims, key);
        redissonClient.getBucket("token:" + user.getId()).set(token);
        return token;
    }

}
