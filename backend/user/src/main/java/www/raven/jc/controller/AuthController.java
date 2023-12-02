package www.raven.jc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.dto.RoleDTO;
import www.raven.jc.dto.UserAuthDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.dto.UserRegisterDTO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.UserService;

import java.util.List;

/**
 * auth controller
 *
 * @author 刘家辉
 * @date 2023/12/02
 */
@RestController
@ResponseBody
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    @Autowired
    private UserService userService;
    /**
     * get single info by name
     *
     * @param username username
     * @return {@link CommonResult}<{@link UserInfoDTO}>
     */
    @PostMapping("/getSingleInfoByName")
    CommonResult<UserAuthDTO> getUserToAuth(String username){
        return CommonResult.operateSuccess("查找成功", userService.querySingleInfoByName(username));
    }
    //TODO 加路径
    /**
     * get roles by id
     *
     * @param userId user id
     * @return {@link CommonResult}<{@link List}<{@link RoleDTO}>>
     */
    @PostMapping("/getRolesById")
    CommonResult<List<RoleDTO>> getRolesById(Integer userId){
        return CommonResult.operateSuccess("查找成功", userService.queryRolesById(userId));
    }

    /**
     * insert
     *
     * @param user user
     * @return {@link CommonResult}<{@link Boolean}>
     */
    @PostMapping("/insert")
    CommonResult<UserAuthDTO> insert(UserRegisterDTO user){
        return CommonResult.operateSuccess("插入成功", userService.insert(user));
    }
}
