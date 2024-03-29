package www.raven.jc.controller;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.entity.vo.NoticeVO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.NoticeService;

/**
 * notice controller
 *
 * @author 刘家辉
 * @date 2023/12/16
 */
@RestController
@ResponseBody
@RequestMapping("/notice")
@Slf4j
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    @GetMapping("/getNotice")
    public CommonResult<List<NoticeVO>> getNotice() {
        return CommonResult.operateSuccess("查找成功", noticeService.loadNotice());
    }

    @GetMapping("/addFriendApply/{friendName}")
    public CommonResult<Void> addFriendApply(@PathVariable("friendName") @NotNull String friendName) {
        noticeService.addFriendApply(friendName);
        return CommonResult.operateSuccess("添加好友申请成功");
    }
}
