package www.raven.jc.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * token dto
 *
 * @author 刘家辉
 * @date 2023/11/27
 */
@Data
@Accessors (chain = true)
public class TokenDTO {
    private Integer userId;
    private  String role;
    private Long expireTime;
}
