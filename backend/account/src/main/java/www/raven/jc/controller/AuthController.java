package www.raven.jc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import www.raven.jc.entity.model.LoginModel;
import www.raven.jc.entity.model.RegisterModel;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.AccountService;

/**
 * account controller
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
@RestController
@RequestMapping("/auth")
@ResponseBody
public class AuthController {
    @Autowired
    private AccountService accountService;

    @PostMapping("/login")
    public CommonResult<String> login(@RequestBody LoginModel loginModel) {
        return CommonResult.operateSuccess("登录成功", accountService.login(loginModel));
    }

    @PostMapping("/register")
    public CommonResult<String> register(@RequestBody RegisterModel registerModel) {
        return CommonResult.operateSuccess("注册成功", accountService.register(registerModel));
    }



}
