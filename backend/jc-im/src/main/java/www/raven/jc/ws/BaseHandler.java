package www.raven.jc.ws;

import www.raven.jc.entity.dto.MessageDTO;

import javax.websocket.Session;

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
    void onMessage(MessageDTO message, Session session);
}
