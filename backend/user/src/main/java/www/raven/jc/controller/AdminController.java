package www.raven.jc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import www.raven.jc.dto.RoleDTO;
import www.raven.jc.dto.UserAuthDTO;
import www.raven.jc.dto.UserRegisterDTO;
import www.raven.jc.entity.vo.RealAllInfoVO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.UserService;

import java.util.List;

/**
 * admin controller
 *
 * @author 刘家辉
 * @date 2023/11/29
 */
@RestController
@ResponseBody
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    @Autowired
    private UserService userService;

    @GetMapping("/queryAllUser/{page}")
    public CommonResult<RealAllInfoVO> queryAllUser(@PathVariable("page") Integer page) {
        return CommonResult.operateSuccess("查找成功", userService.queryPageUser(page));
    }

    /**
     * insert
     *
     * @param user user
     * @return {@link CommonResult}<{@link Boolean}>
     */
    @PostMapping("/insert")
    CommonResult<UserAuthDTO> insert(@RequestBody UserRegisterDTO user) {
        return CommonResult.operateSuccess("插入成功", userService.insert(user));
    }

    @PostMapping("/getUserToAuth")
    CommonResult<UserAuthDTO> getUserToAuth(@RequestBody String username) {
        return CommonResult.operateSuccess("查找成功", userService.querySingleInfoByColumn("username",username));
    }

    @PostMapping("/getRolesById")
    CommonResult<List<RoleDTO>> getRolesById(@RequestBody Integer userId) {
        return CommonResult.operateSuccess("查找成功", userService.queryRolesById(userId));
    }

}
