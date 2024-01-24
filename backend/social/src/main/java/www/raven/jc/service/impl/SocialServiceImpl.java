package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.stereotype.Service;
import www.raven.jc.api.UserDubbo;
import www.raven.jc.constant.MqConstant;
import www.raven.jc.dao.MomentDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.model.CommentModel;
import www.raven.jc.entity.model.MomentModel;
import www.raven.jc.entity.po.Comment;
import www.raven.jc.entity.po.Like;
import www.raven.jc.entity.po.Moment;
import www.raven.jc.entity.vo.MomentVO;
import www.raven.jc.event.MomentReleaseEvent;
import www.raven.jc.result.RpcResult;
import www.raven.jc.service.SocialService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * social serivce impl
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@Service
public class SocialServiceImpl implements SocialService {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private MomentDAO momentDAO;
    @DubboReference(interfaceClass = UserDubbo.class, version = "1.0.0", timeout = 15000)
    private UserDubbo userDubbo;
    @Autowired
    private StreamBridge streamBridge;
    @Override
    public void releaseMoment(MomentModel model) {
        String userId = request.getHeader("userId");
        RpcResult<UserInfoDTO> singleInfo = userDubbo.getSingleInfo(Integer.valueOf(userId));
        Assert.isTrue(singleInfo.isSuccess(), "获取用户信息失败");
        UserInfoDTO data = singleInfo.getData();
        Moment moment = new Moment()
                .setUserInfo(data)
                .setImg(model.getImg())
                .setContent(model.getText())
                .setTimestamp(System.currentTimeMillis());
        Assert.isTrue(momentDAO.save(moment), "发布失败");
        MomentReleaseEvent momentReleaseEvent = new MomentReleaseEvent();
        streamBridge.send("producer-out-0", MqUtil.createMsg(JsonUtil.objToJson(momentReleaseEvent), MqConstant.TAGS_MOMENT_RELEASE_RECORD));
    }

    @Override
    public void deleteMoment(String momentId) {
        Assert.isTrue(momentDAO.delete(momentId));
    }

    @Override
    public void likeMoment(String momentId) {
        String userId = request.getHeader("userId");
        RpcResult<UserInfoDTO> singleInfo = userDubbo.getSingleInfo(Integer.valueOf(userId));
        Assert.isTrue(singleInfo.isSuccess(), "获取用户信息失败");
        UserInfoDTO data = singleInfo.getData();
        Like like = new Like().setTimestamp(System.currentTimeMillis()).setUserInfo(data)
                .setTimestamp(System.currentTimeMillis());
        Assert.isTrue(momentDAO.like(momentId, like), "点赞失败");
    }

    @Override
    public void commentMoment(CommentModel model) {
        String userId = request.getHeader("userId");
        RpcResult<UserInfoDTO> singleInfo = userDubbo.getSingleInfo(Integer.valueOf(userId));
        Assert.isTrue(singleInfo.isSuccess(), "获取用户信息失败");
        UserInfoDTO data = singleInfo.getData();
        Comment comment = new Comment().setTimestamp(System.currentTimeMillis())
                .setUserInfo(data)
                .setContent(model.getText());
        Assert.isTrue(momentDAO.comment(model.getMomentId(), comment), "评论失败");

    }

    @Override
    public List<MomentVO> queryMoment() {
        String userId = request.getHeader("userId");
        RpcResult<List<UserInfoDTO>> friendInfos = userDubbo.getFriendAndMeInfos(Integer.parseInt(userId));
        Assert.isTrue(friendInfos.isSuccess(), "获取好友信息失败");
        List<Moment> moments = momentDAO.queryMoment(friendInfos.getData());
        return moments.stream().map(moment -> {
            MomentVO vo = new MomentVO();
            vo.setMomentId(moment.getMomentId().toHexString());
            vo.setUserInfo(moment.getUserInfo());
            vo.setContent(moment.getContent());
            vo.setImg(moment.getImg());
            vo.setTimestamp(moment.getTimestamp());
            vo.setComments(moment.getComments());
            vo.setLikes(moment.getLikes());
            return vo;
        }).collect(Collectors.toList());
    }
}
