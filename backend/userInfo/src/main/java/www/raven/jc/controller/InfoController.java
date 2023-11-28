package www.raven.jc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import www.raven.jc.dto.QueryUserInfoDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.dto.UserRoleInfo;
import www.raven.jc.entity.vo.InfoVO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.InfoService;

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
@Slf4j
public class InfoController {
    @Autowired
    private InfoService infoService;
    @Autowired
    private HttpServletRequest request;

    @PostMapping("/setProfile")
    public CommonResult<Void> setProfile(@RequestParam("file") MultipartFile profile) {
        infoService.setProfile(profile);
        return CommonResult.operateSuccess("设置头像成功");
    }

    @PostMapping("/setSignature")
    public CommonResult<Void> setSignature(@RequestParam("signature") String signature) {
        infoService.setSignature(signature);
        return CommonResult.operateSuccess("设置签名成功");
    }

    @PostMapping("/setUsername")
    public CommonResult<Void> setUsername(@RequestParam("username") String username) {
        infoService.setUsername(username);
        return CommonResult.operateSuccess("重命名成功");
    }

    @PostMapping("/defaultInfo")
    public CommonResult<InfoVO> defaultInfo() {
        String userId = request.getHeader("userId");
        return CommonResult.operateSuccess("查找成功", infoService.defaultInfo(Integer.parseInt(userId)));
    }

    @PostMapping("/getSingleInfo")
    public CommonResult<UserInfoDTO> getSingleInfo(@RequestBody Integer userId) {
        return CommonResult.operateSuccess("查找成功", infoService.querySingleInfo(userId));
    }

    @PostMapping("/getAllInfo")
    public CommonResult<List<UserInfoDTO>> getAllInfo() {
        return CommonResult.operateSuccess("查找成功", infoService.queryAllInfo());
    }

    @PostMapping("/getRelatedInfoList")
    public CommonResult<List<UserInfoDTO>> getRelatedInfoList(@RequestBody QueryUserInfoDTO userInfoDTO) {
        return CommonResult.operateSuccess("查找成功", infoService.queryLikedInfoList(userInfoDTO.getColumn(), userInfoDTO.getText()));
    }
}
