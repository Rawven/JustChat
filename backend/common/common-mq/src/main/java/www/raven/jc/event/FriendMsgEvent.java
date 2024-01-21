package www.raven.jc.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors
@AllArgsConstructor
@NoArgsConstructor
public class FriendMsgEvent {
    private Integer senderId;
    private Integer receiverId;
    private String msg;
}
