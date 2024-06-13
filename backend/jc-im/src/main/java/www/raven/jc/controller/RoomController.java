package www.raven.jc.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.entity.model.RoomIdModel;
import www.raven.jc.entity.model.RoomModel;
import www.raven.jc.entity.vo.RealRoomVO;
import www.raven.jc.entity.vo.UserFriendVO;
import www.raven.jc.entity.vo.UserRoomVO;
import www.raven.jc.result.HttpResult;
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
    private UserRpcService userRpcService;

    //TODO 分页 懒得实现
    @GetMapping("/initUserMainPage")
    public HttpResult<List<UserRoomVO>> initUserMainPage() {
        return HttpResult.operateSuccess("获取房间列表成功", roomService.initUserMainPage());
    }

    //TODO 分页 懒得实现
    @GetMapping("/initUserFriendPage")
    public HttpResult<List<UserFriendVO>> initUserFriendPage() {
        return HttpResult.operateSuccess("获取好友列表成功", friendService.initUserFriendPage());
    }

    @GetMapping("/queryIdRoomList/{page}/{size}")
    public HttpResult<RealRoomVO> queryRoomList(
        @PathVariable("page") @NotNull int page,
        @PathVariable("size") @NotNull int size) {
        return HttpResult.operateSuccess("获取房间列表成功", roomService.queryListPage(page, size));
    }

    @GetMapping("/queryRelatedRoomList/{text}/{choice}/{page}")
    public HttpResult<RealRoomVO> queryRelatedRoomList(
        @PathVariable("text") @NotBlank String text,
        @PathVariable("choice") @NotBlank int choice,
        @PathVariable("page") @NotBlank int page) {
        RealRoomVO rooms;
        //roomName查找
        if (choice == 1) {
            rooms = roomService.queryLikedRoomList("room_name", text, page);
        } else {
            //userName查找
            rooms = roomService.queryUserNameRoomList("username", text, page);
        }
        return HttpResult.operateSuccess("获取房间列表成功", rooms);
    }

    @PostMapping("/createRoom")
    public HttpResult<Void> createRoom(
        @RequestBody @Validated RoomModel roomModel) {
        roomService.createRoom(roomModel);
        return HttpResult.operateSuccess("创建房间成功");
    }

    @PostMapping("/applyToJoinRoom")
    public HttpResult<Void> applyToJoinRoom(
        @RequestBody @Validated RoomIdModel roomId) {
        roomService.applyToJoinRoom(roomId.getRoomId());
        return HttpResult.operateSuccess("申请加入房间成功");
    }

    @GetMapping("/agreeToJoinRoom/{roomId}/{userId}/{noticeId}")
    public HttpResult<Void> agreeApply(
        @PathVariable("roomId") @NotBlank String roomId,
        @PathVariable("userId") @NotBlank int userId,
        @PathVariable("noticeId") @NotBlank int noticeId) {
        roomService.agreeApply(Integer.valueOf(roomId), userId, noticeId);
        return HttpResult.operateSuccess("同意申请成功");
    }

    /**
     * refuse apply
     *
     * @param noticeId notice id
     * @return {@link HttpResult}<{@link Void}>
     */
    @GetMapping("/refuseToJoinRoom/{roomId}/{userId}/{noticeId}")
    public HttpResult<Void> refuseApply(
        @PathVariable("noticeId") int noticeId,
        @PathVariable("roomId") String roomId,
        @PathVariable("userId") int userId) {
        roomService.refuseApply(Integer.valueOf(roomId), userId, noticeId);
        return HttpResult.operateSuccess("拒绝申请成功");
    }

}
