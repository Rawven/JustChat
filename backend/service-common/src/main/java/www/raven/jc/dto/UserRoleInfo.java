package www.raven.jc.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * user role info
 *
 * @author 刘家辉
 * @date 2023/11/27
 */
@Data
@Accessors(chain = true)
public class UserRoleInfo {
    /**
     * user id
     */
    private Integer userId;
    /**
     * user name
     */
    private String username;
    /**
     * user password
     */
    private String password;
    /**
     * user role
     */
    private String role;
}
