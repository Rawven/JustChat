package www.raven.jc.ws;

import lombok.Data;

import javax.websocket.Session;

/**
 * base handler
 *
 * @author 刘家辉
 * @date 2024/01/21
 */
@Data
public class BaseHandler {
    /**
     * user id
     * 对应是哪个用户
     */
    protected Integer userId;

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     **/
    protected Session session;

    protected long lastActivityTime = System.currentTimeMillis();
}
