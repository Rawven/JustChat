package www.raven.jc.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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
import www.raven.jc.result.HttpResult;
import www.raven.jc.service.UserService;
import www.raven.jc.util.RequestUtil;

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
     * @return {@link HttpResult}<{@link Boolean}>
     */
    @PostMapping("/checkUserExit")
    HttpResult<Boolean> checkUserExit(
        @RequestBody @NotBlank String username) {
        return HttpResult.operateSuccess("查找成功", userService.checkUserExit(username));
    }

    /**
     * user logout
     *
     * @param userId user id
     * @return {@link HttpResult}<{@link Void}>
     */
    @PostMapping("/userLogout")
    HttpResult<Void> saveLogOutTime(@RequestBody @NotNull Integer userId) {
        userService.saveTime(userId);
        return HttpResult.operateSuccess("登出成功");
    }

    @PostMapping("/setProfile")
    public HttpResult<Void> setProfile(
        @RequestParam("profile") @NotNull String profile) {
        int userId = RequestUtil.getUserId(request);
        userService.updateByColumn(userId, "profile", profile);
        return HttpResult.operateSuccess("设置头像成功");
    }

    @PostMapping("/setSignature")
    public HttpResult<Void> setSignature(
        @RequestParam("signature") @NotNull String signature) {
        int userId = RequestUtil.getUserId(request);
        userService.updateByColumn(userId, "signature", signature);
        return HttpResult.operateSuccess("设置签名成功");
    }

    @PostMapping("/setUsername")
    public HttpResult<Void> setUsername(
        @RequestParam("username") @NotNull String username) {
        int userId = RequestUtil.getUserId(request);
        userService.updateByColumn(userId, "username", username);
        return HttpResult.operateSuccess("重命名成功");
    }

    @PostMapping("/defaultInfo")
    public HttpResult<UserInfoDTO> defaultInfo() {
        int userId = RequestUtil.getUserId(request);
        return HttpResult.operateSuccess("查找成功", userService.querySingleInfo(userId));
    }

    @PostMapping("/getSingleInfo")
    public HttpResult<UserInfoDTO> getSingleInfo(
        @RequestBody @NotNull Integer userId) {
        return HttpResult.operateSuccess("查找成功", userService.querySingleInfo(userId));
    }

    @PostMapping("/getAllInfo")
    public HttpResult<List<UserInfoDTO>> getAllInfo() {
        return HttpResult.operateSuccess("查找成功", userService.queryAllInfo());
    }

    @PostMapping("/getBatchInfo")
    HttpResult<List<UserInfoDTO>> getBatchInfo(
        @RequestBody List<Integer> userIds) {
        return HttpResult.operateSuccess("查找成功", userService.queryBatchInfo(userIds));
    }

    @PostMapping("/getRelatedInfoList")
    public HttpResult<List<UserInfoDTO>> getRelatedInfoList(
        @RequestBody @Validated QueryUserInfoDTO userInfoDTO) {
        return HttpResult.operateSuccess("查找成功", userService.queryLikedInfoList(userInfoDTO.getColumn(), userInfoDTO.getText()));
    }
}
