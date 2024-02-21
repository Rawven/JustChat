package www.raven.jc.entity.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * like model
 *
 * @author 刘家辉
 * @date 2024/02/21
 */
@Data
@Accessors(chain = true)
public class LikeModel {
    private String momentId;
    private Integer momentUserId;
    private Long momentTimeStamp;
}
