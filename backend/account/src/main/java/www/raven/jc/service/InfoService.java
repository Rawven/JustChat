package www.raven.jc.service;

import www.raven.jc.dto.UserInfoDTO;

import java.util.List;

/**
 * info service
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
public interface InfoService {
    /**
     * query single info
     *
     * @param userId user id
     * @return {@link UserInfoDTO}
     */
    UserInfoDTO querySingleInfo(Integer userId);

    /**
     * query all info
     *
     * @return {@link List}<{@link UserInfoDTO}>
     */
    List<UserInfoDTO> queryAllInfo();
}
