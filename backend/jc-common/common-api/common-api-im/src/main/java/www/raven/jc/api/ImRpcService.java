package www.raven.jc.api;

import www.raven.jc.result.RpcResult;

/**
 * im dubbo
 *
 * @author 刘家辉
 * @date 2024/02/29
 */
public interface ImRpcService {
    /**
     * delete notification
     *
     * @param id id
     * @return {@link RpcResult}<{@link Void}>
     */
    RpcResult<Void> deleteNotification(int id);
}
