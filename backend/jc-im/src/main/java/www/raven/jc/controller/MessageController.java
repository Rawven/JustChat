package www.raven.jc.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.entity.model.LatestFriendMsgModel;
import www.raven.jc.entity.model.LatestGroupMsgModel;
import www.raven.jc.entity.model.PageGroupMsgModel;
import www.raven.jc.entity.model.PagesFriendMsgModel;
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

    //TODO 离线消息
    @PostMapping("/getLatestRoomHistory")
    public CommonResult<List<MessageVO>> getLatestRoomMsg(@RequestBody @Validated LatestGroupMsgModel model) {
        return CommonResult.operateSuccess("获取最新群聊信息成功", roomService.getLatestGroupMsg(model));
    }

    @PostMapping("/getLatestFriendHistory")
    public CommonResult<List<MessageVO>> getLatestFriendMsg(@RequestBody @Validated LatestFriendMsgModel model) {
        return CommonResult.operateSuccess("获取最新私聊信息成功", friendService.getLatestFriendMsg(model));
    }

    @PostMapping("/queryRoomMsgPages")
    public CommonResult<List<MessageVO>> getGroupMsgHistory(@RequestBody @Validated PageGroupMsgModel model) {
        return CommonResult.operateSuccess("获取历史群聊信息成功", roomService.getGroupMsgPages(model));
    }

    @PostMapping("/queryFriendMsgPages")
    public CommonResult<List<MessageVO>> getFriendMsgHistory(@RequestBody @Validated PagesFriendMsgModel model) {
        return CommonResult.operateSuccess("获取历史私聊信息成功", friendService.getFriendMsgPages(model));
    }
}
