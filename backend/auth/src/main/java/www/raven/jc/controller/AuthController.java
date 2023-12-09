package www.raven.jc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.entity.model.LoginModel;
import www.raven.jc.entity.model.RegisterAdminModel;
import www.raven.jc.entity.model.RegisterModel;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.AuthService;

import java.util.Objects;

/**
 * account controller
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
@RestController
@ResponseBody
public class AuthController {
    @Autowired
    private AuthService authService;
    @Value("${Raven.key}")
    private String key;

    @PostMapping("/login")
    public CommonResult<String> login(@RequestBody LoginModel loginModel) {
        return CommonResult.operateSuccess("登录成功", authService.login(loginModel));
    }

    @PostMapping("/register")
    public CommonResult<String> register(@RequestBody RegisterModel registerModel) {
        return CommonResult.operateSuccess("注册成功", authService.registerCommonRole(registerModel));
    }

    @PostMapping("/registerAdmin")
    public CommonResult<String> register(@RequestBody RegisterAdminModel registerModel) {
        if (Objects.equals(registerModel.getPrivateKey(), key)) {
            RegisterModel model = new RegisterModel().setUsername(registerModel.getUsername())
                    .setPassword(registerModel.getPassword())
                    .setEmail(registerModel.getEmail());
            return CommonResult.operateSuccess("注册成功", authService.registerAdminRole(model));
        } else {
            return CommonResult.operateFailure("注册失败", "私钥错误");
        }
    }

    @PostMapping("/logout")
    public CommonResult<Void> logout(@RequestBody String token) {
        authService.logout(token);
        return CommonResult.operateSuccess("登出成功");
    }

    @PostMapping("/refresh")
    public CommonResult<String> refresh(@RequestBody String token) {
        return CommonResult.operateSuccess("刷新成功", authService.refreshToken(token));
    }


}
