package www.raven.jc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import www.raven.jc.entity.model.CommentModel;
import www.raven.jc.entity.model.MomentModel;
import www.raven.jc.entity.po.Moment;
import www.raven.jc.entity.vo.MomentVO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.SocialService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * social controller
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@RestController
@ResponseBody
public class SocialController {
    @Autowired
    private SocialService socialService;

    @GetMapping("/queryMoment")
    public CommonResult<List<MomentVO>> queryMoment() {
        return CommonResult.operateSuccess("查询成功", socialService.queryMoment());
    }

    @PostMapping("/releaseMoment")
    public CommonResult<Void> releaseMoment(@RequestBody MomentModel model) {
        socialService.releaseMoment(model);
        return CommonResult.operateSuccess("发布成功");
    }

    @GetMapping("/deleteMoment/{momentId}")
    public CommonResult<Void> deleteMoment(@PathVariable("momentId") String momentId) {
        socialService.deleteMoment(momentId);
        return CommonResult.operateSuccess("删除成功");
    }

    @GetMapping("/likeMoment/{momentId}")
    public CommonResult<Void> likeMoment(@PathVariable("momentId") String momentId) {
        socialService.likeMoment(momentId);
        return CommonResult.operateSuccess("点赞成功");
    }
    @PostMapping("/commentMoment")
    public CommonResult<Void> commentMoment(@RequestBody CommentModel model) {
        socialService.commentMoment(model);
        return CommonResult.operateSuccess("评论成功");
    }
}
