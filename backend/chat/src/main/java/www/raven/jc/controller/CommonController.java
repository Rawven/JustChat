package www.raven.jc.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import www.raven.jc.api.UserDubbo;
import www.raven.jc.entity.model.RoomIdModel;
import www.raven.jc.entity.model.RoomModel;
import www.raven.jc.result.CommonResult;
import www.raven.jc.result.RpcResult;
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
    @DubboReference(interfaceClass = UserDubbo.class, version = "1.0.0", timeout = 15000)
    private UserDubbo userDubbo;


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
    public CommonResult<Void> agreeApply(@PathVariable("roomId") String roomId, @PathVariable("userId") int userId,@PathVariable("noticeId") int noticeId) {
        roomService.agreeApply(Integer.valueOf(roomId),userId,noticeId);
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
