package www.raven.jc.entity.po;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.model.CommonSerializable;

import java.util.List;

/**
 * comment
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class Comment extends CommonSerializable {
    private String id;
    private UserInfoDTO userInfo;
    private String content;
    private Long timestamp;
    private List<Reply> replies;
}
