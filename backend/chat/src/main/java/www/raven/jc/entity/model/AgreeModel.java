package www.raven.jc.entity.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * agree model
 *
 * @author 刘家辉
 * @date 2023/12/16
 */
@Data
@Accessors(chain = true)
public class AgreeModel {
    private Integer roomId;
    private Integer userId;
}
