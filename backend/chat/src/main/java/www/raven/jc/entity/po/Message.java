package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * message
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@TableName
@Data
@Accessors(chain = true)
public class Message {
    @TableId(value = "message_id", type = IdType.AUTO)
    private Integer messageId;
    private Integer senderId;
    private String content;
    private Date timestamp;
}
