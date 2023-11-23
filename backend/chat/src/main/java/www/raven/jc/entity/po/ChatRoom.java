package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * chat room
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@TableName("chat_room")
@Data
@Accessors(chain = true)
public class ChatRoom {
    @TableId(value = "room_id", type = IdType.AUTO)
    private Integer roomId;
    private String roomName;
    private String roomDescription;
    private Integer founderId;
    private Integer maxPeople;
}
