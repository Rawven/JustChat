package www.raven.jc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.entity.vo.MessageVO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.ChatService;

import java.util.List;

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

    @PostMapping("/restoreHistory")
    public CommonResult<List<MessageVO>> restoreHistory() {
        return CommonResult.operateSuccess("获取历史记录成功", chatService.restoreHistory());
    }
}
