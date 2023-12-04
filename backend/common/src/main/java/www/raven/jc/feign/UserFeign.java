package www.raven.jc.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import www.raven.jc.dto.*;
import www.raven.jc.result.CommonResult;

import java.util.List;

/**
 * account feign
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@FeignClient("JC-User")
public interface UserFeign {
    /**
     * get single info
     *
     * @param userId user id
     * @return {@link CommonResult}<{@link UserInfoDTO}>
     */
    @PostMapping("/common/getSingleInfo")
    CommonResult<UserInfoDTO> getSingleInfo(Integer userId);

    /**
     * get all info
     *
     * @return {@link CommonResult}<{@link List}<{@link UserInfoDTO}>>
     */
    @PostMapping("/common/getAllInfo")
    CommonResult<List<UserInfoDTO>> getAllInfo();


    /**
     * get related info list
     *
     * @param userInfoDTO user info dto
     * @return {@link CommonResult}<{@link List}<{@link UserInfoDTO}>>
     */
    @PostMapping("/common/getRelatedInfoList")
    CommonResult<List<UserInfoDTO>> getRelatedInfoList( QueryUserInfoDTO userInfoDTO);

    /**
     * insert
     *
     * @param user user
     * @return {@link CommonResult}<{@link UserAuthDTO}>
     */
    @PostMapping("/admin/insert")
    CommonResult<UserAuthDTO> insert( UserRegisterDTO user);

    /**
     * get user to auth
     *
     * @param username username
     * @return {@link CommonResult}<{@link UserAuthDTO}>
     */
    @PostMapping("/admin/getUserToAuth")
    CommonResult<UserAuthDTO> getUserToAuth(String username);

    /**
     * get roles by id
     *
     * @param userId user id
     * @return {@link CommonResult}<{@link List}<{@link RoleDTO}>>
     */
    @PostMapping("/admin/getRolesById")
    CommonResult<List<RoleDTO>> getRolesById(Integer userId);

    /**
     * check user exit
     *
     * @param username username
     * @return {@link CommonResult}<{@link Boolean}>
     */
    @PostMapping("/common/checkUserExit")
    CommonResult<Boolean> checkUserExit(String username);
}
