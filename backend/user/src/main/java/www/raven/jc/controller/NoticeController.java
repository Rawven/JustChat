package www.raven.jc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import www.raven.jc.entity.model.FriendApplyModel;
import www.raven.jc.entity.vo.NoticeVO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.NoticeService;

import java.util.List;

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

    @GetMapping("/doneNotice/{id}")
    public CommonResult<Void> doneNotice(@PathVariable("id") Integer id) {
        noticeService.doneNotification(id);
        return CommonResult.operateSuccess("处理通知成功");
    }

    @GetMapping("/addFriendApply")
    public CommonResult<Void> addFriendApply(@RequestBody FriendApplyModel friendApplyModel) {
        noticeService.addFriendApply(friendApplyModel.getFriendId().intValue(), friendApplyModel.getMessage());
        return CommonResult.operateSuccess("添加好友申请成功");
    }
}
