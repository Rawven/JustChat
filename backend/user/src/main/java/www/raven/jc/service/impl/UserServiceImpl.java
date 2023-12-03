package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import www.raven.jc.client.IpfsClient;
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
import www.raven.jc.entity.vo.AllInfoVO;
import www.raven.jc.entity.vo.InfoVO;
import www.raven.jc.entity.vo.RealAllInfoVO;
import www.raven.jc.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
public class UserServiceImpl implements UserService {
    @Autowired
    private RolesDAO rolesDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private UserRoleDAO userRoleDAO;
    @Autowired
    private IpfsClient ipfsClient;
    @Autowired
    private HttpServletRequest request;


    @Override
    public UserInfoDTO querySingleInfo(Integer userId) {
        User user = userDAO.getById(userId);
        log.info("userId is:{}", userId);
        log.info("查询到的用户信息为:{}", user);
        Assert.notNull(user, "用户不存在");
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUsername(user.getUsername()).setProfile(user.getProfile()).setUserId(user.getId());
        return userInfoDTO;
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

    @Override
    public InfoVO defaultInfo(Integer userId) {
        User user = userDAO.getById(userId);
        Assert.notNull(user, "用户不存在");
        return new InfoVO().setSignature(user.getSignature())
                .setProfile(user.getProfile())
                .setUserId(user.getId())
                .setUsername(user.getUsername());
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
    public RealAllInfoVO queryPageUser(Integer page) {
        Long total = userDAO.getBaseMapper().selectCount(null);
        Page<User> userPage = userDAO.getBaseMapper().selectPage(new Page<>(page, 8), null);
        List<AllInfoVO> collect = userPage.getRecords().stream().map(
                user -> {
                    AllInfoVO allInfoVO = new AllInfoVO();
                    allInfoVO.setUserId(user.getId()).setUsername(user.getUsername()).setProfile(user.getProfile())
                            .setEmail(user.getEmail()).setSignature(user.getSignature());
                    return allInfoVO;
                }
        ).collect(Collectors.toList());
        return new RealAllInfoVO().setTotal(Math.toIntExact(total)).setUsers(collect);
    }

    @Override
    public UserAuthDTO querySingleInfoByName(String username) {
        User user = userDAO.getBaseMapper().selectOne(new QueryWrapper<User>().eq("username", username));
        return new UserAuthDTO().setPassword(user.getPassword()).setUserId(user.getId()).setUsername(user.getUsername());
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
    @Transactional(rollbackFor = IllegalArgumentException.class)
    public UserAuthDTO insert(UserRegisterDTO user) {
        User realUser = new User().
                setUsername(user.getUsername()).
                setPassword(user.getPassword())
                .setEmail(user.getEmail());
        Assert.isTrue(userDAO.getBaseMapper().insert(realUser) > 0);
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
    public Boolean checkUserExit(String username) {
        User user = userDAO.getBaseMapper().selectOne(new QueryWrapper<User>().eq("username", username));
        return user != null;
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void setProfile(MultipartFile profile) {
        String upload = ipfsClient.upload(profile);
        String header = request.getHeader("userId");
        Assert.isTrue(userDAO.getBaseMapper().update(new UpdateWrapper<User>().eq("id", header).set("profile", upload)) > 0, "插入头像失败");
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void setSignature(String signature) {
        String header = request.getHeader("userId");
        Assert.isTrue(userDAO.getBaseMapper().update(new UpdateWrapper<User>().eq("id", header).set("signature", signature)) > 0, "插入签名失败");
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void setUsername(String username) {
        String header = request.getHeader("userId");
        Assert.isTrue(userDAO.getBaseMapper().update(new UpdateWrapper<User>().eq("id", header).set("username", username)) > 0, "插入用户名失败");
    }
}
