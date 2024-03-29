package www.raven.jc.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.entity.model.CommentModel;
import www.raven.jc.entity.model.LikeModel;
import www.raven.jc.entity.model.MomentModel;
import www.raven.jc.entity.vo.MomentVO;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.SocialService;
import www.raven.jc.util.RequestUtil;

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
    @Autowired
    private HttpServletRequest request;

    @GetMapping("/queryMoment")
    public CommonResult<List<MomentVO>> queryMoment() {
        return CommonResult.operateSuccess("查询成功", socialService.queryMoment(RequestUtil.getUserId(request)));
    }

    @PostMapping("/releaseMoment")
    public CommonResult<Void> releaseMoment(@RequestBody @Validated MomentModel model) {
        socialService.releaseMoment(model);
        return CommonResult.operateSuccess("发布成功");
    }

    @PostMapping("/deleteMoment/{momentId}")
    public CommonResult<Void> deleteMoment(@PathVariable("momentId") @Validated String momentId) {
        socialService.deleteMoment(momentId);
        return CommonResult.operateSuccess("删除成功");
    }

    @PostMapping("/likeMoment")
    public CommonResult<Void> likeMoment(@RequestBody @Validated LikeModel model) {
        socialService.likeMoment(model);
        return CommonResult.operateSuccess("点赞成功");
    }

    @PostMapping("/commentMoment")
    public CommonResult<Void> commentMoment(@RequestBody @Validated CommentModel model) {
        socialService.commentMoment(model);
        return CommonResult.operateSuccess("评论成功");
    }
}
