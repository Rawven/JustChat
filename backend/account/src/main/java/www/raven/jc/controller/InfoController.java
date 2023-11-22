package www.raven.jc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.InfoService;

/**
 * info controller
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@RestController
@ResponseBody
public class InfoController {
    @Autowired
    private InfoService infoService;

    @GetMapping("/getSingleInfo")
    public CommonResult<UserInfoDTO> getSingleInfo(Integer userId) {
        return CommonResult.operateSuccess("查找成功",infoService.querySingleInfo(userId));
    }
}
