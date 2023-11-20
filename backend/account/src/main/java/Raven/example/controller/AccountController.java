package Raven.example.controller;

import Raven.example.entity.model.LoginModel;
import Raven.example.entity.model.RegisterModel;
import Raven.example.result.CommonResult;
import Raven.example.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * account controller
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
@RestController
@ResponseBody
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @PostMapping("/login")
    public CommonResult<String> login(@RequestBody LoginModel loginModel) {
        return CommonResult.operateSuccess("登录成功", accountService.login(loginModel));

    }

    @PostMapping("/register")
    public CommonResult<Void> register(@RequestBody RegisterModel registerModel) {
        accountService.register(registerModel);
        return CommonResult.operateSuccess("注册成功");
    }

}
