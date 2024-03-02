package www.raven.jc.event;
import www.raven.jc.entity.po.Message;
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
public class SaveMsgEvent {
    private Message message;
    private String type;
}
