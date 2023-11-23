package www.raven.jc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.entity.model.RoomModel;
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
public class RoomController {
    @Autowired
    private RoomService roomService;


    @PostMapping("/createRoom")
    public CommonResult<Void> createRoom(@RequestBody RoomModel roomModel) {
         roomService.createRoom(roomModel);
         return CommonResult.operateSuccess("创建房间成功");
    }
}
