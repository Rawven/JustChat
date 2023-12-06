package www.raven.jc.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * user send msg event
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class UserSendMsgEvent {
    private Integer userId;
    private Integer roomId;
    private List<Integer> idsFromRoom;
    private String msg;
}
