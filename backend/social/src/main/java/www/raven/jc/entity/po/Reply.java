package www.raven.jc.entity.po;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.model.CommonSerializable;

/**
 * reply
 *
 * @author 刘家辉
 * @date 2024/02/14
 */
@EqualsAndHashCode(callSuper = true) @Data
@Accessors(chain = true)
public class Reply  extends CommonSerializable {
    private String id;
    private UserInfoDTO userInfo;
    private String content;
    private Long timestamp;
    private String parentId;
}
