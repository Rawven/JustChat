package www.raven.jc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import www.raven.jc.entity.model.RoomModel;
import www.raven.jc.entity.vo.RoomRealVO;
import www.raven.jc.entity.vo.RoomVO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.RoomService;

import java.util.List;

/**
 * room controller
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@RestController
@ResponseBody
public class RoomController {
    @Autowired
    private RoomService roomService;

    @GetMapping("/queryRoomList/{page}")
    public CommonResult<RoomRealVO> queryRoomList(@PathVariable("page") int page) {
        List<RoomVO> rooms = roomService.queryAllRoomPage(page);
        RoomRealVO roomRealVO = new RoomRealVO().setRooms(rooms).setTotal(rooms.size());
        return CommonResult.operateSuccess("获取房间列表成功", roomRealVO);
    }
    @GetMapping("/queryUserNameRelatedRoomList")
    public CommonResult<RoomRealVO> queryUserNameRelatedRoomList(@RequestParam("text") String text) {
        List<RoomVO> rooms = roomService.queryLikedRoomList("username",text);
        RoomRealVO roomRealVO = new RoomRealVO().setRooms(rooms).setTotal(rooms.size());
        return CommonResult.operateSuccess("获取房间列表成功", roomRealVO);
    }

    @GetMapping("/queryRoomNameRelatedRoomList")
    public CommonResult<RoomRealVO> queryRoomNameRelatedRoomList(@RequestParam("text") String text) {
        List<RoomVO> rooms = roomService.queryLikedRoomList("room_name",text);
        RoomRealVO roomRealVO = new RoomRealVO().setRooms(rooms).setTotal(rooms.size());
        return CommonResult.operateSuccess("获取房间列表成功", roomRealVO);
    }


    @PostMapping("/createRoom")
    public CommonResult<Void> createRoom(@RequestBody RoomModel roomModel) {
        roomService.createRoom(roomModel);
        return CommonResult.operateSuccess("创建房间成功");
    }
}
