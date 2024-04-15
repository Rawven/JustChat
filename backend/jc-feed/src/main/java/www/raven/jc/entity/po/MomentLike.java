package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MomentLike {
    @TableId
    private Integer id;
    private String likeId;
    private String momentId;
}
