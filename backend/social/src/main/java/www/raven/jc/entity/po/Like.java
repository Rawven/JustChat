package www.raven.jc.entity.po;

import lombok.Data;
import lombok.experimental.Accessors;
import www.raven.jc.dto.UserInfoDTO;

/**
 * like
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@Data
@Accessors(chain = true)
public class Like {
    private UserInfoDTO userInfo;
    private Long timestamp;
}
