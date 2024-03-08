package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.constant.UserConstant;
import www.raven.jc.dao.RolesDAO;
import www.raven.jc.dao.UserDAO;
import www.raven.jc.dao.UserRoleDAO;
import www.raven.jc.dto.RoleDTO;
import www.raven.jc.dto.UserAuthDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.dto.UserRegisterDTO;
import www.raven.jc.entity.po.Role;
import www.raven.jc.entity.po.User;
import www.raven.jc.entity.po.UserRole;
import www.raven.jc.service.UserService;
import www.raven.jc.util.JsonUtil;

/**
 * info service impl
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private RolesDAO rolesDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private UserRoleDAO userRoleDAO;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public UserInfoDTO querySingleInfo(Integer userId) {
        if(redissonClient.getBucket(UserConstant.PREFIX +userId).isExists()){
            return JsonUtil.jsonToObj(redissonClient.getBucket(UserConstant.PREFIX+userId).get().toString(),UserInfoDTO.class);
        }
        User user = userDAO.getById(userId);
        Assert.notNull(user, "用户不存在");
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUsername(user.getUsername()).setProfile(user.getProfile()).setUserId(user.getId());
        RBucket<Object> bucket = redissonClient.getBucket(UserConstant.PREFIX + userId);
        bucket.expire(UserConstant.EXPIRE_TIME);
        return userInfoDTO;
    }

    @Override
    public UserAuthDTO queryAuthSingleInfoByColumn(String column, String value) {
        User user = userDAO.getBaseMapper().selectOne(new QueryWrapper<User>().eq(column, value));
        if (user == null) {
            return null;
        }
        return new UserAuthDTO().setPassword(user.getPassword()).setUserId(user.getId()).setUsername(user.getUsername());
    }

    @Override
    public UserInfoDTO querySingleInfoByColumn(String column, String value) {
        User user = userDAO.getBaseMapper().selectOne(new QueryWrapper<User>().eq(column, value));
        Assert.notNull(user, "用户不存在");
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUsername(user.getUsername()).setProfile(user.getProfile()).setUserId(user.getId());
        return userInfoDTO;
    }

    @Override
    public List<UserInfoDTO> queryBatchInfo(List<Integer> userIds) {
        if (userIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<User> users = userDAO.getBaseMapper().selectBatchIds(userIds);
        return users.stream().map(
            user -> {
                UserInfoDTO userInfoDTO = new UserInfoDTO();
                userInfoDTO.setUsername(user.getUsername()).setProfile(user.getProfile()).setUserId(user.getId());
                return userInfoDTO;
            }
        ).collect(Collectors.toList());
    }

    @Override
    public List<UserInfoDTO> queryAllInfo() {
        List<User> users = userDAO.getBaseMapper().selectList(null);
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
    public void updateByColumn(Integer id, String column, String value) {
        Assert.isTrue(userDAO.getBaseMapper().update(new UpdateWrapper<User>().eq("id", id).set(column, value)) > 0, "更新失败");
        redissonClient.getBucket(UserConstant.PREFIX + id).delete();
    }

    @Override
    @Transactional(rollbackFor = IllegalArgumentException.class)
    public UserAuthDTO insert(UserRegisterDTO user) {
        User realUser = new User().
            setUsername(user.getUsername()).
            setPassword(user.getPassword())
            .setEmail(user.getEmail()).setProfile(user.getProfile());
        Assert.isTrue(userDAO.getBaseMapper().insert(realUser) > 0, "插入用户失败");
        List<Role> roles = rolesDAO.getBaseMapper().selectBatchIds(user.getRoleIds());
        List<UserRole> userRoles = new ArrayList<>();
        for (Role role : roles) {
            userRoles.add(new UserRole().setUserId(realUser.getId()).setRoleId(role.getId()));
            role.setUserCount(role.getUserCount() + 1);
        }
        Assert.isTrue(rolesDAO.saveOrUpdateBatch(roles));
        Assert.isTrue(userRoleDAO.saveBatch(userRoles));
        return new UserAuthDTO().setUserId(realUser.getId()).setUsername(realUser.getUsername()).setPassword(realUser.getPassword());
    }

    @Override
    public List<UserInfoDTO> queryLikedInfoList(String column, String text) {
        List<User> users = userDAO.getBaseMapper().selectList(new QueryWrapper<User>().like(column, text));
        Assert.notEmpty(users, "没有找到相关用户");
        return users.stream().map(
            user -> {
                UserInfoDTO userInfoDTO = new UserInfoDTO();
                userInfoDTO.setUsername(user.getUsername()).setProfile(user.getProfile()).setUserId(user.getId());
                return userInfoDTO;
            }
        ).collect(Collectors.toList());
    }

    @Override
    public List<RoleDTO> queryRolesById(Integer userId) {
        List<UserRole> roleIds = userRoleDAO.getBaseMapper().selectList(new QueryWrapper<UserRole>().eq("user_id", userId));
        Assert.notEmpty(roleIds, "用户没有角色");
        List<Role> roles = rolesDAO.getBaseMapper().selectBatchIds(roleIds.stream().map(UserRole::getRoleId).collect(Collectors.toList()));
        return roles.stream().map(
            role -> {
                RoleDTO roleDTO = new RoleDTO();
                roleDTO.setValue(role.getValue());
                return roleDTO;
            }
        ).collect(Collectors.toList());
    }

    @Override
    public Boolean checkUserExit(String username) {
        User user = userDAO.getBaseMapper().selectOne(new QueryWrapper<User>().eq("username", username));
        return user != null;
    }

    @Override
    @Transactional(rollbackFor = IllegalArgumentException.class)
    public void saveTime(Integer userId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Assert.isTrue(userDAO.getBaseMapper().update(new UpdateWrapper<User>().eq("id", userId).set("last_online_time", timestamp)) > 0, "登出失败");
    }

}
