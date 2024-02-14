package www.raven.jc.entity.po;

import lombok.Data;
import lombok.experimental.Accessors;
import www.raven.jc.dto.UserInfoDTO;

/**
 * reply
 *
 * @author 刘家辉
 * @date 2024/02/14
 */
@Data
@Accessors(chain = true)
public class Reply {
    private String id;
    private UserInfoDTO userInfo;
    private String content;
    private Long timestamp;
    private String parentId;
}
