package www.raven.jc.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @PostMapping("/user/getSingleInfo")
    CommonResult<UserInfoDTO> getSingleInfo(Integer userId);

    /**
     * get all info
     *
     * @return {@link CommonResult}<{@link List}<{@link UserInfoDTO}>>
     */
    @PostMapping("/user/getAllInfo")
    CommonResult<List<UserInfoDTO>> getAllInfo();


    /**
     * get related info list
     *
     * @param userInfoDTO user info dto
     * @return {@link CommonResult}<{@link List}<{@link UserInfoDTO}>>
     */
    @PostMapping("/user/getRelatedInfoList")
    CommonResult<List<UserInfoDTO>> getRelatedInfoList(@RequestBody QueryUserInfoDTO userInfoDTO);

    /**
     * get single info by name
     *
     * @param username username
     * @return {@link CommonResult}<{@link UserInfoDTO}>
     */
    @PostMapping("/auth/getSingleInfoByName")
    CommonResult<UserAuthDTO> getUserToAuth(String username);
    //TODO 加路径
    /**
     * get roles by id
     *
     * @param userId user id
     * @return {@link CommonResult}<{@link List}<{@link RoleDTO}>>
     */
    @PostMapping("/auth/getRolesById")
    CommonResult<List<RoleDTO>> getRolesById(Integer userId);

    /**
     * insert
     *
     * @param user user
     * @return {@link CommonResult}<{@link Boolean}>
     */
    @PostMapping("/auth/insert")
    CommonResult<UserAuthDTO> insert(UserRegisterDTO user);
}
