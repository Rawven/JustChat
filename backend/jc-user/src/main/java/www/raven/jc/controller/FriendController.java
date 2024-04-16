package www.raven.jc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.FriendService;

import jakarta.validation.constraints.NotNull;


/**
 * friend controller
 *
 * @author 刘家辉
 * @date 2024/01/20
 */
@RestController
@ResponseBody
@RequestMapping("/friend")
@Slf4j
public class FriendController {
    @Autowired
    private FriendService friendService;

    @GetMapping("/agreeToBeFriend/{friendId}/{noticeId}")
    public CommonResult<Void> agreeApplyFriend(@PathVariable("friendId") @NotNull int friendId,
                                               @PathVariable("noticeId") @NotNull int noticeId) {
        friendService.agreeApplyFromFriend(friendId, noticeId);
        return CommonResult.operateSuccess("成为好友成功");
    }

    @GetMapping("/refuseToBeFriend/{friendId}/{noticeId}")
    public CommonResult<Void> refuseApplyFriend(@PathVariable("friendId") int friendId,
                                                @PathVariable("noticeId") @NotNull int noticeId) {
        friendService.refuseApplyFromFriend(noticeId);
        return CommonResult.operateSuccess("拒绝好友成功");
    }

}
