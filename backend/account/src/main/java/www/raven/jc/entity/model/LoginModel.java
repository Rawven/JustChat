package www.raven.jc.entity.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * login model
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
@Data
public class LoginModel {
    @NotNull
    private String username;
    @NotNull
    private String password;
}
