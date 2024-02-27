package www.raven.jc.exception;

/**
 * ws收到不应该的消息
 *
 * @author 刘家辉
 * @date 2024/01/22
 */
public class WsAttackException extends RuntimeException {
    public WsAttackException(String message) {
        super(message);
    }
}
