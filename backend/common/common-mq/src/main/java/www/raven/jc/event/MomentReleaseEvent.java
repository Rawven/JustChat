package www.raven.jc.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * moment release event
 *
 * @author 刘家辉
 * @date 2024/01/25
 */
@Data
@Accessors
@AllArgsConstructor
@NoArgsConstructor
public class MomentReleaseEvent {
    //TODO
    private Integer releaseId;

}
