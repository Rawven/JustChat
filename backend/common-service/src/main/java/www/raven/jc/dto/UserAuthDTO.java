package www.raven.jc.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * user dto
 *
 * @author 刘家辉
 * @date 2023/12/02
 */

@Data
@Accessors(chain = true)
public class UserAuthDTO {
    private Integer userId;
    private String username;
    private String password;
}
