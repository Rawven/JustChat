package www.raven.jc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.entity.vo.AllRoomVO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.RoomService;

import java.util.List;

/**
 * admin controller
 *
 * @author 刘家辉
 * @date 2023/11/29
 */
@RestController
@ResponseBody
@RequestMapping("/admin")
public class AdminController {


    //TODO 修改一下权限角色在token中的存放 变为数组 然后admin主页的user和room的查询都用分页查询 userinfo那边的接口要改一下
    @Autowired
    private RoomService roomService;
    @GetMapping("/queryAllRoom")
    public CommonResult<List<AllRoomVO>>  getAllRoom(){
       //    return CommonResult.operateSuccess("获取所有房间成功",roomService.queryAllRoom());
        return null;
    }
}
