package www.raven.jc.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.api.UserDubbo;
import www.raven.jc.entity.model.RoomIdModel;
import www.raven.jc.entity.model.RoomModel;
import www.raven.jc.entity.vo.RealRoomVO;
import www.raven.jc.entity.vo.UserFriendVO;
import www.raven.jc.entity.vo.UserRoomVO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.result.RpcResult;
import www.raven.jc.service.FriendService;
import www.raven.jc.service.RoomService;

/**
 * room controller
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@RestController
@ResponseBody
@RequestMapping("/room")
public class RoomController {

    @Autowired
    private RoomService roomService;
    @Autowired
    private FriendService friendService;
    @Autowired
    private UserDubbo userDubbo;

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
    public CommonResult<RealRoomVO> queryRelatedRoomList(@PathVariable("text") String text,
        @PathVariable("choice") int choice,
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

    @PostMapping("/createRoom")
    public CommonResult<Void> createRoom(@RequestBody RoomModel roomModel) {
        roomService.createRoom(roomModel);
        return CommonResult.operateSuccess("创建房间成功");
    }

    @PostMapping("/applyToJoinRoom")
    public CommonResult<Void> applyToJoinRoom(@RequestBody RoomIdModel roomId) {
        roomService.applyToJoinRoom(roomId.getRoomId());
        return CommonResult.operateSuccess("申请加入房间成功");
    }

    @GetMapping("/agreeToJoinRoom/{roomId}/{userId}/{noticeId}")
    public CommonResult<Void> agreeApply(@PathVariable("roomId") String roomId, @PathVariable("userId") int userId,
        @PathVariable("noticeId") int noticeId) {
        roomService.agreeApply(Integer.valueOf(roomId), userId, noticeId);
        return CommonResult.operateSuccess("同意申请成功");
    }

    @GetMapping("/refuseToJoinRoom/{roomId}/{userId}/{noticeId}")
    public CommonResult<Void> refuseApply(@PathVariable("noticeId") int noticeId) {
        RpcResult<Void> voidRpcResult = userDubbo.deleteNotice(noticeId);
        if (!voidRpcResult.isSuccess()) {
            return CommonResult.operateFailure(voidRpcResult.getMessage());
        }
        return CommonResult.operateSuccess("拒绝申请成功");
    }

}
