package www.raven.jc.entity.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * register admin model
 *
 * @author 刘家辉
 * @date 2023/11/28
 */

@Data
@Accessors(chain = true)
public class RegisterAdminModel {
    private String username;
    private String password;
    private String email;
    private String privateKey;
}
