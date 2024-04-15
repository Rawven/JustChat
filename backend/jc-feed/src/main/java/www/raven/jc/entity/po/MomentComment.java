package www.raven.jc.entity.po;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MomentComment {
    private Integer id;
    private String momentId;
    private String commentId;
}
