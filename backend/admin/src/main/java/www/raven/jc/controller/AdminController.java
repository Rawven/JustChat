package www.raven.jc.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.result.CommonResult;

/**
 * admin controller
 *
 * @author 刘家辉
 * @date 2023/11/27
 */
@RestController
@ResponseBody
public class AdminController {


    @PostMapping("/delete")
    public CommonResult<Void> disableAccount() {
        return null;
    }
}
