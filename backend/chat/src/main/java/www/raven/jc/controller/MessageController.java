package www.raven.jc.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.entity.vo.MessageVO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.ChatService;
import www.raven.jc.service.FriendService;
import www.raven.jc.service.RoomService;

/**
 * query controller
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
@RestController
@ResponseBody
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private ChatService chatService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private FriendService friendService;

    @PostMapping("/restoreRoomHistory/{roomId}")
    public CommonResult<List<MessageVO>> restoreRoomHistory(@PathVariable("roomId") Integer roomId) {
        return CommonResult.operateSuccess("获取历史记录成功", chatService.restoreRoomHistory(roomId));
    }

    @PostMapping("/restoreFriendHistory/{friendId}")
    public CommonResult<List<MessageVO>> restoreFriendHistory(@PathVariable("friendId") Integer friendId) {
        return CommonResult.operateSuccess("获取历史记录成功", friendService.restoreFriendHistory(friendId));
    }
}
