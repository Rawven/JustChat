package www.raven.jc.entity.model;

import javax.validation.constraints.NotNull;
import lombok.Data;

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
