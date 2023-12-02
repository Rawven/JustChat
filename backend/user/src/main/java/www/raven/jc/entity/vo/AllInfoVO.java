package www.raven.jc.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * all info vo
 * @author 刘家辉
 * @date 2023/11/29
 */
@Data
@Accessors(chain = true)
public class AllInfoVO {
    private Integer userId;
    private String username;
    private String profile;
    private String signature;
    private String email;
}
