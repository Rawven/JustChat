package www.raven.jc.entity.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * group msg model
 *
 * @author 刘家辉
 * @date 2024/02/23
 */
@Data
@Accessors(chain = true)
public class GroupMsgModel {
    private Integer roomId;
    private Integer page;
    private Integer size;
}
