package www.raven.jc.event;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * moment like event
 *
 * @author 刘家辉
 * @date 2024/01/30
 */
@Data
@Accessors(chain = true)
public class MomentLikeEvent {
    private String like;
    private String momentId;
    private Integer momentUserId;
}
