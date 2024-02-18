package www.raven.jc.event.model;

import lombok.Data;
import lombok.experimental.Accessors;
import www.raven.jc.entity.po.Reply;

/**
 * moment reply event
 *
 * @author 刘家辉
 * @date 2024/02/14
 */
@Data
@Accessors(chain = true)
public class MomentReplyEvent {
    private String commentId;
    private String momentId;
    private Integer momentUserId;
    private Reply reply;
}
