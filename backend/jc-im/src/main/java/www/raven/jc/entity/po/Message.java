package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;
import www.raven.jc.dto.UserInfoDTO;

import java.util.Date;

/**
 * message
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@Data
@Accessors(chain = true)
public class Message {
    @TableId(value = "id", type = IdType.AUTO)
    private String messageId;
    private UserInfoDTO sender;
    private String content;
    private String type;
    private String receiverId;
    private Date timestamp;
}
