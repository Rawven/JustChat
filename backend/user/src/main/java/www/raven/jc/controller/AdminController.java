package www.raven.jc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import www.raven.jc.entity.vo.RealAllInfoVO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.UserService;

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

}
