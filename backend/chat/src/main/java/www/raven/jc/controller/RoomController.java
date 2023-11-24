package www.raven.jc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import www.raven.jc.entity.model.RoomModel;
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

    @GetMapping("/getRoomList")
    public CommonResult<List<RoomVO>> getRoomList(@RequestBody Integer page) {
        return CommonResult.operateSuccess("获取房间列表成功",roomService.queryRoomPage(page));
    }


    @PostMapping("/createRoom")
    public CommonResult<Void> createRoom(@RequestBody RoomModel roomModel) {
         roomService.createRoom(roomModel);
         return CommonResult.operateSuccess("创建房间成功");
    }
}
