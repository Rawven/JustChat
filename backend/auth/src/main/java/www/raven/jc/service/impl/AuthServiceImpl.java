package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import www.raven.jc.constant.RoleConstant;
import www.raven.jc.dao.RolesMapper;
import www.raven.jc.dao.UserMapper;
import www.raven.jc.dao.UserRoleMapper;
import www.raven.jc.entity.model.LoginModel;
import www.raven.jc.entity.model.RegisterModel;
import www.raven.jc.entity.po.Role;
import www.raven.jc.entity.po.User;
import www.raven.jc.entity.po.UserRole;
import www.raven.jc.service.AuthService;
import www.raven.jc.util.JwtUtil;

import java.util.HashMap;

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
    private RolesMapper rolesMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
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
        UserRole userRole = userRoleMapper.selectById(user.getId());
        Assert.notNull(userRole, "用户角色不存在");
        Role role = rolesMapper.selectById(userRole.getRoleId());
        Assert.notNull(role, "角色不存在");
        return getTokenClaims(user, role);
    }

    @Override
    public String registerCommonRole(RegisterModel registerModel) {
        return register(registerModel, RoleConstant.COMMON_ROLE);
    }

    @Override
    public String registerAdminRole(RegisterModel registerModel) {
        return register(registerModel, RoleConstant.ADMIN_ROLE);
    }

    private String register(RegisterModel registerModel, Integer roleId) {
        Assert.isNull(userMapper.selectOne(new QueryWrapper<User>().
                eq("username", registerModel.getUsername())));
        User user = new User();
        Assert.isTrue(userMapper.insert(user.
                setUsername(registerModel.getUsername()).
                setPassword(passwordEncoder.encode(registerModel.getPassword()))
                .setEmail(registerModel.getEmail())) > 0);
        UserRole userRole = new UserRole().setUserId(user.getId()).setRoleId(roleId);
        Assert.isTrue(userRoleMapper.insert(userRole) > 0);
        Role role = rolesMapper.selectById(roleId);
        role.setUserCount(role.getUserCount() + 1);
        Assert.isTrue(rolesMapper.updateById(role) > 0);
        return getTokenClaims(user, role);
    }


    private String getTokenClaims(User user, Role role) {
        HashMap<String, Object> claims = new HashMap<>(3);
        claims.put("userId", user.getId());
        claims.put("role", role.getValue());
        claims.put("expireTime", System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
        String token = JwtUtil.createToken(claims, key);
        redissonClient.getBucket("token:" + user.getId()).set(token);
        return token;
    }

}
