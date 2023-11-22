package www.raven.jc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.ChatService;

/**
 * chat controller
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@RestController
@ResponseBody
public class ChatController {
    @Autowired
    private ChatService chatService;

    @PostMapping("/sendMsg")
    public CommonResult<Void> sendMsg() {
        chatService.sendMsg();
        return CommonResult.operateSuccess("发送信息成功");
    }
}
