package www.raven.jc.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * info vo
 *
 * @author 刘家辉
 * @date 2023/11/25
 */

@Data
@Accessors(chain = true)
public class InfoVO {
    private Integer userId;
    private String username;
    private String profile;
    private String signature;
}
