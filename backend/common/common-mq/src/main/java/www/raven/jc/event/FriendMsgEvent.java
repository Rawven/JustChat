package www.raven.jc.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * friend msg event
 *
 * @author 刘家辉
 * @date 2024/01/22
 */
@Data
@Accessors
@AllArgsConstructor
@NoArgsConstructor
public class FriendMsgEvent {
    private Integer senderId;
    private Integer receiverId;
    private String msg;
}
