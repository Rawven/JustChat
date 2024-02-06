package www.raven.jc.event.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import www.raven.jc.entity.po.Moment;

/**
 * moment release event
 *
 * @author 刘家辉
 * @date 2024/01/25
 */
@Getter
@Setter
@Accessors(chain = true)
public class MomentReleaseEvent {
    private Integer releaseId;
    private Moment moment;

}
