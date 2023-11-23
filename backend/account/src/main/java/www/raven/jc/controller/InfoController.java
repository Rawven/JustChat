package www.raven.jc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.InfoService;

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
public class InfoController {
    @Autowired
    private InfoService infoService;

    @PostMapping("/getSingleInfo")
    public CommonResult<UserInfoDTO> getSingleInfo(@RequestBody Integer userId) {
        return CommonResult.operateSuccess("查找成功",infoService.querySingleInfo(userId));
    }

    @PostMapping("/getAllInfo")
    public CommonResult<List<UserInfoDTO>> getAllInfo() {
        return CommonResult.operateSuccess("查找成功",infoService.queryAllInfo());
    }
}
