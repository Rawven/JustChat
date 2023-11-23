package www.raven.jc.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * message vo
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@Data
@Accessors(chain = true)
public class MessageVO {
    private Date time;
    private String text;
    private String user;
    private String profile;

}
