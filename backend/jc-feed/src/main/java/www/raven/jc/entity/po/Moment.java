package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import www.raven.jc.model.CommonSerializable;

/**
 * moment
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@JsonSerialize
public class Moment extends CommonSerializable {
    @TableId
    private Integer id;
    private Integer userId;
    private String content;
    private String img;
    private Long timestamp;
}
