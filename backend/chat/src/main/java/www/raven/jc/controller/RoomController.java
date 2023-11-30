package www.raven.jc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import www.raven.jc.entity.model.RoomModel;
import www.raven.jc.entity.vo.RealRoomVO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.RoomService;

/**
 * room controller
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@RestController
@ResponseBody
@RequestMapping("/user")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @GetMapping("/queryRoomList/{page}/{size}")
    public CommonResult<RealRoomVO> queryRoomList(@PathVariable("page") int page, @PathVariable("size") int size) {
        return CommonResult.operateSuccess("获取房间列表成功", roomService.queryAllRoomPage(page,size));
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

    @PostMapping("/createRoom")
    public CommonResult<Void> createRoom(@RequestBody RoomModel roomModel) {
        roomService.createRoom(roomModel);
        return CommonResult.operateSuccess("创建房间成功");
    }
}
