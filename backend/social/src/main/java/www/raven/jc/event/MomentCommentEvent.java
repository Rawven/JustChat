package www.raven.jc.event;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * moment comment event
 *
 * @author 刘家辉
 * @date 2024/01/30
 */
@Data
@Accessors(chain = true)
public class MomentCommentEvent {
    private String comment;
    private String momentId;
    private Integer momentUserId;
}
