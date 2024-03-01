package www.raven.jc.entity.dto;

import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;
import www.raven.jc.dto.UserInfoDTO;

/**
 * message dto
 *
 * @author 刘家辉
 * @date 2023/11/24
 */
@Data
@Accessors(chain = true)
public class MessageDTO {
    private Long time;
    private String text;
    private UserInfoDTO userInfoDTO;
    private Integer belongId;
    private String type;
}
