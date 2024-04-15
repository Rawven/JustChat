package www.raven.jc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.dto.QueryUserInfoDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.UserService;
import www.raven.jc.util.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * info controller
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@RestController
@ResponseBody
@RequestMapping("/common")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private HttpServletRequest request;

    /**
     * check user exit
     *
     * @param username username
     * @return {@link CommonResult}<{@link Boolean}>
     */
    @PostMapping("/checkUserExit")
    CommonResult<Boolean> checkUserExit(@RequestBody @NotBlank String username) {
        return CommonResult.operateSuccess("查找成功", userService.checkUserExit(username));
    }

    /**
     * user logout
     *
     * @param userId user id
     * @return {@link CommonResult}<{@link Void}>
     */
    @PostMapping("/userLogout")
    CommonResult<Void> saveLogOutTime(@RequestBody @NotNull Integer userId) {
        userService.saveTime(userId);
        return CommonResult.operateSuccess("登出成功");
    }

    @PostMapping("/setProfile")
    public CommonResult<Void> setProfile(@RequestParam("profile") @NotNull String profile) {
        int userId = RequestUtil.getUserId(request);
        userService.updateByColumn(userId, "profile", profile);
        return CommonResult.operateSuccess("设置头像成功");
    }

    @PostMapping("/setSignature")
    public CommonResult<Void> setSignature(@RequestParam("signature") @NotNull String signature) {
        int userId = RequestUtil.getUserId(request);
        userService.updateByColumn(userId, "signature", signature);
        return CommonResult.operateSuccess("设置签名成功");
    }

    @PostMapping("/setUsername")
    public CommonResult<Void> setUsername(@RequestParam("username") @NotNull String username) {
        int userId = RequestUtil.getUserId(request);
        userService.updateByColumn(userId, "username", username);
        return CommonResult.operateSuccess("重命名成功");
    }

    @PostMapping("/defaultInfo")
    public CommonResult<UserInfoDTO> defaultInfo() {
        int userId = RequestUtil.getUserId(request);
        return CommonResult.operateSuccess("查找成功", userService.querySingleInfo(userId));
    }

    @PostMapping("/getSingleInfo")
    public CommonResult<UserInfoDTO> getSingleInfo(@RequestBody @NotNull Integer userId) {
        return CommonResult.operateSuccess("查找成功", userService.querySingleInfo(userId));
    }

    @PostMapping("/getAllInfo")
    public CommonResult<List<UserInfoDTO>> getAllInfo() {
        return CommonResult.operateSuccess("查找成功", userService.queryAllInfo());
    }


    @PostMapping("/getBatchInfo")
    CommonResult<List<UserInfoDTO>> getBatchInfo(@RequestBody List<Integer> userIds) {
        return CommonResult.operateSuccess("查找成功", userService.queryBatchInfo(userIds));
    }

    @PostMapping("/getRelatedInfoList")
    public CommonResult<List<UserInfoDTO>> getRelatedInfoList(@RequestBody @Validated QueryUserInfoDTO userInfoDTO) {
        return CommonResult.operateSuccess("查找成功", userService.queryLikedInfoList(userInfoDTO.getColumn(), userInfoDTO.getText()));
    }
}
