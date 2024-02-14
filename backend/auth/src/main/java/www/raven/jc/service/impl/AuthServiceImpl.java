package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.api.UserDubbo;
import www.raven.jc.constant.JwtConstant;
import www.raven.jc.constant.RoleConstant;
import www.raven.jc.dto.RoleDTO;
import www.raven.jc.dto.TokenDTO;
import www.raven.jc.dto.UserAuthDTO;
import www.raven.jc.dto.UserRegisterDTO;
import www.raven.jc.entity.model.LoginModel;
import www.raven.jc.entity.model.RegisterModel;
import www.raven.jc.result.RpcResult;
import www.raven.jc.service.AuthService;
import www.raven.jc.util.JwtUtil;

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
    private RedissonClient redissonClient;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDubbo userDubbo;
    @Value("${raven.key}")
    private String key;
    /**
     * 默认7天
     * expire time
     */
    @Value("${jwt.expire}")
    private Long expireTime;

    @Override
    public String login(LoginModel loginModel) {
        RpcResult<UserAuthDTO> result = userDubbo.getUserToAuth(loginModel.getUsername());
        Assert.isTrue(result.isSuccess());
        UserAuthDTO user = result.getData();
        if (redissonClient.getBucket(JwtConstant.TOKEN + user.getUserId()).isExists()) {
            return redissonClient.getBucket(JwtConstant.TOKEN + user.getUserId()).get().toString();
        }
        Assert.isTrue(passwordEncoder.matches(loginModel.getPassword(), user.getPassword()), "密码错误");
        RpcResult<List<RoleDTO>> rolesById = userDubbo.getRolesById(user.getUserId());
        Assert.isTrue(rolesById.isSuccess());
        return produceToken(user.getUserId(), rolesById.getData().stream().map(RoleDTO::getValue).collect(Collectors.toList()));
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public String registerCommonRole(RegisterModel registerModel) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(RoleConstant.COMMON_ROLE_NUMBER);
        return register(registerModel, list);
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public String registerAdminRole(RegisterModel registerModel) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(RoleConstant.ADMIN_ROLE_NUMBER);
        list.add(RoleConstant.COMMON_ROLE_NUMBER);
        return register(registerModel, list);
    }

    @Override
    public String refreshToken(String token) {
        TokenDTO verify = JwtUtil.parseToken(token, key);
        return produceToken(verify.getUserId(), verify.getRole());
    }

    @Override
    public void logout(String token) {
        TokenDTO verify = JwtUtil.parseToken(token, key);
        redissonClient.getBucket(JwtConstant.TOKEN + verify.getUserId()).delete();
        RpcResult<Void> voidCommonResult = userDubbo.saveLogOutTime(verify.getUserId());
        Assert.isTrue(voidCommonResult.isSuccess(), "登出失败");
    }

    private String register(RegisterModel registerModel, List<Integer> roleIds) {
        Assert.isFalse(userDubbo.checkUserExit(registerModel.getUsername()).getData());
        UserRegisterDTO user = new UserRegisterDTO();
        user.setEmail(registerModel.getEmail()).setPassword(passwordEncoder.encode(registerModel.getPassword()))
            .setUsername(registerModel.getUsername()).setRoleIds(roleIds).setProfile(registerModel.getProfile());
        RpcResult<UserAuthDTO> insert = userDubbo.insert(user);
        Assert.isTrue(insert.isSuccess(), "注册失败");
        List<RoleDTO> list = roleIds.stream().map(roleId -> new RoleDTO().setValue(RoleConstant.MAP.get(roleId))).collect(Collectors.toList());
        return produceToken(insert.getData().getUserId(), list.stream().map(RoleDTO::getValue).collect(Collectors.toList()));
    }

    private String produceToken(int userId, List<String> role) {
        String token = JwtUtil.createToken(userId, role, key, expireTime);
        redissonClient.getBucket(JwtConstant.TOKEN + userId).set(token, expireTime, TimeUnit.MILLISECONDS);
        return token;
    }

}
