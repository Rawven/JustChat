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

    @PostMapping("/profileUpload")
    public CommonResult<Void> profileUpload(@RequestParam("file") MultipartFile profile) {
        accountService.profileUpload(profile);
        return CommonResult.operateSuccess("上传图像成功");
    }

}
