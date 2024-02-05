package www.raven.jc.event.model;

import java.time.Clock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;
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
public class MomentReleaseEvent extends ApplicationEvent {
    private Integer releaseId;
    private Moment moment;

    public MomentReleaseEvent(Object source) {
        super(source);
    }

    public MomentReleaseEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
