package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * user room
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
@TableName
@Data
@Accessors(chain = true)
public class UserRoom {
    private Integer id;
    private Integer userId;
    private Integer roomId;
}
