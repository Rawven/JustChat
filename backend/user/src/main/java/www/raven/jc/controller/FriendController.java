package www.raven.jc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.FriendService;

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

    @GetMapping("/agreeToBeFriend/{friendId}")
    public CommonResult<Void> agreeApplyFriend(@PathVariable("friendId") int friendId) {
        friendService.agreeApplyFromFriend(friendId);
        return CommonResult.operateSuccess("成为好友成功");
    }


}
