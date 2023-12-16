package www.raven.jc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import www.raven.jc.entity.model.AgreeModel;
import www.raven.jc.entity.model.RoomIdModel;
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
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private RoomService roomService;


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

    @PostMapping("/agreeApply")
    public CommonResult<Void> agreeApply(@RequestBody AgreeModel agreeModel) {
        roomService.agreeApply(agreeModel.getRoomId(), agreeModel.getUserId());
        return CommonResult.operateSuccess("同意申请成功");
    }

}
