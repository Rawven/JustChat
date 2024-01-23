package www.raven.jc.entity.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import www.raven.jc.util.CommonSerializable;

import java.util.Date;

/**
 * message vo
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class MessageVO extends CommonSerializable {
    private Date time;
    private String text;
    private String user;
    private String profile;

}
