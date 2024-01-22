package www.raven.jc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import www.raven.jc.entity.vo.MessageVO;
import www.raven.jc.entity.vo.RealRoomVO;
import www.raven.jc.entity.vo.UserFriendVO;
import www.raven.jc.entity.vo.UserRoomVO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.ChatService;
import www.raven.jc.service.FriendService;
import www.raven.jc.service.RoomService;

import java.util.List;

/**
 * query controller
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
@RestController
@ResponseBody
@RequestMapping("/common")
public class QueryController {
    @Autowired
    private ChatService chatService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private FriendService friendService;

    @GetMapping("/initUserMainPage")
    public CommonResult<List<UserRoomVO>> initUserMainPage() {
        return CommonResult.operateSuccess("获取房间列表成功", roomService.initUserMainPage());
    }

    @GetMapping("/initUserFriendPage")
    public CommonResult<List<UserFriendVO>> initUserFriendPage() {
        return CommonResult.operateSuccess("获取房间列表成功", friendService.initUserFriendPage());
    }
    @GetMapping("/queryIdRoomList/{page}/{size}")
    public CommonResult<RealRoomVO> queryRoomList(@PathVariable("page") int page, @PathVariable("size") int size) {
        return CommonResult.operateSuccess("获取房间列表成功", roomService.queryListPage(page, size));
    }

    @GetMapping("/queryRelatedRoomList/{text}/{choice}/{page}")
    public CommonResult<RealRoomVO> queryRelatedRoomList(@PathVariable("text") String text, @PathVariable("choice") int choice,
                                                         @PathVariable("page") int page) {
        RealRoomVO rooms;
        //roomName查找
        if (choice == 1) {
            rooms = roomService.queryLikedRoomList("room_name", text, page);
        } else {
            //userName查找
            rooms = roomService.queryUserNameRoomList("username", text, page);
        }
        return CommonResult.operateSuccess("获取房间列表成功", rooms);
    }

    @PostMapping("/restoreHistory/{roomId}")
    public CommonResult<List<MessageVO>> restoreHistory(@PathVariable("roomId") Integer roomId) {
        return CommonResult.operateSuccess("获取历史记录成功", chatService.restoreRoomHistory(roomId));
    }

    @PostMapping("/restoreFriendHistory/{friendId}")
    public CommonResult<List<MessageVO>> restoreFriendHistory(@PathVariable("friendId") Integer friendId) {
        return CommonResult.operateSuccess("获取历史记录成功", friendService.restoreFriendHistory(friendId));
    }
}
