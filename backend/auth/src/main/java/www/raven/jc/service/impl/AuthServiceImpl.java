package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.constant.RoleConstant;
import www.raven.jc.dto.RoleDTO;
import www.raven.jc.dto.TokenDTO;
import www.raven.jc.dto.UserAuthDTO;
import www.raven.jc.dto.UserRegisterDTO;
import www.raven.jc.entity.model.LoginModel;
import www.raven.jc.entity.model.RegisterModel;
import www.raven.jc.feign.UserFeign;
import www.raven.jc.result.CommonResult;
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
    private RedissonClient redissonClient;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserFeign userFeign;
    @Value("${Raven.key}")
    private String key;

    @Override
    public String login(LoginModel loginModel) {
        CommonResult<UserAuthDTO> result = userFeign.getUserToAuth(loginModel.getUsername());
        Assert.isTrue(result.getCode() == 200);
        UserAuthDTO user = result.getData();
        Assert.isTrue(passwordEncoder.matches(loginModel.getPassword(), user.getPassword()), "密码错误");
        CommonResult<List<RoleDTO>> rolesById = userFeign.getRolesById(user.getUserId());
        Assert.isTrue(rolesById.getCode() == 200);
        return getTokenClaims(user.getUserId(), rolesById.getData().stream().map(RoleDTO::getValue).collect(Collectors.toList()));
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
        return register(registerModel, list);
    }

    @Override
    public String refreshToken(String token) {
        TokenDTO verify = JwtUtil.verify(token, key);
        return getTokenClaims(verify.getUserId(), verify.getRole());
    }

    @Override
    public void logout(String token) {
        TokenDTO verify = JwtUtil.verify(token, key);
        redissonClient.getBucket("token:" + verify.getUserId()).delete();
        CommonResult<Void> voidCommonResult = userFeign.saveLogOutTime(verify.getUserId());
        Assert.isTrue(voidCommonResult.getCode() == 200);
    }

    private String register(RegisterModel registerModel, List<Integer> roleIds) {
        Assert.isFalse(userFeign.checkUserExit(registerModel.getUsername()).getData());
        UserRegisterDTO user = new UserRegisterDTO();
        user.setEmail(registerModel.getEmail()).setPassword(passwordEncoder.encode(registerModel.getPassword()))
                .setUsername(registerModel.getUsername()).setRoleIds(roleIds);
        CommonResult<UserAuthDTO> insert = userFeign.insert(user);
        Assert.isTrue(insert.getCode() == 200);
        List<RoleDTO> list = new ArrayList<>();
        for (Integer roleId : roleIds) {
            list.add(new RoleDTO().setValue(RoleConstant.MAP.get(roleId)));
        }
        return getTokenClaims(insert.getData().getUserId(), list.stream().map(RoleDTO::getValue).collect(Collectors.toList()));
    }


    private String getTokenClaims(int userId, List<String> role) {
        HashMap<String, Object> claims = new HashMap<>(3);
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("expireTime", System.currentTimeMillis() + 1000 * 60 * 30);
        String token = JwtUtil.createToken(claims, key);
        redissonClient.getBucket("token:" + userId).set(token);
        return token;
    }

}
