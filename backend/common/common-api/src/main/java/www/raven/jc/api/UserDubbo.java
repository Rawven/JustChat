package www.raven.jc.api;

import www.raven.jc.dto.*;
import www.raven.jc.result.CommonResult;
import www.raven.jc.result.RpcResult;

import java.util.List;

/**
 * account api
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
public interface UserDubbo {
    /**
     * get single info
     *
     * @param userId user id
     * @return {@link CommonResult}<{@link UserInfoDTO}>
     */
    RpcResult<UserInfoDTO> getSingleInfo(Integer userId);

    /**
     * get all info
     *
     * @return {@link CommonResult}<{@link List}<{@link UserInfoDTO}>>
     */
    RpcResult<List<UserInfoDTO>> getAllInfo();


    /**
     * get related info list
     *
     * @param userInfoDTO user info dto
     * @return {@link CommonResult}<{@link List}<{@link UserInfoDTO}>>
     */
    RpcResult<List<UserInfoDTO>> getRelatedInfoList(QueryUserInfoDTO userInfoDTO);

    /**
     * insert
     *
     * @param user user
     * @return {@link CommonResult}<{@link UserAuthDTO}>
     */
    RpcResult<UserAuthDTO> insert(UserRegisterDTO user);

    /**
     * get user to auth
     *
     * @param username username
     * @return {@link CommonResult}<{@link UserAuthDTO}>
     */
    RpcResult<UserAuthDTO> getUserToAuth(String username);

    /**
     * get roles by id
     *
     * @param userId user id
     * @return {@link CommonResult}<{@link List}<{@link RoleDTO}>>
     */
    RpcResult<List<RoleDTO>> getRolesById(Integer userId);

    /**
     * check user exit
     *
     * @param username username
     * @return {@link CommonResult}<{@link Boolean}>
     */
    RpcResult<Boolean> checkUserExit(String username);


    /**
     * get batch info
     *
     * @param userIds user ids
     * @return {@link CommonResult}<{@link List}<{@link UserInfoDTO}>>
     */
    RpcResult<List<UserInfoDTO>> getBatchInfo(List<Integer> userIds);

    /**
     * user logout
     *
     * @param userId user id
     * @return {@link CommonResult}<{@link Void}>
     */
    RpcResult<Void> saveLogOutTime(Integer userId);

    /**
     * get friend ids
     *
     * @param userId user id
     * @return {@link RpcResult}<{@link List}<{@link Integer}>>
     */
    RpcResult<List<UserInfoDTO>> getFriendInfos(int userId);

    /**
     * delete notice
     *
     * @param noticeId notice id
     * @return {@link RpcResult}<{@link Boolean}>
     */
    RpcResult<Void> deleteNotice(int noticeId);
}