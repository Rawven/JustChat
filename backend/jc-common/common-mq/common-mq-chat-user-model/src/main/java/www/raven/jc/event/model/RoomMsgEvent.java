package www.raven.jc.event.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * user send msg event
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class RoomMsgEvent {
    private Integer userId;
    private Integer roomId;
    private List<Integer> idsFromRoom;
    private String msg;
}
