package www.raven.jc.ws;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WsMsg {
    private String message;
    private List<Integer> to;
}
