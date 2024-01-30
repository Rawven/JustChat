package www.raven.jc.entity.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * register model
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
@Data
@Accessors(chain = true)
public class RegisterModel {
    private String username;
    private String password;
    private String email;

    private String profile;
}
