package www.raven.jc.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import www.raven.jc.dto.QueryUserInfoDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.result.CommonResult;

import java.util.List;

/**
 * account feign
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@FeignClient("JC-Account")
public interface AccountFeign {
    /**
     * get single info
     *
     * @param userId user id
     * @return {@link CommonResult}<{@link UserInfoDTO}>
     */
    @PostMapping("/getSingleInfo")
    CommonResult<UserInfoDTO> getSingleInfo(Integer userId);

    /**
     * get all info
     *
     * @return {@link CommonResult}<{@link List}<{@link UserInfoDTO}>>
     */
    @PostMapping("/getAllInfo")
    CommonResult<List<UserInfoDTO>> getAllInfo();


    /**
     * get related info list
     * get related info list
     *
     * @param userInfoDTO user info dto
     * @return {@link CommonResult}<{@link List}<{@link UserInfoDTO}>>
     */
    @PostMapping("/getRelatedInfoList")
    public CommonResult<List<UserInfoDTO>> getRelatedInfoList(@RequestBody QueryUserInfoDTO userInfoDTO);
}
