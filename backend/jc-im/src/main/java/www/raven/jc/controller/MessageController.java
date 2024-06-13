package www.raven.jc.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.entity.model.PageGroupMsgModel;
import www.raven.jc.entity.model.PagesFriendMsgModel;
import www.raven.jc.entity.po.MessageReadAck;
import www.raven.jc.entity.vo.MessageVO;
import www.raven.jc.result.HttpResult;
import www.raven.jc.service.FriendService;
import www.raven.jc.service.MessageService;
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
    private MessageService messageService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private FriendService friendService;

    @GetMapping("/getLatestOffline")
    public HttpResult<List<MessageVO>> getLatestOfflineMsg() {
        return HttpResult.operateSuccess("获取最新离线信息成功", messageService.getLatestOffline());
    }

    @PostMapping("/queryRoomMsgPages")
    public HttpResult<List<MessageVO>> getGroupMsgHistory(
        @RequestBody @Validated PageGroupMsgModel model) {
        return HttpResult.operateSuccess("获取历史群聊信息成功", roomService.getGroupMsgPages(model));
    }

    @PostMapping("/queryFriendMsgPages")
    public HttpResult<List<MessageVO>> getFriendMsgHistory(
        @RequestBody @Validated PagesFriendMsgModel model) {
        return HttpResult.operateSuccess("获取历史私聊信息成功", friendService.getFriendMsgPages(model));
    }

    /**
     * 供轮询拉取的已读回执接口
     *
     * @return {@link HttpResult }<{@link List }<{@link MessageReadAck }>>
     */
    @GetMapping("/getReadMessageAck")
    public HttpResult<List<MessageReadAck>> getReadMessageAck() {
        return HttpResult.operateSuccess("获取消息已读状态成功", messageService.getReadMessageAck());
    }
}
