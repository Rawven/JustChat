package www.raven.jc.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * query user info dto
 *
 * @author 刘家辉
 * @date 2023/11/25
 */
@Data
@Accessors(chain = true)
public class QueryUserInfoDTO {
    private String column;
    private String text;
}
