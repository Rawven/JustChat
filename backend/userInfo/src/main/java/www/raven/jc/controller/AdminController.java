package www.raven.jc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import www.raven.jc.entity.vo.AllInfoVO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.InfoService;

import javax.servlet.http.HttpServletRequest;
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
    private InfoService infoService;

    @GetMapping("/queryAllUser")
    public CommonResult<List<AllInfoVO>> queryAllUser(@RequestBody Integer page) {
        return CommonResult.operateSuccess("查找成功", infoService.queryPageUser(page));
    }

}
