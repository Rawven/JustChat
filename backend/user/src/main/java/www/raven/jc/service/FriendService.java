package www.raven.jc.service;

import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.result.RpcResult;

import java.util.List;

/**
 * friend service
 *
 * @author 刘家辉
 * @date 2024/01/20
 */
public interface FriendService {

    /**
     * get friend infos
     *
     * @param userId user id
     * @return {@link List}<{@link UserInfoDTO}>
     */
    List<UserInfoDTO> getFriendInfos(int userId);


}
