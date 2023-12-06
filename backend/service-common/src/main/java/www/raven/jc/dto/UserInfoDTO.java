package www.raven.jc.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * user info vo
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@Data
@Accessors(chain = true)
public class UserInfoDTO {
    private Integer userId;
    private String username;
    private String profile;
}
