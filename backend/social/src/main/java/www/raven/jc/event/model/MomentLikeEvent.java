package www.raven.jc.event.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import www.raven.jc.entity.po.Like;

/**
 * moment like event
 *
 * @author 刘家辉
 * @date 2024/01/30
 */
@Getter
@Setter
@Accessors(chain = true)
public class MomentLikeEvent {
    private Like like;
    private String momentId;
    private Integer momentUserId;

}
