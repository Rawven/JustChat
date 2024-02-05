package www.raven.jc.event.model;

import java.time.Clock;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;
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
public class MomentLikeEvent extends ApplicationEvent {
    private Like like;
    private String momentId;
    private Integer momentUserId;

    public MomentLikeEvent(Object source) {
        super(source);
    }

    public MomentLikeEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
