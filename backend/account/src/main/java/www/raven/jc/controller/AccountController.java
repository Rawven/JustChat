package www.raven.jc.controller;
import org.springframework.web.multipart.MultipartFile;
import www.raven.jc.entity.model.LoginModel;
import www.raven.jc.entity.model.RegisterModel;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.AccountService;
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
public class AccountController {
    @Autowired
    private AccountService accountService;
    @PostMapping("/login")
    public CommonResult<String> login(@RequestBody LoginModel loginModel) {
        return CommonResult.operateSuccess("登录成功", accountService.login(loginModel));
    }

    @PostMapping("/register")
    public CommonResult<String> register(@RequestBody RegisterModel registerModel) {
        return CommonResult.operateSuccess("注册成功",accountService.register(registerModel));
    }

    @PostMapping("/setProfile")
    public CommonResult<Void> setProfile(@RequestParam("file") MultipartFile profile) {
        accountService.setProfile(profile);
        return CommonResult.operateSuccess("设置头像成功");
    }
    @PostMapping("/setSignature")
    public CommonResult<Void> setSignature(@RequestBody String signature) {
        accountService.setSignature(signature);
        return CommonResult.operateSuccess("设置签名成功");
    }
    @PostMapping("/setUsername")
    public CommonResult<Void> setUsername(@RequestBody String username) {
        accountService.setUsername(username);
        return CommonResult.operateSuccess("重命名成功");
    }

}
