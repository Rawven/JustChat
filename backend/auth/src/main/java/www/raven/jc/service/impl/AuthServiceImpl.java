package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.constant.RoleConstant;
import www.raven.jc.dao.RolesDAO;
import www.raven.jc.dao.UserRoleDAO;
import www.raven.jc.dao.mapper.RolesMapper;
import www.raven.jc.dao.mapper.UserRoleMapper;
import www.raven.jc.dao.mapper.UserMapper;
import www.raven.jc.entity.model.LoginModel;
import www.raven.jc.entity.model.RegisterModel;
import www.raven.jc.entity.po.Role;
import www.raven.jc.entity.po.User;
import www.raven.jc.entity.po.UserRole;
import www.raven.jc.service.AuthService;
import www.raven.jc.util.JwtUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
    private RolesDAO rolesDAO;
    @Autowired
    private UserRoleDAO userRoleDAO;
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
        List<UserRole> userRoles = userRoleMapper.selectList(new QueryWrapper<UserRole>().eq("user_id", user.getId()));
        Assert.notNull(userRoles, "用户角色不存在");
        List<Role> roles = rolesMapper.selectBatchIds(userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList()));
        Assert.isTrue(!roles.isEmpty(), "角色不存在");
        return getTokenClaims(user, roles);
    }
    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public String registerCommonRole(RegisterModel registerModel) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(RoleConstant.COMMON_ROLE);
        return register(registerModel, list);
    }
    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public String registerAdminRole(RegisterModel registerModel) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(RoleConstant.ADMIN_ROLE);
        list.add(RoleConstant.COMMON_ROLE);
        return register(registerModel,list);
    }

    private String register(RegisterModel registerModel, List<Integer> roleId) {
        Assert.isNull(userMapper.selectOne(new QueryWrapper<User>().
                eq("username", registerModel.getUsername())));
        User user = new User();
        Assert.isTrue(userMapper.insert(user.
                setUsername(registerModel.getUsername()).
                setPassword(passwordEncoder.encode(registerModel.getPassword()))
                .setEmail(registerModel.getEmail())) > 0);
        List<Role> roles = rolesMapper.selectBatchIds(roleId);
        List<UserRole> userRoles = new ArrayList<>();
        for (Role role: roles) {
            userRoles.add(new UserRole().setUserId(user.getId()).setRoleId(role.getRoleId()));
            role.setUserCount(role.getUserCount() + 1);
        }
        Assert.isTrue(rolesDAO.saveOrUpdateBatch(roles));
        Assert.isTrue(userRoleDAO.saveBatch(userRoles));
        return getTokenClaims(user, roles);
    }


    private String getTokenClaims(User user, List<Role> role) {
        HashMap<String, Object> claims = new HashMap<>(3);
        claims.put("userId", user.getId());
        List<String> values = role.stream().map(Role::getValue).collect(Collectors.toList());
        claims.put("role", values);
        claims.put("expireTime", System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
        String token = JwtUtil.createToken(claims, key);
        redissonClient.getBucket("token:" + user.getId()).set(token);
        return token;
    }

}
