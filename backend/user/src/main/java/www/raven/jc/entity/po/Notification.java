package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

/**
 * notification
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
@TableName(value = "notice")
@Data
@Accessors(chain = true)
public class Notification {
    private Integer id;
    private Integer userId;
    private Integer chatRoomId;
    private String type;
    private String message;
    private String status;
    private Timestamp timestamp;
}