package www.raven.jc.entity.po;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.util.CommonSerializable;

/**
 * like
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@EqualsAndHashCode(callSuper = true) @Data
@Accessors(chain = true)
public class Like extends CommonSerializable {
    private UserInfoDTO userInfo;
    private Long timestamp;
}
