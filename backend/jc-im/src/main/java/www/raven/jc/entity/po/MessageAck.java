package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * message ack
 *
 * @author 刘家辉
 * @date 2024/06/12
 */
@Data
@Accessors(chain = true)
@TableName(value = "message_ack", schema = "public")
public class MessageAck {
    @TableId(value = "id", type = IdType.AUTO)
    private String id;
    private String messageId;
    private Integer senderId;
    private Integer receiverId;
    private Integer roomId;
    private Boolean ifAck;
    private Long createTime;
}
