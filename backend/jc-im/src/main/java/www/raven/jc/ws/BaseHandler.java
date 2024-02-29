package www.raven.jc.ws;

import javax.websocket.Session;
import lombok.Data;
import www.raven.jc.entity.dto.MessageDTO;

/**
 * base handler
 *
 * @author 刘家辉
 * @date 2024/01/21
 */

public interface BaseHandler {
    /**
     * on message
     *
     * @param message message
     * @param session session
     */
    void onMessage(MessageDTO message,Session session);
}
