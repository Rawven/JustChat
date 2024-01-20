package www.raven.jc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
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
   //TODO
    @PostMapping("/getFriendList")
    public CommonResult<Void> getFriendList() {
        return CommonResult.operateSuccess("查找成功");
    }

    @PostMapping("/addFriend")
    public CommonResult<Void> addFriend() {
        return CommonResult.operateSuccess("添加成功");
    }


}
