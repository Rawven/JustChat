package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import www.raven.jc.model.CommonSerializable;

import java.util.Date;

/**
 * message
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class Message extends CommonSerializable {
    @TableId(value = "id", type = IdType.AUTO)
    private String messageId;
    private Integer senderId;
    private String content;
    private String type;
    private String receiverId;
    private Date timestamp;
}
