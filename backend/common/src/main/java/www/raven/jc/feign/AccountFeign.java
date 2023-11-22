package www.raven.jc.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.result.CommonResult;

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
    @GetMapping("/getSingleInfo")
     CommonResult<UserInfoDTO> getSingleInfo(Integer userId);
}
