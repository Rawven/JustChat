package www.raven.jc.entity.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * login model
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
@Data
public class LoginModel {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}
