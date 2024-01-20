package www.raven.jc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import www.raven.jc.annotions.Auth;
import www.raven.jc.client.IpfsClient;
import www.raven.jc.constant.RoleConstant;
import www.raven.jc.dto.QueryUserInfoDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.vo.InfoVO;
import www.raven.jc.entity.vo.RealAllInfoVO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.NoticeService;
import www.raven.jc.service.UserService;

import javax.servlet.http.HttpServletRequest;
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
    private NoticeService noticeService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private IpfsClient ipfsClient;


    /**
     * check user exit
     *
     * @param username username
     * @return {@link CommonResult}<{@link Boolean}>
     */
    @PostMapping("/checkUserExit")
    CommonResult<Boolean> checkUserExit(@RequestBody String username) {
        return CommonResult.operateSuccess("查找成功", userService.checkUserExit(username));
    }
    /**
     * user logout
     *
     * @param userId user id
     * @return {@link CommonResult}<{@link Void}>
     */
    @PostMapping("/userLogout")
    CommonResult<Void> saveLogOutTime(@RequestBody Integer userId) {
        userService.saveTime(userId);
        return CommonResult.operateSuccess("登出成功");
    }

    @PostMapping("/setProfile")
    public CommonResult<Void> setProfile(@RequestParam("file") MultipartFile profile) {
        userService.updateByColumn("profile",ipfsClient.upload(profile));
        return CommonResult.operateSuccess("设置头像成功");
    }

    @PostMapping("/setSignature")
    public CommonResult<Void> setSignature(@RequestParam("signature") String signature) {
        userService.updateByColumn("signature",signature);
        return CommonResult.operateSuccess("设置签名成功");
    }

    @PostMapping("/setUsername")
    public CommonResult<Void> setUsername(@RequestParam("username") String username) {
        userService.updateByColumn("username",username);
        return CommonResult.operateSuccess("重命名成功");
    }

    @PostMapping("/defaultInfo")
    public CommonResult<InfoVO> defaultInfo() {
        String userId = request.getHeader("userId");
        return CommonResult.operateSuccess("查找成功", userService.defaultInfo(Integer.parseInt(userId)));
    }

    @PostMapping("/getSingleInfo")
    public CommonResult<UserInfoDTO> getSingleInfo(@RequestBody Integer userId) {
        return CommonResult.operateSuccess("查找成功", userService.querySingleInfo(userId));
    }

    @PostMapping("/getAllInfo")
    public CommonResult<List<UserInfoDTO>> getAllInfo() {
        return CommonResult.operateSuccess("查找成功", userService.queryAllInfo());
    }

    @GetMapping("/admin/queryAllUser/{page}")
    @Auth(value = RoleConstant.ADMIN_ROLE)
    public CommonResult<RealAllInfoVO> queryAllUser(@PathVariable("page") Integer page) {
        return CommonResult.operateSuccess("查找成功", userService.queryPageUser(page));
    }

    @PostMapping("/getBatchInfo")
    CommonResult<List<UserInfoDTO>> getBatchInfo(@RequestBody List<Integer> userIds) {
        return CommonResult.operateSuccess("查找成功", userService.queryBatchInfo(userIds));
    }

    @PostMapping("/getRelatedInfoList")
    public CommonResult<List<UserInfoDTO>> getRelatedInfoList(@RequestBody QueryUserInfoDTO userInfoDTO) {
        return CommonResult.operateSuccess("查找成功", userService.queryLikedInfoList(userInfoDTO.getColumn(), userInfoDTO.getText()));
    }
}