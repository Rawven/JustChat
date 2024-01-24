package www.raven.jc.entity.po;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * like
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@Data
@Accessors(chain = true)
public class Like {
    private Integer userId;
    private String username;
    private Long timestamp;
}
