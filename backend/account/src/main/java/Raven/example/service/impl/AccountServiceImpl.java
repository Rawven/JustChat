package Raven.example.service.impl;

import Raven.example.client.IpfsClient;
import Raven.example.dao.UserMapper;
import Raven.example.entity.model.LoginModel;
import Raven.example.entity.model.RegisterModel;
import Raven.example.entity.po.User;
import Raven.example.service.AccountService;
import Raven.example.util.JwtUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * account service impl
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IpfsClient ipfsClient;

    @Value("${Raven.key}")
    private String key;
    @Override
    public String login(LoginModel loginModel) {
        User user = userMapper.selectOne(new QueryWrapper<User>().
                eq("username", loginModel.getUsername()).
                eq("password", loginModel.getPassword()));
        Assert.notNull(user, "用户名或密码错误");
        return JwtUtil.createToken(user.getId(),key);
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
        return JwtUtil.createToken(user.getId(),key);
    }
}
