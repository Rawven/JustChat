package www.raven.jc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import www.raven.jc.dto.QueryUserInfoDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.vo.InfoVO;
import www.raven.jc.result.CommonResult;
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
public class CommonController {
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
    CommonResult<Boolean> checkUserExit(@RequestBody String username) {
        return CommonResult.operateSuccess("查找成功", userService.checkUserExit(username));
    }

    @PostMapping("/setProfile")
    public CommonResult<Void> setProfile(@RequestParam("file") MultipartFile profile) {
        userService.setProfile(profile);
        return CommonResult.operateSuccess("设置头像成功");
    }

    @PostMapping("/setSignature")
    public CommonResult<Void> setSignature(@RequestParam("signature") String signature) {
        userService.setSignature(signature);
        return CommonResult.operateSuccess("设置签名成功");
    }

    @PostMapping("/setUsername")
    public CommonResult<Void> setUsername(@RequestParam("username") String username) {
        userService.setUsername(username);
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

    @PostMapping("/common/getBatchInfo")
    CommonResult<List<UserInfoDTO>> getBatchInfo(List<Integer> userIds) {
        return CommonResult.operateSuccess("查找成功", userService.queryBatchInfo(userIds));
    }

    @PostMapping("/getRelatedInfoList")
    public CommonResult<List<UserInfoDTO>> getRelatedInfoList(@RequestBody QueryUserInfoDTO userInfoDTO) {
        return CommonResult.operateSuccess("查找成功", userService.queryLikedInfoList(userInfoDTO.getColumn(), userInfoDTO.getText()));
    }
}
