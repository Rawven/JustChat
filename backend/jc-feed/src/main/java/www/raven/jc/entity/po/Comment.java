package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import www.raven.jc.model.CommonSerializable;

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
    @TableId
    private Integer id;
    private Integer userId;
    private String content;
    private Long timestamp;
    private String parentId;
}
