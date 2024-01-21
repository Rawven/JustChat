package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * friend chat
 *
 * @author 刘家辉
 * @date 2024/01/21
 */
@TableName("friend_chat")
@Data
@Accessors(chain = true)
public class FriendChat {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String fixId;
    private String lastMsgId;
}
